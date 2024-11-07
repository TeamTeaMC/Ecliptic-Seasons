package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.MapFixerMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

/**
 * <p>如今迁移到服务器上，那么设计也需要更新。
 * 那么当Server区块更新时，首先到这里首先发送到这里区块。
 * 同时客户端也会有一个缓冲计数，这将如何。
 * 总之，真正更新方块状态需要服务器端进行考虑，在服务器端的tick方法进行运行。
 * </p>
 **/
public class ServerMapFixer {


    private static final Map<Level, Map<ChunkPos, List<XZPos>>> CHUNK_POS_XZ_POS_MAP = new IdentityHashMap<>();

    public static Map<ChunkPos, List<XZPos>> getMap(Level level) {
        return CHUNK_POS_XZ_POS_MAP.computeIfAbsent(level, level1 -> new HashMap<>());
    }

    public static void unloadLevel(Level level) {
        getMap(level).clear();
        CHUNK_POS_XZ_POS_MAP.remove(level);
    }

    public static void unloadChunk(Level level, ChunkPos chunkPos) {
        getMap(level).remove(chunkPos);
    }

    public static void unloadBlockPos(Level level, BlockPos blockPos) {
        var orDefault = getMap(level).getOrDefault(new ChunkPos(blockPos), new ArrayList<>());
        for (int i = 0; i < orDefault.size(); i++) {
            XZPos xzPos = orDefault.get(i);
            if (xzPos.x() == blockPos.getX() && xzPos.z() == blockPos.getZ()) {
                orDefault.remove(i);
                i--;
            }
        }
    }

    // 这里指的是先前Y高度
    public static void addPlanner(Level level, BlockState state, BlockState oldState, BlockPos pos, long startTick, int startY, boolean forceClearSnow) {
        if (!MapChecker.isValidDimension(level)) return;

        boolean informClientImmediately = false;
        int newy = level.getMinBuildHeight();
        if (!forceClearSnow) {
            int mcHeight = MapChecker.getMCHeightWithCheck(level, pos);
            // boolean stateChange = state != oldState;
            boolean isNotOldHeight =
                    startY != mcHeight;
            if (isNotOldHeight
                            && (Heightmap.Types.MOTION_BLOCKING.isOpaque().test(state)
                            || state.getBlock() == Blocks.AIR
                    )
                            && EclipticUtil.isHereWithSnow(level, pos)
            ) {
                ChunkPos chunkPos = new ChunkPos(pos);
                List<XZPos> xzPosList = getMap(level).computeIfAbsent(chunkPos, k -> new ArrayList<>());
                xzPosList.add(new XZPos(pos.getX(), pos.getZ(), startTick, startY));
                if (state.getBlock() == Blocks.AIR) {
                    newy = level.getMaxBuildHeight() + 1;
                    informClientImmediately = true;
                }
            } else {
                // 延迟一会再更新覆雪状态
                if (isNotOldHeight) {
                    newy = mcHeight;
                    informClientImmediately = true;
                }
            }
        } else {
            ChunkPos chunkPos = new ChunkPos(pos);
            List<XZPos> xzPosList = getMap(level).computeIfAbsent(chunkPos, k -> new ArrayList<>());
            xzPosList.add(new XZPos(pos.getX(), pos.getZ(), startTick, startY));
            newy = level.getMaxBuildHeight() + 1;
            informClientImmediately = true;
        }

        if (informClientImmediately && level instanceof ServerLevel serverLevel) {
            MapChecker.updatePosForce(level, pos, newy);
            BlockPos nextPos = new BlockPos(pos.getX(), newy, pos.getZ());
            SimpleNetworkHandler.send(serverLevel.players(), new MapFixerMessage(List.of(nextPos), List.of(startY)));
        }
    }

    public static void tick(Level level) {
        if (!MapChecker.isValidDimension(level)) return;

        Map<ChunkPos, List<XZPos>> chunkPosListMap = getMap(level);

        long tick = level.getGameTime();
        List<ChunkPos> removeNeedChunkPosList = new ArrayList<>();
        Set<SectionPos> updateSectionsList = new HashSet<>();
        List<BlockPos> updatePosList = new ArrayList<>();
        List<Integer> oldYs = new ArrayList<>();
        chunkPosListMap.forEach(
                (chunkPos, xzPosList) -> {
                    for (int i = 0; i < xzPosList.size(); i++) {
                        XZPos xzPos = xzPosList.get(i);
                        // 这里需要限制，一次不能刷新太多，不然会超载
                        if (tick - xzPos.startTick() > 160
                                && updateSectionsList.size() < 12
                        ) {
                            var updatePos = new BlockPos.MutableBlockPos(xzPos.x(), xzPos.startY(), xzPos.z());
                            // 不下雪时不增加，延迟等待时间
                            if (
                                    !EclipticUtil.isHereSnowy(level, updatePos)
                                            && EclipticUtil.isHereWithSnow(level, updatePos)
                            ) {
                                xzPos = new XZPos(xzPos.x(), xzPos.z(), tick - 50, xzPos.startY());
                                xzPosList.set(i, xzPos);
                            } else {
                                xzPosList.removeFirst();
                                i--;
                                int newY = MapChecker.getHeightOrUpdate(level, updatePos, true);

                                updatePos.setY(newY);

                                updateSectionsList.add(SectionPos.of(updatePos));
                                updatePosList.add(updatePos);
                                oldYs.add(xzPos.startY());
                            }
                        } else {
                            break;
                        }
                    }

                    if (xzPosList.isEmpty()) {
                        removeNeedChunkPosList.add(chunkPos);
                    }
                }
        );

        for (ChunkPos chunkPos : removeNeedChunkPosList) {
            chunkPosListMap.remove(chunkPos);
        }

        if (level instanceof ServerLevel serverLevel
                && !updatePosList.isEmpty()) {
            sendToAll(serverLevel, updatePosList, oldYs);
            EclipticSeasons.logger(level.getGameTime(), updatePosList);
        }

        // for (SectionPos sectionPos : updateSectionsList) {
        //     Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
        // }
    }


    public static void sendToAll(ServerLevel serverLevel, List<BlockPos> blockPos, List<Integer> startY) {
        SimpleNetworkHandler.send(serverLevel.players(), new MapFixerMessage(blockPos, startY));
    }
}

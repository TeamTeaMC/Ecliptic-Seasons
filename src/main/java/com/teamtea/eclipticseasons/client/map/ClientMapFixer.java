package com.teamtea.eclipticseasons.client.map;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.XZPos;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>这里可能会有复杂的情况，比如说<font color="blue">连续放置方块</font>的时候怎么计算。
 * 但是我们不管这么多，只需要定时刷新即可。
 * 未来也许可以处理连续更新<font color="green">同一xz位置</font>的情况</p>
 * <p>这里有一个新情况是如果是打破的话，就需要set 超高然后再恢复</p>
 * <p>未来需要处理下雪时才设置</p>
 **/
public class ClientMapFixer {


    private static final Long2ObjectOpenHashMap<Long2ObjectLinkedOpenHashMap<XZPos>> CHUNK_POS_XZ_POS_MAP = new Long2ObjectOpenHashMap<>();

    // private static final Map<ChunkPos, List<XZPos>> CHUNK_POS_XZ_POS_MAP = new HashMap<>();

    public static void clearAll() {
        CHUNK_POS_XZ_POS_MAP.clear();
    }

    public static void clearChunk(ChunkPos chunkPos) {
        CHUNK_POS_XZ_POS_MAP.remove(chunkPos.toLong());
    }

    public static void clearBlockPos(BlockPos blockPos) {
        var orDefault = CHUNK_POS_XZ_POS_MAP
                .computeIfAbsent(new ChunkPos(blockPos).toLong(), a -> new Long2ObjectLinkedOpenHashMap<>());

        var iterator = orDefault.long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            XZPos xzPos = entry.getValue();
            if (xzPos.x() == blockPos.getX() && xzPos.z() == blockPos.getZ()) {
                iterator.remove();
            }
        }
    }

    public static void addLightPlanner(ClientLevel level, long packedPos, int brightness) {
        if (CommonConfig.isSnowyWinter()
                && ClientConfig.Renderer.realisticSnowyChange.get()
                && CommonConfig.Season.notSnowyNearGlowingBlock.get()
        ) {
            // int brightness = level.getBrightness(LightLayer.BLOCK, pos);
            boolean isTooLight = brightness >= CommonConfig.Season.notSnowyNearGlowingBlockLevel.get();

            if (isTooLight) {
                BlockPos pos = new BlockPos(BlockPos.getX(packedPos), BlockPos.getY(packedPos), BlockPos.getZ(packedPos));
                long startTick = level.getGameTime();
                pos = pos.below();
                BlockState state = level.getBlockState(pos);
                boolean isOldHeight = pos.getY() == agentGetLevelHeight(level, pos);
                if (solidTest(state)
                        && isOldHeight
                        && MapColorReplacer.getTopSnowColor(level, state, pos, true) != null
                ) {
                    ChunkPos chunkPos = new ChunkPos(pos);
                    Long2ObjectLinkedOpenHashMap<XZPos> xzPosList = CHUNK_POS_XZ_POS_MAP.computeIfAbsent(chunkPos.toLong(), k -> new Long2ObjectLinkedOpenHashMap<>());
                    XZPos xzPos = new XZPos(pos.getX(), pos.getZ(), startTick, pos.getY());
                    long longKey = xzPos.toLongKey();
                    xzPosList.remove(longKey);
                    xzPosList.put(longKey, xzPos);
                    MapChecker.updatePosForce(level, pos, level.getMaxBuildHeight() + 1);
                }
            }
        }
    }

    public static int agentGetLevelHeight(Level level, BlockPos pos) {
        // return level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
        ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, pos);
        Integer old = null;
        if (chunkMap != null) {
            old = chunkMap.getHeight(pos);
            if (old > level.getMaxBuildHeight()) return old;
        }
        return MapChecker.getMCHeightWithCheck(level, pos, old);
    }

    public static boolean solidTest(BlockState state) {
        return Heightmap.Types.MOTION_BLOCKING.isOpaque().test(state)
                && !MapChecker.extraSnowPassable(state);
    }

    public static void addPlanner(Level level, BlockState state, BlockPos pos, long startTick, int startY) {
        if (ClientConfig.Renderer.realisticSnowyChange.get()) {
            boolean isNotOldHeight = startY != agentGetLevelHeight(level, pos);
            if (((solidTest(state))
                    || state.getBlock() == Blocks.AIR
            )
                    && isNotOldHeight
                    && EclipticUtil.isHereWithSnow(level, pos)) {
                ChunkPos chunkPos = new ChunkPos(pos);
                Long2ObjectLinkedOpenHashMap<XZPos> xzPosList = CHUNK_POS_XZ_POS_MAP.computeIfAbsent(chunkPos.toLong(), k -> new Long2ObjectLinkedOpenHashMap<>());
                startY = level.getMaxBuildHeight() + 1;
                XZPos xzPos = new XZPos(pos.getX(), pos.getZ(), startTick, startY);
                long longKey = xzPos.toLongKey();
                xzPosList.remove(longKey);
                xzPosList.put(longKey, xzPos);
                // if (state.getBlock() == Blocks.AIR) {
                //     MapChecker.updatePosForce(level, pos, level.getMaxBuildHeight() + 1);
                // }
                MapChecker.updatePosForce(level, pos, startY);
            } else {
                if (isNotOldHeight) {
                    MapChecker.getHeightOrUpdate(level, pos, true);
                }
            }
        } else {
            MapChecker.getHeightOrUpdate(level, pos, true);
        }
    }

    public static void tick(Level level) {
        long tick = level.getGameTime();
        List<ChunkPos> removeNeedChunkPosList = new ArrayList<>();
        Set<SectionPos> updateSectionsList = new HashSet<>();
        CHUNK_POS_XZ_POS_MAP.forEach(
                (chunkPos, xzPosList) -> {
                    var iterator = xzPosList.long2ObjectEntrySet().iterator();
                    List<XZPos> xzPosToReAdd = null;
                    while (iterator.hasNext()) {
                        var entry = iterator.next();
                        XZPos xzPos = entry.getValue();
                        if (tick - xzPos.startTick() > 160
                                && updateSectionsList.size() < 12
                        ) {
                            var updatePos = new BlockPos.MutableBlockPos(xzPos.x(), xzPos.startY(), xzPos.z());
                            if (
                                    !EclipticUtil.isHereSnowy(level, updatePos)
                                            // (isHereSunny(level, updatePos))
                                            // || isHereRainy(level, updatePos)
                                            && EclipticUtil.isHereWithSnow(level, updatePos)
                            ) {
                                iterator.remove();

                                if (xzPosToReAdd == null) xzPosToReAdd = new ArrayList<>();
                                {
                                    xzPosToReAdd.add(new XZPos(xzPos.x(), xzPos.z(), level.getGameTime() - 50, level.getMaxBuildHeight() + 1));
                                }
                                MapChecker.updatePosForce(level, updatePos, xzPos.startY());
                                var sectionPos = SectionPos.of(updatePos);
                                updateSectionsList.add(sectionPos);
                            } else {
                                // xzPosList.removeFirst();
                                // i--;
                                iterator.remove();
                                int y = MapChecker.getHeightOrUpdate(level, updatePos, true);
                                // if (y != xzPos.startY())
                                {
                                    updatePos.setY(y);
                                    var sectionPos = SectionPos.of(updatePos);
                                    updateSectionsList.add(sectionPos);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    if (xzPosToReAdd != null) {
                        for (XZPos xzPos : xzPosToReAdd) {
                            xzPosList.put(xzPos.toLongKey(), xzPos);
                        }
                    }
                    // for (int i = 0; i < xzPosList.size(); i++) {
                    //     XZPos xzPos = xzPosList.get(i);
                    //     // 这里需要限制，一次不能刷新太多，不然会超载
                    //     if (tick - xzPos.startTick() > 160
                    //             && updateSectionsList.size() < 12
                    //     ) {
                    //         var updatePos = new BlockPos.MutableBlockPos(xzPos.x(), xzPos.startY(), xzPos.z());
                    //         if (
                    //                 !EclipticUtil.isHereSnowy(level, updatePos)
                    //                         // (isHereSunny(level, updatePos))
                    //                         // || isHereRainy(level, updatePos)
                    //                         && EclipticUtil.isHereWithSnow(level, updatePos)
                    //         ) {
                    //             xzPos = new XZPos(xzPos.x(), xzPos.z(), level.getGameTime() - 50, level.getMaxBuildHeight() + 1);
                    //             xzPosList.put(i, xzPos);
                    //             MapChecker.updatePosForce(level, updatePos, xzPos.startY());
                    //             var sectionPos = SectionPos.of(updatePos);
                    //             updateSectionsList.add(sectionPos);
                    //         } else {
                    //             xzPosList.removeFirst();
                    //             i--;
                    //             int y = MapChecker.getHeightOrUpdate(level, updatePos, true);
                    //             // if (y != xzPos.startY())
                    //             {
                    //                 updatePos.setY(y);
                    //                 var sectionPos = SectionPos.of(updatePos);
                    //                 updateSectionsList.add(sectionPos);
                    //             }
                    //         }
                    //     } else {
                    //         break;
                    //     }
                    // }

                    if (xzPosList.isEmpty()) {
                        removeNeedChunkPosList.add(new ChunkPos(chunkPos));
                    }
                }
        );

        for (ChunkPos chunkPos : removeNeedChunkPosList) {
            CHUNK_POS_XZ_POS_MAP.remove(chunkPos.toLong());
        }

        for (SectionPos sectionPos : updateSectionsList) {
            WorldRenderer.setSectionDirty(sectionPos);
        }
    }


}

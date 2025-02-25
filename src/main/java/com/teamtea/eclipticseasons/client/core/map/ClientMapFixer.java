package com.teamtea.eclipticseasons.client.core.map;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

import java.util.*;

/**
 * <p>这里可能会有复杂的情况，比如说<font color="blue">连续放置方块</font>的时候怎么计算。
 * 但是我们不管这么多，只需要定时刷新即可。
 * 未来也许可以处理连续更新<font color="green">同一xz位置</font>的情况</p>
 * <p>这里有一个新情况是如果是打破的话，就需要set 超高然后再恢复</p>
 * <p>未来需要处理下雪时才设置</p>
 **/
public class ClientMapFixer {


    private static final Map<ChunkPos, List<XZPos>> CHUNK_POS_XZ_POS_MAP = new HashMap<>();

    public static void clearAll() {
        CHUNK_POS_XZ_POS_MAP.clear();
    }

    public static void clearChunk(ChunkPos chunkPos) {
        CHUNK_POS_XZ_POS_MAP.remove(chunkPos);
    }

    public static void clearBlockPos(BlockPos blockPos) {
        List<XZPos> orDefault = CHUNK_POS_XZ_POS_MAP.getOrDefault(new ChunkPos(blockPos), new ArrayList<>());
        for (int i = 0; i < orDefault.size(); i++) {
            XZPos xzPos = orDefault.get(i);
            if (xzPos.x() == blockPos.getX() && xzPos.z() == blockPos.getZ()) {
                orDefault.remove(i);
                i--;
            }
        }

    }

    public static void addLightPlanner(ClientWorld level,  BlockPos pos, long startTick) {
        if (ClientConfig.Renderer.snowyWinter.get()
                && ClientConfig.Renderer.notSnowyNearGlowingBlock.get()
                && ClientConfig.Renderer.realisticSnowyChange.get()) {
            boolean isTooLight = level.getBrightness(LightType.BLOCK, pos) > ClientConfig.Renderer.notSnowyNearGlowingBlockLevel.get();

            if (isTooLight) {
                pos = pos.below();
                BlockState state = level.getBlockState(pos);
                boolean isOldHeight = pos.getY() == level.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                if (Heightmap.Type.MOTION_BLOCKING.isOpaque().test(state)
                        && isOldHeight
                        && EclipticUtil.isHereWithSnow(level, pos)) {
                    ChunkPos chunkPos = new ChunkPos(pos);
                    List<XZPos> xzPosList = CHUNK_POS_XZ_POS_MAP.computeIfAbsent(chunkPos, k -> new ArrayList<>());
                    xzPosList.add(new XZPos(pos.getX(), pos.getZ(), startTick, pos.getY()));
                    ModelManager.updatePosForce(pos, level.getMaxBuildHeight() + 1);
                }
            }
        }

    }
    public static void addPlanner(ClientWorld level, BlockState state, BlockPos pos, long startTick, int startY) {
        boolean isNotOldHeight = startY != level.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;

        if (ClientConfig.Renderer.realisticSnowyChange.get()
                && ((Heightmap.Type.MOTION_BLOCKING.isOpaque().test(state))
                || state.getBlock() == Blocks.AIR
        )
                && isNotOldHeight
                && EclipticUtil.isHereWithSnow(level, pos)
        ) {
            // TODO：如果这里不下雪的话，那么直接更新就好了.以及未来可以考虑合并同一个点的
            ChunkPos chunkPos = new ChunkPos(pos);
            List<XZPos> xzPosList = CHUNK_POS_XZ_POS_MAP.computeIfAbsent(chunkPos, k -> new ArrayList<>());
            xzPosList.add(new XZPos(pos.getX(), pos.getZ(), startTick, startY));
            if (state.getBlock() == Blocks.AIR) {
                ModelManager.updatePosForce(pos, level.getMaxBuildHeight() + 1);
            }
        } else {
            if (isNotOldHeight) {
                ModelManager.getHeightOrUpdate( pos, true);
            }
        }

    }

    public static void tick(World level) {
        long tick = level.getGameTime();
        List<ChunkPos> removeNeedChunkPosList = new ArrayList<>();
        Set<SectionPos> updateSectionsList = new HashSet<>();
        CHUNK_POS_XZ_POS_MAP.forEach(
                (chunkPos, xzPosList) -> {
                    for (int i = 0; i < xzPosList.size(); i++) {
                        XZPos xzPos = xzPosList.get(i);
                        if (tick - xzPos.startTick() > 160
                                &&updateSectionsList.size()<12
                        ) {
                            BlockPos.Mutable updatePos = new BlockPos.Mutable(xzPos.x(), xzPos.startY(), xzPos.z());
                            if (
                                    !EclipticUtil.isHereSnowy(level, updatePos)
                                            // (isHereSunny(level, updatePos))
                                            // || isHereRainy(level, updatePos)
                                            && EclipticUtil.isHereWithSnow(level, updatePos)
                            ) {
                                xzPos = new XZPos(xzPos.x(), xzPos.z(), level.getGameTime() - 50, level.getMaxBuildHeight() + 1);
                                xzPosList.set(i, xzPos);
                                ModelManager.updatePosForce( updatePos, xzPos.startY());
                                SectionPos sectionPos = SectionPos.of(updatePos);
                                updateSectionsList.add(sectionPos);
                            } else {
                                xzPosList.remove(0);
                                i--;
                                int y = ModelManager.getHeightOrUpdate(updatePos, true);
                                // if (y != xzPos.startY())
                                {
                                    updatePos.setY(y);
                                    SectionPos sectionPos = SectionPos.of(updatePos);
                                    updateSectionsList.add(sectionPos);
                                }
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
            CHUNK_POS_XZ_POS_MAP.remove(chunkPos);
        }

        for (SectionPos sectionPos : updateSectionsList) {
            Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
        }
    }


}

package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.level.ClientLevelModule;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.level.DhClientServerLevel;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.render.LodQuadTree;
import com.seibel.distanthorizons.core.render.LodRenderSection;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.gridList.MovableGridRingList;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.mixin.compat.distanthorizons.MixinQuadTree;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.McObjectConverter;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DHTool {

    public static void forceReloadAll() {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;

        IDhClientWorld clientWorld = SharedApi.getIDhClientWorld();
        if (Minecraft.getInstance().level != null
                && ClientLevelWrapper.getWrapper(Minecraft.getInstance().level) instanceof ClientLevelWrapper clientLevelWrapper
                && clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel) {

            AtomicReference<ClientLevelModule.ClientRenderState> clientRenderStateAtomicReference = null;
            if (clientLevel instanceof DhClientServerLevel dhClientServerLevel) {
                clientRenderStateAtomicReference = dhClientServerLevel.clientside.ClientRenderStateRef;
            } else if (clientLevel instanceof DhClientLevel dhClientLevel) {
                clientRenderStateAtomicReference = dhClientLevel.clientside.ClientRenderStateRef;
            }
            if (clientRenderStateAtomicReference != null) {

                LodQuadTree quadtree = clientRenderStateAtomicReference.get().quadtree;
                MixinQuadTree quadtree1 = (MixinQuadTree) quadtree;


                List<Long> reloadList = new ArrayList<>();
                MovableGridRingList<QuadNode> topRingList = quadtree1.getTopRingList();
                Stack<QuadNode> stack = new Stack<>();

                for (int i = 0, topRingListSize = topRingList.size(); i < topRingListSize; i++) {
                    stack.push(topRingList.get(i));
                }

                while (!stack.isEmpty()) {
                    QuadNode node = stack.pop();
                    if (node == null || node.value == null) continue;
                    if (!(node.value instanceof LodRenderSection lodRenderSection)
                            || lodRenderSection.getRenderingEnabled()) {
                        reloadList.add(node.sectionPos);
                    }
                    for (int i = 3; i >= 0; i--) {
                        stack.push(node.getChildByIndex(i));
                    }
                }

                // Map<Byte, List<Long>> groupedByDetail = reloadList.stream()
                //         .collect(Collectors.groupingBy(DhSectionPos::getDetailLevel));
                // List<Byte> sortedDetailLevels = new ArrayList<>(groupedByDetail.keySet());
                // sortedDetailLevels.sort(Byte::compare);
                // DhBlockPos2D centerBlockPos = quadtree.getCenterBlockPos();
                // for (Byte detailLevel : sortedDetailLevels) {
                //     List<Long> group = groupedByDetail.get(detailLevel);
                //
                //     group.sort((l1, l2) -> {
                //         int dx1 = Math.abs(centerBlockPos.x - DhSectionPos.getCenterBlockPosX(l1));
                //         int dz1 = Math.abs(centerBlockPos.z - DhSectionPos.getCenterBlockPosZ(l1));
                //         int dx2 = Math.abs(centerBlockPos.x - DhSectionPos.getCenterBlockPosX(l2));
                //         int dz2 = Math.abs(centerBlockPos.z - DhSectionPos.getCenterBlockPosZ(l2));
                //         int dist1 = dx1 + dz1;
                //         int dist2 = dx2 + dz2;
                //         return Integer.compare(dist1, dist2);
                //     });
                //
                //     Set<Long> setsLong = new LongLinkedOpenHashSet();
                //     for (Long pos : group) {
                //         if (setsLong.contains(pos)) continue;
                //         quadtree.reloadPos(pos);
                //         setsLong.add(pos);
                //         for (EDhDirection direction : EDhDirection.ADJ_DIRECTIONS) {
                //             long adjacentPos = DhSectionPos.getAdjacentPos(pos, direction);
                //             setsLong.add(adjacentPos);
                //         }
                //     }
                // }

                Set<Long> setsLong = new LongLinkedOpenHashSet();
                for (long pos : reloadList) {
                    if (setsLong.contains(pos)) continue;
                    quadtree.reloadPos(pos);
                    setsLong.add(pos);
                    for (EDhDirection direction : EDhDirection.ADJ_DIRECTIONS) {
                        long adjacentPos = DhSectionPos.getAdjacentPos(pos, direction);
                        setsLong.add(adjacentPos);
                    }
                }

                //     // 也许未来需要定向刷新，但是目前来看只需要全部刷新即可
                // int d = (int) Config.Client.quickLodChunkRenderDistance.get().get() / 2;

                // SectionPos sectionPos = SectionPos.of(pos);
                // int pSectionX = SectionPos.blockToSectionCoord(pos.x);
                // int pSectionZ = SectionPos.blockToSectionCoord(pos.z);
                //
                // byte treeMinDetailLevel = quadtree.treeMinDetailLevel;
                // byte treeMaxDetailLevel = quadtree.treeMaxDetailLevel;
                // for (int i = pSectionX - d; i <= pSectionX + d; i++) {
                //     for (int j = pSectionZ - d; j <= pSectionZ + d; j++) {
                //         for (byte k = treeMaxDetailLevel; k <= treeMinDetailLevel; k++) {
                //             // 注意这里是dh的sectionpos，其实与mc中类似
                //             // long rootPos = DhSectionPos.encode(k, i, j);
                //             // clientRenderStateAtomicReference.get().quadtree.reloadPos(rootPos);
                //
                //         }
                //     }
                // }

            }
        }
    }

    public static MapColor computeBaseColor(IClientLevelWrapper instance, DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, IBlockStateWrapper iBlockStateWrapper, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, IWrapperFactory WRAPPER_FACTORY, int skyLight) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;

        if (CommonConfig.isSnowyWinter()) {
            if (!dhBlockPos.equals(DhBlockPos.ZERO)
                    && iBlockStateWrapper instanceof BlockStateWrapper blockStateWrapper
                    && !blockStateWrapper.isAir()
                    && skyLight > 0
            ) {
                var mcPos = McObjectConverter.Convert(dhBlockPos);
                var level = Minecraft.getInstance().level;
                var blockState = blockStateWrapper.blockState;
                // 当给的pos未加载时，读取的是虚空，这并不好。
                if (instance instanceof ClientLevelWrapper clientLevelWrapper) {
                    var holderKey = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(iBiomeWrapper.getSerialString()));
                    Holder.Reference<Biome> holder = clientLevelWrapper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(holderKey);
                    // if ((holderOrThrow
                    //         instanceof Holder.Reference<Biome> holder))
                    {

                        if (MapChecker.shouldSnowAtBiome(level, holder.value(), blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos))
                        //     return mapColor.col;
                        {
                            HashSet<IBlockStateWrapper> blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(instance);
                            for (int i = 0; i < fullColumnData.size(); i++) {
                                long fullData = fullColumnData.getLong(i);
                                int id = FullDataPointUtil.getId(fullData);
                                IBlockStateWrapper iBlockStateWrapper_NowQuery;
                                try {
                                    iBlockStateWrapper_NowQuery = fullDataMapping.getBlockStateWrapper(id);
                                } catch (IndexOutOfBoundsException e) {
                                    continue;
                                }
                                int bottomY = FullDataPointUtil.getBottomY(fullData);
                                int blockHeight = FullDataPointUtil.getHeight(fullData);
                                int topY = bottomY + blockHeight;
                                if (CommonConfig.Debug.notLightAbove.get()
                                        && iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery) {
                                    if (blockStateWrapper_NowQuery.blockState != null &&
                                            blockStateWrapper_NowQuery.blockState.getBlock() instanceof LightBlock) {
                                        if (blockStateWrapper_NowQuery.blockState.hasProperty(LightBlock.LEVEL)
                                                && blockStateWrapper_NowQuery.blockState.getValue(LightBlock.LEVEL) == 0)
                                            break;
                                    }
                                }

                                if (iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery
                                        && !iBlockStateWrapper_NowQuery.isAir()
                                        && !blockStatesToIgnore.contains(iBlockStateWrapper_NowQuery)
                                ) {

                                    if (bottomY + instance.getMinHeight() == dhBlockPos.getY() &&
                                            (MapChecker.getDefaultBlockTypeFlag(blockStateWrapper_NowQuery.blockState) != 0
                                                    // || (blockStateWrapper1.blockState.is(BlockTags.FLOWERS))
                                                    || (!blockStateWrapper_NowQuery.isSolid() && !blockStateWrapper_NowQuery.isLiquid())
                                            )) {
                                        // return Color.WHITE.getRGB();
                                        return MapColor.SNOW;
                                    } else {
                                        if (!blockStateWrapper_NowQuery.isLiquid()
                                                && !blockStateWrapper_NowQuery.blockState.blocksMotion()) {
                                            // 如果colorBelowWithAvoidedBlocks时，这时会查看下面的方块，我们也进行一个染色
                                            // 暂时不处理多层需要跳过的方块，实际上也许保留一点颜色会更好看
                                            if (i + 1 < fullColumnData.size()) {
                                                int belowBottomY = FullDataPointUtil.getBottomY(fullColumnData.getLong(i + 1));
                                                if (belowBottomY + instance.getMinHeight() == dhBlockPos.getY())
                                                    return MapColor.SNOW;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }
        return null;
    }

    public static Biome recoverBiomeObject(BiomeWrapper biomeWrapper, IClientLevelWrapper iClientLevelWrapper) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;
        // if (iClientLevelWrapper instanceof ClientLevelWrapper clientLevelWrapper) {
        //     var holderKey = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(biomeWrapper.getSerialString()));
        //     if ((clientLevelWrapper.getLevel().registryAccess().holder(holderKey).orElse(null)
        //             instanceof Holder.Reference<Biome> holder)) {
        //         // if (BiomeWrapper.getBiomeWrapper(holder, clientLevelWrapper) instanceof BiomeWrapper biomeWrapper1)
        //         return holder.value();
        //     }
        // }
        return null;
    }

    public static void clearRenderCache() {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;
        IDhClientWorld clientWorld = SharedApi.getIDhClientWorld();
        if (Minecraft.getInstance().level != null
                && ClientLevelWrapper.getWrapper(Minecraft.getInstance().level) instanceof ClientLevelWrapper clientLevelWrapper
                && clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel) {
            clientLevel.clearRenderCache();
        }
    }

    public static IBlockStateWrapper shouldFrozen(ClientLevelWrapper instance, IBiomeWrapper biomeWrapper, DhBlockPosMutable dhBlockPosMutable, BlockState blockState, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, int index) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;

        if (ClientConfig.Debug.frozenWater.get()
                && biomeWrapper.getWrappedMcObject() instanceof Holder<?> holder
                && holder.value() instanceof Biome biome
                && blockState.is(Blocks.WATER)
                && blockState.getFluidState().isSourceOfType(Fluids.WATER)) {
            if (index > 0 && index < fullColumnData.size() - 1) {
                try {
                    int id = FullDataPointUtil.getId(fullColumnData.getLong(index - 1));
                    if (!fullDataMapping.getBlockStateWrapper(id).isAir())
                        return null;
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
            var mcPos = McObjectConverter.Convert(dhBlockPosMutable);
            ClientLevel level = instance.getLevel();
            if (MapChecker.shouldSnowAtBiome(level, biome, blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos)) {
                return BlockStateWrapper.fromBlockState(Blocks.ICE.defaultBlockState(), instance);
            }
        }
        return null;
    }

}

package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

import java.util.*;

public class DHTool {

    public static MapColor computeBaseColor(IClientLevelWrapper instance, DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, IBlockStateWrapper iBlockStateWrapper, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, IWrapperFactory WRAPPER_FACTORY, int skyLight) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;

        if (CommonConfig.isSnowyWinter()) {
            if (!dhBlockPos.equals(DhBlockPos.ZERO)
                    && iBlockStateWrapper instanceof BlockStateWrapper_neoforge blockStateWrapper
                    && !blockStateWrapper.isAir()
                    && skyLight > 0
            ) {
                var mcPos = McObjectConverter_neoforge.Convert(dhBlockPos);
                var level = ClientCon.getUseLevel();
                var blockState = blockStateWrapper.blockState;
                // 当给的pos未加载时，读取的是虚空，这并不好。
                if (instance instanceof ClientLevelWrapper_neoforge clientLevelWrapper) {
                    var holderKey = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(iBiomeWrapper.getSerialString()));
                    Holder.Reference<Biome> holder = clientLevelWrapper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(holderKey);
                    // if ((holderOrThrow
                    //         instanceof Holder.Reference<Biome> holder))
                    {

                        if (MapChecker.shouldSnowAtBiome(level, holder.value(), blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos))
                        //     return mapColor.col;
                        {
                            var blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(instance);
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
                                        && iBlockStateWrapper_NowQuery instanceof BlockStateWrapper_neoforge blockStateWrapper_NowQuery) {
                                    if (blockStateWrapper_NowQuery.blockState != null &&
                                            blockStateWrapper_NowQuery.blockState.getBlock() instanceof LightBlock) {
                                        if (blockStateWrapper_NowQuery.blockState.hasProperty(LightBlock.LEVEL)
                                                && blockStateWrapper_NowQuery.blockState.getValue(LightBlock.LEVEL) == 0)
                                            break;
                                    }
                                }

                                if (iBlockStateWrapper_NowQuery instanceof BlockStateWrapper_neoforge blockStateWrapper_NowQuery
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

    public static Biome recoverBiomeObject(BiomeWrapper_neoforge biomeWrapper, IClientLevelWrapper iClientLevelWrapper) {
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

    //public static void clearRenderCache() {
    //    if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;
    //    IDhClientWorld clientWorld = SharedApi.getIDhClientWorld();
    //    if (Minecraft.getInstance().level != null
    //            && ClientLevelWrapper.getWrapper(Minecraft.getInstance().level) instanceof ClientLevelWrapper clientLevelWrapper
    //            && clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel) {
    //        clientLevel.clearRenderCache();
    //    }
    //}

    public static IBlockStateWrapper shouldFrozen(ClientLevelWrapper_neoforge instance, IBiomeWrapper biomeWrapper, DhBlockPosMutable dhBlockPosMutable, BlockState blockState, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, int index) {
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
            var mcPos = McObjectConverter_neoforge.Convert(dhBlockPosMutable);
            Level level = instance.getLevel();
            if (MapChecker.shouldSnowAtBiome(level, biome, blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos)) {
                return BlockStateWrapper_neoforge.fromBlockState(Blocks.ICE.defaultBlockState(), instance);
            }
        }
        return null;
    }

}

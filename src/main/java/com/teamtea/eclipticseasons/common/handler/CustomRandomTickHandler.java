package com.teamtea.eclipticseasons.common.handler;


import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick2;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;


public final class CustomRandomTickHandler {
    public static final CustomRandomTick2 SNOW_MELT_2 = (level, biomeHolder, pos) ->
    {
        if (WeatherManager.getSnowStatus(level, biomeHolder, pos, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biomeHolder, pos)) == WeatherManager.SnowRenderStatus.SNOW_MELT)
        {
            // snow melt
            BlockState topState = level.getBlockState(pos);
            if (topState.is(Blocks.SNOW)
                    && ((!(CommonConfig.Temperature.snowKeepInSnowyBiomes.get() && biomeHolder.is(ClimateTypeBiomeTags.EXTREME_COLD))
                    || !biomeHolder.value().shouldSnow(level, pos)))) {
                int layer = topState.getValue(SnowLayerBlock.LAYERS);
                level.setBlockAndUpdate(pos, layer <= 2 ?
                        Blocks.AIR.defaultBlockState() :
                        topState.setValue(SnowLayerBlock.LAYERS, layer - 2));
            }

            // ice melt
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(Blocks.ICE)
                    && ((!(CommonConfig.Temperature.waterFreezesInFrozenBiomes.get() && biomeHolder.is(ClimateTypeBiomeTags.EXTREME_COLD))
                    || !biomeHolder.value().shouldFreeze(level, belowPos, false)))) {
                if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) level.removeBlock(belowPos, false);
                else level.setBlockAndUpdate(belowPos, Blocks.WATER.defaultBlockState());
            }
        }
    };

    @Deprecated
    public static final CustomRandomTick2 SNOW_MELT = (level, biomeHolder, pos) ->
    {
        if (WeatherManager.getSnowStatus(level, biomeHolder, pos, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biomeHolder, pos)) == WeatherManager.SnowRenderStatus.SNOW) {

            // place snow
            if (pos.getY() >= level.getMinY() && pos.getY() < level.getMaxY() && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
                int layers = level.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
                if (layers > 0) {
                    BlockState blockstate = level.getBlockState(pos);
                    if (blockstate.is(Blocks.SNOW)) {
                        int k = blockstate.getValue(SnowLayerBlock.LAYERS);
                        if (k < Math.min(layers, 8)) {
                            BlockState snowState = blockstate.setValue(SnowLayerBlock.LAYERS, k + 1);
                            Block.pushEntitiesUp(blockstate, snowState, level, pos);
                            level.setBlockAndUpdate(pos, snowState);
                        }
                    } else {
                        level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                    }
                }
            }

            // place ice
            BlockPos below = pos.below();
            if (below.getY() >= level.getMinY() && below.getY() < level.getMaxY() && level.getBrightness(LightLayer.BLOCK, below) < 10) {
                BlockState blockstate = level.getBlockState(below);
                FluidState fluidstate = level.getFluidState(below);
                if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof LiquidBlock) {
                    boolean flag = level.isWaterAt(below.west()) && level.isWaterAt(below.east()) && level.isWaterAt(below.north()) && level.isWaterAt(below.south());
                    if (!flag) {
                        level.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
                    }
                }
            }

        }
    };

    public static boolean checkExtraSnowCondition(ServerLevel level, Holder<Biome> biomeHolder, BlockPos pos) {
        if (CommonConfig.Temperature.snowDown.get()
                && WeatherManager.getSnowStatus(level, biomeHolder, pos, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biomeHolder, pos)) == WeatherManager.SnowRenderStatus.SNOW) {
            if (pos.getY() >= level.getMinY()
                    && pos.getY() < level.getMaxY()
                    && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
                BlockState blockstate = level.getBlockState(pos);
                if ((blockstate.isAir() || blockstate.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive(level, pos)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean checkExtraFreezeCondition(ServerLevel level, Holder<Biome> biomeHolder, BlockPos water) {
        if (CommonConfig.Temperature.waterFreezesInFrozenBiomes.get()
                && biomeHolder.is(ClimateTypeBiomeTags.EXTREME_COLD)) {
            return biomeHolder.value().shouldFreeze(level, water);
        }
        if (CommonConfig.Temperature.snowDown.get()
                && WeatherManager.getSnowStatus(level, biomeHolder, water, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biomeHolder, water)) == WeatherManager.SnowRenderStatus.SNOW) {
            if (water.getY() >= level.getMinY()
                    && water.getY() < level.getMaxY()
                    && level.getBrightness(LightLayer.BLOCK, water) < 10) {
                BlockState blockstate = level.getBlockState(water);
                FluidState fluidstate = level.getFluidState(water);
                if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof LiquidBlock) {
                    // if (!mustBeAtEdge) {
                    //     return true;
                    // }

                    boolean flag = level.isWaterAt(water.west())
                            && level.isWaterAt(water.east())
                            && level.isWaterAt(water.north())
                            && level.isWaterAt(water.south());
                    if (!flag) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }


}

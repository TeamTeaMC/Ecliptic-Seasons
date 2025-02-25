package com.teamtea.eclipticseasons.common.handler;


import com.teamtea.eclipticseasons.api.misc.CustomRandomTick2;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;


public final class CustomRandomTickHandler {
    public static final CustomRandomTick2 SNOW_MELT_2 = (world, biome, blockpos) ->
    {
        if (WeatherManager.getSnowStatus(world,biome,blockpos)== WeatherManager.SnowRenderStatus.SNOW_MELT) {
            // snow melt
            BlockState topState = world.getBlockState(blockpos);
            if (topState.getBlock().equals(Blocks.SNOW)) {
                int layer = topState.getValue(SnowBlock.LAYERS);
                world.setBlockAndUpdate(blockpos, layer <= 2 ?
                        Blocks.AIR.defaultBlockState() :
                        topState.setValue(SnowBlock.LAYERS, layer - 2));
            }

            // ice melt
            BlockState belowState = world.getBlockState(blockpos.below());
            if (belowState.getBlock().equals(Blocks.ICE)) {
                if (world.dimensionType().ultraWarm()) world.removeBlock(blockpos, false);
                else world.setBlockAndUpdate(blockpos.below(), Blocks.WATER.defaultBlockState());
            }
        }
    };
    public static final CustomRandomTick2 SNOW_MELT = (world, biome, blockpos) ->
    {
        if (WeatherManager.getSnowStatus(world, biome, blockpos) == WeatherManager.SnowRenderStatus.SNOW) {

            // place snow
            if (blockpos.getY() >= 0 && blockpos.getY() < world.getMaxBuildHeight() && world.getBrightness(LightType.BLOCK, blockpos) < 10) {
                BlockState blockstate = world.getBlockState(blockpos);
                int layer = 0;
                if (blockstate.isAir() || (CommonConfig.Temperature.layerSnow.get() && blockstate.is(Blocks.SNOW))) {
                    if (CommonConfig.Temperature.layerSnow.get() && blockstate.is(Blocks.SNOW)) {
                        layer = Math.min(blockstate.getValue(SnowBlock.LAYERS) +
                                (world.getRandom().nextBoolean() ? 1 : 0), 5);
                    }
                    BlockState snowState = blockstate.isAir() ? Blocks.SNOW.defaultBlockState() : blockstate.setValue(SnowBlock.LAYERS, layer);
                    if (snowState.canSurvive(world, blockpos)) {
                        world.setBlockAndUpdate(blockpos, snowState);
                    }
                }
            }

            // place ice
            BlockPos below = blockpos.below();
            if (below.getY() >= 0 && below.getY() < world.getMaxBuildHeight() && world.getBrightness(LightType.BLOCK, below) < 10) {
                BlockState blockstate = world.getBlockState(below);
                FluidState fluidstate = world.getFluidState(below);
                if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
                    boolean flag = world.isWaterAt(below.west()) && world.isWaterAt(below.east()) && world.isWaterAt(below.north()) && world.isWaterAt(below.south());
                    if (!flag) {
                        world.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
                    }
                }
            }

        }
    };
}

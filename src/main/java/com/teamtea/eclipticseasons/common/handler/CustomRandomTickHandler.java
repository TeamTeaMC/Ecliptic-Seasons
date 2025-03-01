package com.teamtea.eclipticseasons.common.handler;


import com.teamtea.eclipticseasons.api.misc.CustomRandomTick2;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.level.ChunkDataEvent;


public final class CustomRandomTickHandler {
    public static final CustomRandomTick2 SNOW_MELT_2 = (world, biome, blockpos) ->
    {
        if (WeatherManager.getSnowStatus(world, biome, blockpos) == WeatherManager.SnowRenderStatus.SNOW_MELT) {
            // snow melt
            BlockState topState = world.getBlockState(blockpos);
            if (topState.is(Blocks.SNOW)) {
                int layer = topState.getValue(SnowLayerBlock.LAYERS);
                world.setBlockAndUpdate(blockpos, layer <= 2 ?
                        Blocks.AIR.defaultBlockState() :
                        topState.setValue(SnowLayerBlock.LAYERS, layer - 2));
            }

            // ice melt
            BlockState belowState = world.getBlockState(blockpos.below());
            if (belowState.is(Blocks.ICE)) {
                if (world.dimensionType().ultraWarm()) world.removeBlock(blockpos, false);
                else world.setBlockAndUpdate(blockpos.below(), Blocks.WATER.defaultBlockState());
            }
        }
    };
    public static final CustomRandomTick2 SNOW_MELT = (world, biome, blockpos) ->
    {
        if (WeatherManager.getSnowStatus(world, biome, blockpos) == WeatherManager.SnowRenderStatus.SNOW) {

            // place snow
            if (blockpos.getY() >= world.getMinBuildHeight() && blockpos.getY() < world.getMaxBuildHeight() && world.getBrightness(LightLayer.BLOCK, blockpos) < 10) {
                int layers = world.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
                if (layers > 0) {
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.is(Blocks.SNOW)) {
                        int k = blockstate.getValue(SnowLayerBlock.LAYERS);
                        if (k < Math.min(layers, 8)) {
                            BlockState snowState = blockstate.setValue(SnowLayerBlock.LAYERS, k + 1);
                            Block.pushEntitiesUp(blockstate, snowState, world, blockpos);
                            world.setBlockAndUpdate(blockpos, snowState);
                        }
                    } else {
                        world.setBlockAndUpdate(blockpos, Blocks.SNOW.defaultBlockState());
                    }
                }
            }

            // place ice
            BlockPos below = blockpos.below();
            if (below.getY() >= world.getMinBuildHeight() && below.getY() < world.getMaxBuildHeight() && world.getBrightness(LightLayer.BLOCK, below) < 10) {
                BlockState blockstate = world.getBlockState(below);
                FluidState fluidstate = world.getFluidState(below);
                if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof LiquidBlock) {
                    boolean flag = world.isWaterAt(below.west()) && world.isWaterAt(below.east()) && world.isWaterAt(below.north()) && world.isWaterAt(below.south());
                    if (!flag) {
                        world.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
                    }
                }
            }

        }
    };
}

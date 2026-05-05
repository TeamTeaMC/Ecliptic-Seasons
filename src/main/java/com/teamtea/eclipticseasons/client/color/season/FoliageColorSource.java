package com.teamtea.eclipticseasons.client.color.season;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public record FoliageColorSource() implements BlockTintSource {


    @Override
    public int color(@NonNull BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.SPRUCE_LEAVES) {
            return FoliageColor.FOLIAGE_EVERGREEN;
        } else if (block == Blocks.BIRCH_LEAVES) {
            return FoliageColor.FOLIAGE_BIRCH;
        } else if (block == Blocks.MANGROVE_LEAVES) {
            return FoliageColor.FOLIAGE_MANGROVE;
        }
        return FoliageColor.FOLIAGE_DEFAULT;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        // return BlockTintSource.super.colorInWorld(state, level, pos);
        Block block = state.getBlock();
        if (block == Blocks.SPRUCE_LEAVES) {
            return BiomeColorsHandler.getSpruceColor(state, level, pos);
        } else if (block == Blocks.BIRCH_LEAVES) {
            return BiomeColorsHandler.getBirchColor(state, level, pos);
        } else if (block == Blocks.MANGROVE_LEAVES) {
            return BiomeColorsHandler.getMangroveColor(state, level, pos);
        }
        return color(state);
    }

    public static Impl createOrNull(String s) {
        FoliageColorSourceDefault.ColorHolder parse = FoliageColorSourceDefault.parse(s);
        return parse == null ? null : new Impl(parse);
    }


    public record Impl(FoliageColorSourceDefault.ColorHolder content) implements BlockTintSource {
        @Override
        public int color(@NonNull BlockState state) {
            return content.base();
        }

        @Override
        public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
            return BiomeColorsHandler.getLeavesColor(content.base(), content.values(), pos);
        }
    }

}

package com.teamtea.eclipticseasons.compat.sodium;

import net.caffeinemc.mods.sodium.api.util.ColorMixer;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public interface SodiumBlenderColorProvider extends ColorProvider<BlockState>, BlockColor {
    @Override
    default void getColors(LevelSlice slice, BlockPos pos, BlockPos.MutableBlockPos scratchPos, BlockState state, ModelQuadView quad, int[] output, boolean smooth) {
        if (smooth) {
            for (int vertexIndex = 0; vertexIndex < 4; ++vertexIndex) {
                output[vertexIndex] = this.getVertexColor(slice, pos, scratchPos, quad, state, vertexIndex);
            }
        } else {
            int color = this.getColor(slice, state, pos);

            for (int vertexIndex = 0; vertexIndex < 4; ++vertexIndex) {
                output[vertexIndex] = color;
            }
        }

    }

    private int getVertexColor(LevelSlice slice, BlockPos pos, BlockPos.MutableBlockPos scratchPos, ModelQuadView quad, BlockState state, int vertexIndex) {
        float x = quad.getX(vertexIndex) - 0.5F;
        float y = quad.getY(vertexIndex) - 0.5F;
        float z = quad.getZ(vertexIndex) - 0.5F;
        int intX = Mth.floor(x);
        int intY = Mth.floor(y);
        int intZ = Mth.floor(z);
        float fracX = x - (float) intX;
        float var10000 = y - (float) intY;
        float fracZ = z - (float) intZ;
        int blockX = pos.getX() + intX;
        int blockY = pos.getY() + intY;
        int blockZ = pos.getZ() + intZ;
        int m00 = this.getColor(slice, state, scratchPos.set(blockX + 0, blockY, blockZ + 0));
        int m01 = this.getColor(slice, state, scratchPos.set(blockX + 0, blockY, blockZ + 1));
        int m10 = this.getColor(slice, state, scratchPos.set(blockX + 1, blockY, blockZ + 0));
        int m11 = this.getColor(slice, state, scratchPos.set(blockX + 1, blockY, blockZ + 1));
        return ColorMixer.mix2d(m00, m01, m10, m11, fracX, fracZ);
    }

    private int getColor(LevelSlice slice, BlockState state, BlockPos set) {
        return this.getColor(state, slice, set, 1);
    }
}

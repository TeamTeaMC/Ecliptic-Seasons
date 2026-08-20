package com.teamtea.eclipticseasons.compat.embeddium;


import com.teamtea.eclipticseasons.common.util.ColorMixHelper;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.embeddedt.embeddium.api.render.chunk.EmbeddiumBlockAndTintGetter;
import org.embeddedt.embeddium.api.util.ColorU8;
import org.embeddedt.embeddium.impl.model.color.ColorProvider;
import org.embeddedt.embeddium.impl.model.quad.ModelQuadView;
import org.jetbrains.annotations.Nullable;

public interface EmbeddiumBlenderColorProvider extends ColorProvider<BlockState>, BlockColor {

    @Override
    int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i);

    @Override
    default void getColors(EmbeddiumBlockAndTintGetter slice, BlockPos pos, BlockState state, ModelQuadView quad, int[] output) {
        BlockPos.MutableBlockPos scratchPos;
        if (slice instanceof IExtendBlockView view) {
            scratchPos = view.getModelCheckPos();
        } else {
            scratchPos = pos.mutable();
        }
        for (int vertexIndex = 0; vertexIndex < 4; ++vertexIndex) {
            int color = this.getVertexColor(slice, pos, scratchPos, quad, state, vertexIndex);

            output[vertexIndex] = 0xFF000000
                    | color & 0x0000FF00
                    | (color & 0x00FF0000) >>> 16
                    | (color & 0x000000FF) << 16;
        }
    }

    private int getVertexColor(EmbeddiumBlockAndTintGetter slice, BlockPos pos, BlockPos.MutableBlockPos scratchPos, ModelQuadView quad, BlockState state, int vertexIndex) {
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
        return ColorMixHelper.mix2d(m00, m01, m10, m11, fracX, fracZ);
    }

    private int getColor(EmbeddiumBlockAndTintGetter slice, BlockState state, BlockPos set) {
        return this.getColor(state, slice, set, 1);
    }
}

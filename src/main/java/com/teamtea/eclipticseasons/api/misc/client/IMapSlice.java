package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface IMapSlice extends IMapSliceProvider, BlockAndTintGetter, IExtendBlockView {
    default void forceMapSliceUpdate(int[][] heights, int[][] solidHeights, int[][] biomes, int SIZE_X, int SIZE_Z) {
    }

    BakedModel eclipticseasons$getSnowModel();

    void eclipticseasons$setSnowModel(BakedModel bakedModel);

    int getBlockHeight(BlockPos blockPos);

    int getSurfaceFaceBiomeId(BlockPos blockPos);

    default boolean isSnowyBlock(BlockPos blockPos) {
        return false;
    }

}

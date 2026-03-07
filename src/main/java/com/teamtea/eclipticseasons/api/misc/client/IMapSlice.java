package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface IMapSlice extends IMapSliceProvider, BlockAndTintGetter, IExtendBlockView, IFakeSnowHolder {
    default void forceMapSliceUpdate(int[][] heights, int[][] solidHeights, int[][] biomes, int SIZE_X, int SIZE_Z, SnowyStatusKeeper[] statusKeepers) {
    }

    int getBlockHeight(BlockPos pos);

    int getSurfaceFaceBiomeId(BlockPos pos);

    default boolean isSnowyBlock(BlockPos pos) {
        return false;
    }

}

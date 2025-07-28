package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.ApiStatus;

public interface IMapSlice extends BlockAndTintGetter, IMapSliceProvider {
    int getBlockHeight(BlockPos blockPos);

    int getSurfaceFaceBiomeId(BlockPos blockPos);

    default int getSnowyStatus(BlockPos blockPos) {
        return SnowyRemover.SNOWY;
    }

    default void forceMapSliceUpdate() {
    }

    @ApiStatus.Experimental
    default int getSnowDepth(BlockPos blockPos) {
        return -1;
    }
}

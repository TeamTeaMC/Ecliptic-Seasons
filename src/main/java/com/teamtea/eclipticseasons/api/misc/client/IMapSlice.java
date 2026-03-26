package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.jetbrains.annotations.ApiStatus;

public interface IMapSlice extends BlockAndTintGetter, IMapSliceProvider, IExtendBlockView, IFakeSnowHolder {
    int getBlockHeight(BlockPos pos);

    int getSurfaceFaceBiomeId(BlockPos pos);

    default int getSnowyStatus(BlockPos pos) {
        return SnowyRemover.SNOWY;
    }

    default boolean isSnowyBlock(BlockPos pos) {
        return false;
    }

    default void forceMapSliceUpdate() {
    }

}

package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface IMapSlice extends BlockAndTintGetter {
    int getBlockHeight(BlockPos blockPos);

    int getSurfaceFaceBiomeId(BlockPos blockPos);

    default int getSnowyStatus(BlockPos blockPos){return SnowyRemover.SNOWY;};
}

package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface IMapSlice extends BlockAndTintGetter {
    int getBlockHeight(BlockPos blockPos);

    int getSurfaceFaceBiomeId(BlockPos blockPos);



}

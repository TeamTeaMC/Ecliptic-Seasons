package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

public interface IMapSliceProvider {
    @ApiStatus.Experimental
    int getSolidBlockHeight(BlockPos pos);
}

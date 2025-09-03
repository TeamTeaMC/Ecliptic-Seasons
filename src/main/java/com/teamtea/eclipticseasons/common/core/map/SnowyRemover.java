package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(forRemoval = true)
@ApiStatus.Experimental
public record SnowyRemover(
        int[][] blockWatcher
) {
    @Deprecated(forRemoval = true)
    public boolean notSnowyAt(BlockPos pos) {
        return false;
    }
}

package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;


/**
 * we use the class since mc version 1.21.1, not now.
 **/
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

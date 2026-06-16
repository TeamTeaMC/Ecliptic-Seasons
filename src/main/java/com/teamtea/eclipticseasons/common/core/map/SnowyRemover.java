package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;


/**
 * we use the class since mc version 1.21.1, not now.
 **/
@Deprecated
@ApiStatus.Experimental
public record SnowyRemover(
        int[][] blockWatcher
) {
    @Deprecated(forRemoval = true)
    public boolean notSnowyAt(BlockPos pos) {
        return false;
    }

    public static final int SNOWY = 0;
    public static final int NONE_SNOWY = 1;
}

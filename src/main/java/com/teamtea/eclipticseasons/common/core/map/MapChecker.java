package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MapChecker {
    public static boolean isValidDimension(@Nullable World level) {
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        return result;
    }
}

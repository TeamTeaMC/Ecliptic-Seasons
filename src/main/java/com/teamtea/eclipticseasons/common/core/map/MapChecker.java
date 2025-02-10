package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class MapChecker {
    public static boolean isValidDimension(@Nullable Level level) {
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        return result;
    }
}

package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class MapChecker {
    public static boolean isValidDimension(@Nullable World level) {
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        return result;
    }

    public static Biome getSurfaceBiome(World level, BlockPos pos) {
        return level.getBiome(pos);
    }
}

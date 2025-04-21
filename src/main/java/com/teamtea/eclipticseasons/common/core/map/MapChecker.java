package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;

public class MapChecker {
    public static boolean isValidDimension(@Nullable Level level) {
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        return result;
    }

    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos) {
        return level.getBiome(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos));
    }
}

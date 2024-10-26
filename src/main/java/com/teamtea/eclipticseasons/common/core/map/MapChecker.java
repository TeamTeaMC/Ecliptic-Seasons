package com.teamtea.eclipticseasons.common.core.map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class MapChecker {

    public static boolean isSmallBiome(Holder<Biome> biomeHolder) {
        return biomeHolder.is(BiomeTags.IS_RIVER)
                || biomeHolder.is(BiomeTags.IS_BEACH)
                ;
    }


    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos) {
        // fix the pos to surface
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING,pos.getX(),pos.getZ());
        if (y != pos.getY()) {
            pos = new BlockPos(pos.getX(), y, pos.getZ());
        }


        var biome = level.getBiome(pos);
        int i = 0;
        while (isSmallBiome(biome)) {
            i += 1;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                // if (level.isLoaded(pos.relative(direction, i)))
                {
                    biome = level.getBiome(pos.relative(direction, i));
                    if (!isSmallBiome(biome)) {
                        break;
                    }
                }
            }
        }
        return biome;
    }

}

package com.teamtea.eclipticseasons.api.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

@Deprecated(forRemoval = true)
public class EclipticTagClientTool extends EclipticTagTool {

    @Deprecated(forRemoval = true)
    public static TagKey<Biome> getTag(Biome biome) {
        return EclipticTagTool.getTag(biome);

    }
}

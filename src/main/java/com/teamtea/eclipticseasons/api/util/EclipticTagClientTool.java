package com.teamtea.eclipticseasons.api.util;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;


@Deprecated
public class EclipticTagClientTool extends EclipticTagTool {

    @Deprecated
    public static BiomeDictionary.Type getTag(Biome biome) {
        return EclipticTagTool.getTag(biome);

    }
}

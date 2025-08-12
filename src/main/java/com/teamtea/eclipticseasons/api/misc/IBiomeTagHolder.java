package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeTagHolder {

    void eclipticseasons$setTag(TagKey<Biome> tag);

    TagKey<Biome> eclipticseasons$getBindTag();

    void eclipticseasons$setColorTag(TagKey<Biome> tag);

    TagKey<Biome> eclipticseasons$getBindColorTag();

    void eclipticseasons$setSmall(boolean isSmall);

    boolean eclipticseasons$isSmallBiome();

    default int eclipticseasons$getBindId() {
        return -1;
    }

    default void eclipticseasons$setBindId(int id) {
    }
}

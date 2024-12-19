package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeTagHolder {

    void eclipticSeasons$setTag(TagKey<Biome> tag);

    TagKey<Biome> eclipticSeasons$getBindTag();

    void eclipticSeasons$setSmall(boolean isSmall);

    boolean eclipticSeasons$isSmallBiome();
}

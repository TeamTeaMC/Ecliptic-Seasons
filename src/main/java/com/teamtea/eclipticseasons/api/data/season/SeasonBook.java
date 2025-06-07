package com.teamtea.eclipticseasons.api.data.season;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.EnumMap;

public record SeasonBook(
        Holder<BiomeSet> biomes,
        Holder<LocalSeason> none,
        EnumMap<SolarTerm, Holder<LocalSeason>> localMapping
) {

    public boolean matches(Holder<Biome> biomeHolder) {
        return biomes.value().matches(biomeHolder);
    }
}

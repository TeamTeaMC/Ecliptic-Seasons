package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.EnumMap;
import java.util.List;

public record SeasonBiomeRain(
        Holder<BiomeSet> biomes,
        Weather defaultW,
        EnumMap<SolarTerm, List<Weather>> weathers
) {


    public record Weather(
            float rainLevel, float thunderLevel, Biome.Precipitation precipitation,
            int min, int max, float chance, List<TimePeriod> timePeriod) {
    }

    // public record WeatherSampler(
    //
    // ) {
    //
    // }
}

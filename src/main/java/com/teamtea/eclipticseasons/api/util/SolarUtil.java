package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.constant.climate.*;
import com.teamtea.eclipticseasons.api.constant.climate.seasonal.ColdRain;
import com.teamtea.eclipticseasons.api.constant.climate.seasonal.HotRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;

public class SolarUtil {

    public static BiomeRain getBiomeRain(SolarTerm term, Holder<Biome> biomeHolder) {
        Map<SolarTerm, CustomRain> customRainMap = BiomeClimateManager.getCustomRain(biomeHolder.value(), BiomeClimateManager.isServerInstance(biomeHolder.value()));
        CustomRain customRain = customRainMap.getOrDefault(term, null);
        if (customRain != null) return customRain;

        TagKey<Biome> tag = BiomeClimateManager.getTag(biomeHolder.value());
        if (tag == ClimateTypeBiomeTags.RAINLESS)
            return FlatRain.RAINLESS;
        if (tag == ClimateTypeBiomeTags.ARID)
            return FlatRain.ARID;
        if (tag == ClimateTypeBiomeTags.DROUGHTY)
            return FlatRain.DROUGHTY;
        if (tag == ClimateTypeBiomeTags.SOFT)
            return FlatRain.SOFT;
        if (tag == ClimateTypeBiomeTags.RAINY)
            return FlatRain.RAINY;
        if (tag == ClimateTypeBiomeTags.MONSOONAL)
            return MonsoonRain.collectValues()[term.ordinal()];
        if (tag == ClimateTypeBiomeTags.SEASONAL_HOT)
            return HotRain.collectValues()[term.ordinal()];
        if (tag == ClimateTypeBiomeTags.SEASONAL_COLD)
            return ColdRain.collectValues()[term.ordinal()];
        return TemperateRain.collectValues()[term.ordinal()];
    }

    public static ISnowTerm getSnowTerm(Biome biome, boolean isServer, float tempChange) {
        if (biome == null) return SnowTerm.T05;
        ISnowTerm customSnowTerm = BiomeClimateManager.getCustomSnowTerm(biome, isServer);
        if (customSnowTerm != null) return customSnowTerm.cast(tempChange);
        // float t = BiomeClimateManager.getDefaultTemperature(biome, isServer);
        float t = biome.getModifiedClimateSettings().temperature();

        BiomeClimateSettings biomeClimateSettings = BiomeClimateManager.getBiomeClimateSettings(biome, isServer);
        t = biomeClimateSettings == BiomeClimateManager.EMPTY ? t : biomeClimateSettings.getTemperature();

        return SnowTerm.get(t, tempChange);
    }
}

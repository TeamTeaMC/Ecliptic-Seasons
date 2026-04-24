package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ESRegistries {

    public static final ResourceKey<Registry<WetterStructure>> WETTER = ResourceKey.createRegistryKey(EclipticSeasons.rl("wetter"));

    public static final ResourceKey<Registry<BiomesClimateSettings>> BIOME_CLIMATE_SETTING = ResourceKey.createRegistryKey(EclipticSeasons.rl("biome_climate_setting"));

    public static final ResourceKey<Registry<AgroClimaticZone>> AGRO_CLIMATE = ResourceKey.createRegistryKey(EclipticSeasons.rl("agro_climate"));

    public static final ResourceKey<Registry<CropGrowControlBuilder>> CROP = ResourceKey.createRegistryKey(EclipticSeasons.rl("crop"));

    public static final ResourceKey<Registry<SeasonQuest>> SEASON_QUEST = ResourceKey.createRegistryKey(EclipticSeasons.rl("season_quest"));

    public static final ResourceKey<Registry<HumidityControl>> HUMIDITY_CONTROL = ResourceKey.createRegistryKey(EclipticSeasons.rl("humidity_control"));

    public static final ResourceKey<Registry<SnowDefinition>> SNOW_DEFINITIONS = ResourceKey.createRegistryKey(EclipticSeasons.rl("snow_definitions"));

    public static final ResourceKey<Registry<SeasonPhase>> SEASON_PHASE = ResourceKey.createRegistryKey(EclipticSeasons.rl("season_phase"));

    public static final ResourceKey<Registry<SeasonCycle>> SEASON_CYCLE = ResourceKey.createRegistryKey(EclipticSeasons.rl("season_cycle"));

    public static final ResourceKey<Registry<CustomRainBuilder>> BIOME_RAIN = ResourceKey.createRegistryKey(EclipticSeasons.rl("biome_rain"));

    public static final ResourceKey<Registry<CustomSnowTerm>> SNOW_TERM = ResourceKey.createRegistryKey(EclipticSeasons.rl("snow_term"));

    public static final ResourceKey<Registry<SeasonDefinition>> SEASON_DEFINITION = ResourceKey.createRegistryKey(EclipticSeasons.rl("season_definitions"));

    public static final ResourceKey<Registry<ESSortInfo>> EXTRA_INFO = ResourceKey.createRegistryKey(EclipticSeasons.rl("extra_info"));

    public static final ResourceKey<Registry<WeatherEffect>> WEATHER_EFFECT = ResourceKey.createRegistryKey(EclipticSeasons.rl("biome_rain_effect"));

    public static <T> String createLangKey(ResourceKey<Registry<T>> registryResourceKey, ResourceLocation resourceLocation) {
        // return resourceLocation.toLanguageKey(registryResourceKey.location().getPath());
        return Util.makeDescriptionId(registryResourceKey.location().getPath(), resourceLocation);
    }

    public static <T> String createLangKey(ResourceKey<T> resourceLocation) {
        // return resourceLocation.location().toLanguageKey(resourceLocation.registryKey().location().getPath());
        return Util.makeDescriptionId(resourceLocation.registry().getPath(), resourceLocation.location());
    }
}

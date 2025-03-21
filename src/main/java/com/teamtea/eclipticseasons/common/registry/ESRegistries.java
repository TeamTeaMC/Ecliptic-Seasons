package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ESRegistries {

    public static final ResourceKey<Registry<AgroClimaticZone>> AGRO_CLIMATE = ResourceKey.createRegistryKey(EclipticSeasons.rl("agro_climate"));

    public static final ResourceKey<Registry<CropGrowControlBuilder>> CROP = ResourceKey.createRegistryKey(EclipticSeasons.rl("crop"));


    public static final ResourceKey<Registry<SeasonQuest>> SEASON_QUEST = ResourceKey.createRegistryKey(EclipticSeasons.rl("season_quest"));

    public static final ResourceKey<Registry<HumidityControl>> HUMIDITY_CONTROL = ResourceKey.createRegistryKey(EclipticSeasons.rl("humidity_control"));


    public static <T> String createLangKey(ResourceKey<Registry<T>> registryResourceKey, ResourceLocation resourceLocation) {
        // return resourceLocation.toLanguageKey(registryResourceKey.location().getPath());
        return Util.makeDescriptionId(registryResourceKey.location().getPath(), resourceLocation);
    }

    public static <T> String createLangKey(ResourceKey<T> resourceLocation) {
        // return resourceLocation.location().toLanguageKey(resourceLocation.registryKey().location().getPath());
        return Util.makeDescriptionId(resourceLocation.registry().getPath(), resourceLocation.location());
    }
}

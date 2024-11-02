package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.IdentityHashMap;
import java.util.Map;

public class BiomeClimateManager {
    public final static Map<Biome, Float> BIOME_DEFAULT_TEMPERATURE_MAP = new IdentityHashMap<>();
    public final static Map<Biome, Float> CLIENT_BIOME_DEFAULT_TEMPERATURE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, TagKey<Biome>> CLIENT_BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        resetBiomeTempsMap(registryAccess, isServer ? BIOME_DEFAULT_TEMPERATURE_MAP : CLIENT_BIOME_DEFAULT_TEMPERATURE_MAP);
        putTag(registryAccess, isServer);
    }

    public static void resetBiomeTempsMap(RegistryAccess registryAccess, Map<Biome, Float> useMap) {
        useMap.clear();
        var biomes = registryAccess.registry(Registries.BIOME);
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        {
            useMap.put(biome, biome.getModifiedClimateSettings().temperature());
        }));
    }

    public static final float DEFAULT_TEMPERATURE = 0.598F;

    public static float getDefaultTemperature(Biome biome, boolean isServer) {
        return isServer ?
                BIOME_DEFAULT_TEMPERATURE_MAP.getOrDefault(biome, DEFAULT_TEMPERATURE) :
                CLIENT_BIOME_DEFAULT_TEMPERATURE_MAP.getOrDefault(biome, DEFAULT_TEMPERATURE);
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    public static void updateTemperature(Level level, SolarTerm solarTermIndex) {
        boolean isServer = level instanceof ServerLevel;
        level.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        {
            var temperature = biome.getModifiedClimateSettings().temperature() > SNOW_LEVEL ?
                    Math.max(SNOW_LEVEL + 0.001F, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange()) :
                    Math.min(SNOW_LEVEL, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange());
            if (isServer) {
                BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, temperature);
            } else {
                CLIENT_BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, temperature);
            }

            // clean temperature change
            // var oldClimateSettings = biome.climateSettings;
            // biome.climateSettings = new Biome.ClimateSettings(
            //         oldClimateSettings.hasPrecipitation(),
            //         temperature,
            //         oldClimateSettings.temperatureModifier(),
            //         oldClimateSettings.downfall());
        }));
    }

    // it's hard to check the
    public static Float agent$GetBaseTemperature(Biome biome) {
        float f = getDefaultTemperature(biome, true);
        if (f == DEFAULT_TEMPERATURE) {
            float f2 = getDefaultTemperature(biome, false);
            f = f2 != f ? f2 : f;
        }
        return f;
    }

    public static Boolean agent$hasPrecipitation(Biome biome) {
        // return !getTag(biome).equals(ClimateTypeBiomeTags.RAINLESS);
        return WeatherManager.getPrecipitationAt(biome, BlockPos.ZERO)!= Biome.Precipitation.NONE;
    }


    public static Holder<Biome> getHolder(RegistryAccess registryAccess, Biome biome) {
        return registryAccess.registry(Registries.BIOME)
                .get()
                .holders()
                .filter(biomeReference -> biomeReference.value() == biome)
                .findFirst().get();
    }

    public static TagKey<Biome> getTag(Biome biome) {
        // return getTag(WeatherManager.getMainServerLevel(), biome);
        return BIOME_TAG_KEY_MAP.getOrDefault(biome, CLIENT_BIOME_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.RAINLESS));
    }

    // Clear it on client exit a level
    public static void putTag(RegistryAccess registryAccess, boolean isServer) {
        var useMap = isServer ? BIOME_TAG_KEY_MAP : CLIENT_BIOME_TAG_KEY_MAP;
        useMap.clear();
        for (Biome biome : SMALL_BIOME_MAP.entrySet().stream().filter(biomeBooleanEntry -> biomeBooleanEntry.getValue() == isServer).map(Map.Entry::getKey).toList()) {
            SMALL_BIOME_MAP.remove(biome);
        }

        var biomeRegistry = registryAccess.registry(Registries.BIOME);
        if (biomeRegistry.isPresent()) {
            for (var holder : biomeRegistry.get().holders().toList()) {
                var tag = ClimateTypeBiomeTags.BIOME_TYPES.stream().filter(holder::is).findFirst();
                // var tag = holder.get().tags().filter(ClimateTypeBiomeTags.BIOME_TYPES::contains).findFirst();
                if (tag.isPresent()) {
                    useMap.put(holder.value(), tag.get());
                } else {
                    // 我们按照降雨量进行分配，如果无预测则无雨
                    int size = ClimateTypeBiomeTags.COMMON_BIOME_TYPES.size();
                    int index = Mth.clamp(Mth.floor(holder.value().getModifiedClimateSettings().downfall() * size), 0, size - 1);
                    if (!holder.value().hasPrecipitation()) {
                        index = 0;
                    }
                    useMap.put(holder.value(), ClimateTypeBiomeTags.COMMON_BIOME_TYPES.get(index));
                }

                if (holder.is(ClimateTypeBiomeTags.IS_SMALL)) {
                    SMALL_BIOME_MAP.put(holder.value(), isServer);
                }
            }
        }
    }
}

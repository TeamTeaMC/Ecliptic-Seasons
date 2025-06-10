package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BiomeClimateManager {
    // 由于在1.20时代，客户端与单人服务器端对象未分离，因此这里不能作为评判依据
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        Optional<Registry<BiomesClimateSettings>> registry = registryAccess.registry(ESRegistries.BIOME_CLIMATE_SETTING);
        if (registry.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.BIOME_CLIMATE_SETTING);
        } else {
            if (isServer) {
                Registry<BiomesClimateSettings> biomesClimateSettings = registry.get();
                resetBiomeClimateMap(registryAccess, biomesClimateSettings, BIOME_CLIMATE_MAP);
            } else {
                if (ClientCon.biomeDataPackCache != null) {
                    List<BiomesClimateSettings> build = ClientCon.biomeDataPackCache.build(registryAccess, BiomesClimateSettings.class);
                    resetBiomeClimateMap(registryAccess, build, BIOME_CLIMATE_MAP);
                }
            }
            putTag(registryAccess, isServer);
        }
    }

    public static void resetBiomeClimateMap(RegistryAccess registryAccess, Iterable<BiomesClimateSettings> biomesClimateSettings, Map<Biome, BiomeClimateSettings> useMap) {
        useMap.clear();

        Map<Biome, List<BiomesClimateSettings>> biomeListMap = new IdentityHashMap<>();
        for (var value : biomesClimateSettings) {

            for (Holder<Biome> next : value.biomes()) {
                List<BiomesClimateSettings> biomesClimateSettingsList =
                        biomeListMap.computeIfAbsent(next.value(), k -> new ArrayList<>());
                biomesClimateSettingsList.add(value);
            }
        }
        Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
        List<BiomesClimateSettings> EMPTY_LIST = List.of();
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
                useMap.put(biome, new BiomeClimateSettings(biome, biomeListMap.getOrDefault(biome, EMPTY_LIST))))
        );
    }

    private static final BiomeClimateSettings EMPTY = new BiomeClimateSettings();

    // not really need to know if is server for 1.20
    @Deprecated
    public static BiomeClimateSettings getBiomeClimateSettings(Biome biome, boolean isServer) {
        return BIOME_CLIMATE_MAP.getOrDefault(biome, EMPTY);
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    @Deprecated(forRemoval = true, since = "0.11")
    public static void updateTemperature(Level level, SolarTerm solarTermIndex) {
        // boolean isServer = level instanceof ServerLevel;
        // level.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        // {
        //     var temperature = biome.getModifiedClimateSettings().temperature() > SNOW_LEVEL ?
        //             Math.max(SNOW_LEVEL + 0.001F, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange()) :
        //             Math.min(SNOW_LEVEL, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange());
        //
        //     BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, temperature);
        //
        // }));
    }

    @Deprecated(forRemoval = true, since = "0.11")
    public static float agent$GetBaseTemperature(Biome biome) {
        return biome.getBaseTemperature();
    }

    @Deprecated(forRemoval = true, since = "0.11")
    public static float fixTemp(Level level, Biome biome, float temp) {
        // SolarTerm solarTermIndex = EclipticUtil.getNowSolarTerm(level);
        // float temperatureBiome = biome.getModifiedClimateSettings().temperature();
        // float temperatureGround = temperatureBiome > SNOW_LEVEL ?
        //         Math.max(SNOW_LEVEL + 0.001F, temperatureBiome + solarTermIndex.getTemperatureChange()) :
        //         Math.min(SNOW_LEVEL, temperatureBiome + solarTermIndex.getTemperatureChange());
        // temp += -temperatureGround + temperatureBiome;
        return temp;
    }

    public static boolean agent$hasPrecipitation(Biome biome) {
        // return !getTag(biome).equals(ClimateTypeBiomeTags.RAINLESS);
        return getTag(biome) != ClimateTypeBiomeTags.RAINLESS;
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
        return BIOME_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.RAINLESS);
    }

    // TODO：Clear it on client exit a level
    public static void putTag(RegistryAccess registryAccess, boolean isServer) {
        var useMap = BIOME_TAG_KEY_MAP;
        useMap.clear();
        SMALL_BIOME_MAP.clear();

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
                    if (!holder.value().getModifiedClimateSettings().hasPrecipitation()) {
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

    public static void clearOnClientExitOrServerClose() {
        BiomeClimateManager.BIOME_CLIMATE_MAP.clear();
        BiomeClimateManager.SMALL_BIOME_MAP.clear();
        BiomeClimateManager.BIOME_TAG_KEY_MAP.clear();
    }
}

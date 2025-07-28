package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;

public class BiomeClimateManager {
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public final static Map<Biome, BiomeClimateSettings> CLIENT_CLIMATE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, TagKey<Biome>> CLIENT_BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> SEASON_PHASE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> CLIENT_SEASON_PHASE_MAP = new IdentityHashMap<>();

    // biome rain
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CLIENT_CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();

    // snow term
    public static final Map<Biome, ISnowTerm> CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();
    public static final Map<Biome, ISnowTerm> CLIENT_CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        // resetBiomeClimateMap(registryAccess, isServer ? BIOME_CLIMATE_MAP : CLIENT_CLIMATE_MAP);
        // resetSeasonPhaseMap(registryAccess, isServer ? SEASON_PHASE_MAP : CLIENT_SEASON_PHASE_MAP);

        resetSomeMap(registryAccess, ESRegistries.BIOME_CLIMATE_SETTING,
                isServer ? BIOME_CLIMATE_MAP : CLIENT_CLIMATE_MAP,
                (customRainBuilder) -> Pair.of(customRainBuilder.biomes(), customRainBuilder),
                (Map<Biome, List<BiomesClimateSettings>> map, Pair<Holder<Biome>, BiomesClimateSettings> pair) -> {
                    List<BiomesClimateSettings> biomesClimateSettingsList =
                            map.computeIfAbsent(pair.getFirst().value(), k -> new ArrayList<>());
                    biomesClimateSettingsList.add(pair.getSecond());
                },
                List::of,
                BiomeClimateSettings::new

        );
        resetSomeMap(registryAccess, ESRegistries.SEASON_CYCLE,
                isServer ? SEASON_PHASE_MAP : CLIENT_SEASON_PHASE_MAP,
                (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder.localMapping().combine())),
                (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                () -> new EnumMap<SolarTerm, Holder<SeasonPhase>>(SolarTerm.class),
                (biome, map) -> map
        );
        resetSomeMap(registryAccess, ESRegistries.BIOME_RAIN,
                isServer ? CUSTOME_BIOME_RAIN_MAP : CLIENT_CUSTOME_BIOME_RAIN_MAP,
                (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder.build())),
                (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                Map::<SolarTerm, CustomRain>of,
                (biome, map) -> map
        );
        resetSomeMap(registryAccess, ESRegistries.SNOW_TERM,
                isServer ? CUSTOM_SNOW_TERM_MAP : CLIENT_CUSTOM_SNOW_TERM_MAP,
                (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder)),
                (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                () -> (CustomSnowTerm) null,
                (biome, map) -> map
        );
        putTag(registryAccess, isServer);
    }

    public static <T, U, R, S> void resetSomeMap(RegistryAccess registryAccess,
                                                 ResourceKey<Registry<T>> resourceKey,
                                                 Map<Biome, S> useMap,
                                                 Function<T, Pair<HolderSet<Biome>, U>> biomeTransfer,
                                                 BiConsumer<Map<Biome, R>, Pair<Holder<Biome>, U>> singleDeal,
                                                 Supplier<R> emptyInstance,
                                                 BiFunction<Biome, R, S> mapSaver) {
        useMap.clear();
        Map<Biome, R> biomeUIdentityHashMap = new IdentityHashMap<>();

        var registry = registryAccess.registry(resourceKey);
        if (registry.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(resourceKey);
        } else {
            Registry<T> biomesClimateSettings = registry.get();
            for (var entry : biomesClimateSettings.entrySet()) {
                var pair = biomeTransfer.apply(entry.getValue());
                for (Holder<Biome> next : pair.getFirst()) {
                    singleDeal.accept(biomeUIdentityHashMap, Pair.of(next, pair.getSecond()));
                    // biomeUIdentityHashMap.put(next.value(), singleDeal.apply(pair));
                }
            }
        }
        Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
        var objects = emptyInstance.get();
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
                useMap.put(biome, mapSaver.apply(biome, biomeUIdentityHashMap.getOrDefault(biome, objects))))
        );
    }

    // public static void resetSeasonPhaseMap(RegistryAccess registryAccess, Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> useMap) {
    //     useMap.clear();
    //     Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> biomeListMap = new IdentityHashMap<>();
    //
    //     var registry = registryAccess.registry(ESRegistries.SEASON_CYCLE);
    //     if (registry.isEmpty()) {
    //         SimpleUtil.warningForModWrongCalling(ESRegistries.SEASON_CYCLE);
    //     } else {
    //         Registry<SeasonCycle> biomesClimateSettings = registry.get();
    //         for (Map.Entry<ResourceKey<SeasonCycle>, SeasonCycle> entry : biomesClimateSettings.entrySet()) {
    //             SeasonCycle value = entry.getValue();
    //             EnumMap<SolarTerm, Holder<SeasonPhase>> combine = value.localMapping().combine();
    //             for (Holder<Biome> next : value.biomes()) {
    //                 biomeListMap.put(next.value(), combine);
    //             }
    //         }
    //     }
    //     Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
    //     Map<SolarTerm, Holder<SeasonPhase>> objects = Map.of();
    //     biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
    //             useMap.put(biome, biomeListMap.getOrDefault(biome, objects)))
    //     );
    // }
    //
    // public static void resetBiomeClimateMap(RegistryAccess registryAccess, Map<Biome, BiomeClimateSettings> useMap) {
    //     useMap.clear();
    //     Map<Biome, List<BiomesClimateSettings>> biomeListMap = new IdentityHashMap<>();
    //     Optional<Registry<BiomesClimateSettings>> registry = registryAccess.registry(ESRegistries.BIOME_CLIMATE_SETTING);
    //     if (registry.isEmpty()) {
    //         SimpleUtil.warningForModWrongCalling(ESRegistries.BIOME_CLIMATE_SETTING);
    //     } else {
    //         Registry<BiomesClimateSettings> biomesClimateSettings = registry.get();
    //         for (Map.Entry<ResourceKey<BiomesClimateSettings>, BiomesClimateSettings> entry : biomesClimateSettings.entrySet()) {
    //             BiomesClimateSettings value = entry.getValue();
    //             for (Holder<Biome> next : value.biomes()) {
    //                 List<BiomesClimateSettings> biomesClimateSettingsList =
    //                         biomeListMap.computeIfAbsent(next.value(), k -> new ArrayList<>());
    //                 biomesClimateSettingsList.add(value);
    //             }
    //         }
    //     }
    //     Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
    //     List<BiomesClimateSettings> objects = List.of();
    //     biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
    //             useMap.put(biome, new BiomeClimateSettings(biome, biomeListMap.getOrDefault(biome, objects))))
    //     );
    // }

    public static final BiomeClimateSettings EMPTY = new BiomeClimateSettings();

    public static BiomeClimateSettings getBiomeClimateSettings(Biome biome, boolean isServer) {
        return isServer ?
                BIOME_CLIMATE_MAP.getOrDefault(biome, EMPTY) :
                CLIENT_CLIMATE_MAP.getOrDefault(biome, EMPTY);
    }

    public static Map<SolarTerm, CustomRain> getCustomRain(Biome biome, boolean isServer) {
        return isServer?
                CUSTOME_BIOME_RAIN_MAP.getOrDefault(biome, Map.of()) :
                CLIENT_CUSTOME_BIOME_RAIN_MAP.getOrDefault(biome, Map.of());
    }

    public static @Nullable ISnowTerm getCustomSnowTerm(Biome biome, boolean isServer) {
        return isServer?
                CUSTOM_SNOW_TERM_MAP.getOrDefault(biome, null) :
                CLIENT_CUSTOM_SNOW_TERM_MAP.getOrDefault(biome, null);
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    @Deprecated(forRemoval = true)
    public static void updateTemperature(Level level, SolarTerm solarTermIndex) {
        // boolean isServer = !level.isClientSide();
        // level.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        // {
        //     float temperature = biome.getModifiedClimateSettings().temperature() > SNOW_LEVEL ?
        //             Math.maxTime(SNOW_LEVEL + 0.001F, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange()) :
        //             Math.minTime(SNOW_LEVEL, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange());
        //     temperature = solarTermIndex.getTemperatureChange();
        //     // if (isServer) {
        //     //     BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, temperature);
        //     // } else {
        //     //     CLIENT_BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, temperature);
        //     // }
        //
        //     // clean temperature change
        //     // var oldClimateSettings = biome.climateSettings;
        //     // biome.climateSettings = new Biome.ClimateSettings(
        //     //         oldClimateSettings.hasPrecipitation(),
        //     //         temperature,
        //     //         oldClimateSettings.temperatureModifier(),
        //     //         oldClimateSettings.downfall());
        // }));
    }

    @Deprecated(forRemoval = true)
    public static float agent$GetBaseTemperature(Biome biome) {
        // float f = getDefaultTemperature(biome, true);
        // if (f == DEFAULT_TEMPERATURE) {
        //     float f2 = getDefaultTemperature(biome, false);
        //     f = f2 != f ? f2 : f;
        // }
        return biome.getBaseTemperature();
    }

    @Deprecated
    public static boolean agent$hasPrecipitation(Biome biome) {
        return ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindTag() != ClimateTypeBiomeTags.RAINLESS;
        // return WeatherManager.getPrecipitationAt(biome, BlockPos.ZERO)!= Biome.Precipitation.NONE;
    }

    @Deprecated(forRemoval = true)
    public static float fixTemp(Level level, Biome biome, float temp) {
        // SolarTerm solarTermIndex = EclipticUtil.getNowSolarTerm(level);
        // float temperatureBiome = biome.getModifiedClimateSettings().temperature();
        // float temperatureGround = temperatureBiome > SNOW_LEVEL ?
        //         Math.maxTime(SNOW_LEVEL + 0.001F, temperatureBiome + solarTermIndex.getTemperatureChange()) :
        //         Math.minTime(SNOW_LEVEL, temperatureBiome + solarTermIndex.getTemperatureChange());
        // temp += -temperatureGround + temperatureBiome;
        return temp;
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
        TagKey<Biome> biomeTagKey = CLIENT_BIOME_TAG_KEY_MAP.getOrDefault(biome, null);
        if (biomeTagKey != null) return biomeTagKey;
        return BIOME_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.RAINLESS);
    }

    // TODO：Clear it on client exit a level
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
                    ((IBiomeTagHolder) (Object) holder.value()).eclipticseasons$setTag(tag.get());
                } else {
                    // 我们按照降雨量进行分配，如果无预测则无雨
                    int size = ClimateTypeBiomeTags.COMMON_BIOME_TYPES.size();
                    int index = Mth.clamp(Mth.floor(holder.value().getModifiedClimateSettings().downfall() * size), 0, size - 1);
                    if (!holder.value().getModifiedClimateSettings().hasPrecipitation()) {
                        index = 0;
                    }
                    TagKey<Biome> biomeTagKey = ClimateTypeBiomeTags.COMMON_BIOME_TYPES.get(index);
                    useMap.put(holder.value(), biomeTagKey);
                    ((IBiomeTagHolder) (Object) holder.value()).eclipticseasons$setTag(biomeTagKey);
                }

                if (holder.is(ClimateTypeBiomeTags.IS_SMALL)) {
                    SMALL_BIOME_MAP.put(holder.value(), isServer);
                    ((IBiomeTagHolder) (Object) holder.value()).eclipticseasons$setSmall(true);
                }
            }
        }

    }

    public static void clearOnClientExitOrServerClose() {
        BiomeClimateManager.BIOME_CLIMATE_MAP.clear();
        BiomeClimateManager.SMALL_BIOME_MAP.clear();
        BiomeClimateManager.BIOME_TAG_KEY_MAP.clear();
        BiomeClimateManager.CLIENT_CLIMATE_MAP.clear();
        BiomeClimateManager.CLIENT_BIOME_TAG_KEY_MAP.clear();
        BiomeClimateManager.SEASON_PHASE_MAP.clear();
        BiomeClimateManager.CLIENT_SEASON_PHASE_MAP.clear();
        BiomeClimateManager.CUSTOME_BIOME_RAIN_MAP.clear();
        BiomeClimateManager.CLIENT_CUSTOME_BIOME_RAIN_MAP.clear();
        BiomeClimateManager.CUSTOM_SNOW_TERM_MAP.clear();
        BiomeClimateManager.CLIENT_CUSTOM_SNOW_TERM_MAP.clear();
    }

    public static boolean isServerInstance(Biome value) {
        return BIOME_CLIMATE_MAP.containsKey(value);
    }
}

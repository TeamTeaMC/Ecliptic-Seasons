package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BiomeClimateManager {
    // 由于在1.20时代，客户端与单人服务器端对象未分离，因此这里不能作为评判依据
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> SEASON_PHASE_MAP = new IdentityHashMap<>();

    // biome rain
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();

    // snow term
    public static final Map<Biome, ISnowTerm> CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        Optional<Registry<BiomesClimateSettings>> registry = registryAccess.registry(ESRegistries.BIOME_CLIMATE_SETTING);
        var registry2 = registryAccess.registry(ESRegistries.SEASON_PHASE);
        var registry3 = registryAccess.registry(ESRegistries.SEASON_CYCLE);
        var registry4 = registryAccess.registry(ESRegistries.BIOME_RAIN);
        var registry5 = registryAccess.registry(ESRegistries.SNOW_TERM);
        if (registry.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.BIOME_CLIMATE_SETTING);
        } else if (registry2.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.SEASON_PHASE);
        } else if (registry3.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.SEASON_CYCLE);
        } else if (registry4.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.BIOME_RAIN);
        } else if (registry5.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.SNOW_TERM);
        } else {
            // if (isServer) {
            //     Registry<BiomesClimateSettings> biomesClimateSettings = registry.get();
            //     resetBiomeClimateMap(registryAccess, biomesClimateSettings, BIOME_CLIMATE_MAP);
            //     Registry<SeasonCycle> seasonCycles = registry3.get();
            //     resetSeasonPhaseMap(registryAccess, seasonCycles, SEASON_PHASE_MAP);
            // } else {
            //     if (ClientCon.biomeDataPackCache != null) {
            //         List<BiomesClimateSettings> build = ClientCon.biomeDataPackCache.build(registryAccess, BiomesClimateSettings.class);
            //         resetBiomeClimateMap(registryAccess, build, BIOME_CLIMATE_MAP);
            //     }
            //
            //     if (ClientCon.seasonCycleCache != null) {
            //         List<SeasonCycle> build = ClientCon.seasonCycleCache.build(registryAccess, SeasonCycle.class);
            //         resetSeasonPhaseMap(registryAccess, build, SEASON_PHASE_MAP);
            //     }
            // }
            if (isServer || ClientCon.biomeDataPackCache != null) {
                resetSomeMap(registryAccess, isServer ? registry.get() : ClientCon.biomeDataPackCache.build(registryAccess, BiomesClimateSettings.class),
                        BIOME_CLIMATE_MAP,
                        (customRainBuilder) -> Pair.of(customRainBuilder.biomes(), customRainBuilder),
                        (Map<Biome, List<BiomesClimateSettings>> map, Pair<Holder<Biome>, BiomesClimateSettings> pair) -> {
                            List<BiomesClimateSettings> biomesClimateSettingsList =
                                    map.computeIfAbsent(pair.getFirst().value(), k -> new ArrayList<>());
                            biomesClimateSettingsList.add(pair.getSecond());
                        },
                        List::of,
                        BiomeClimateSettings::new

                );
            }
            if (isServer || ClientCon.seasonCycleCache != null) {
                resetSomeMap(registryAccess, isServer ? registry3.get() : ClientCon.seasonCycleCache.build(registryAccess, SeasonCycle.class),
                        SEASON_PHASE_MAP,
                        (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder.localMapping().combine())),
                        (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                        () -> new EnumMap<SolarTerm, Holder<SeasonPhase>>(SolarTerm.class),
                        (biome, map) -> map
                );
            }
            if (isServer || ClientCon.biomeRainCache != null) {
                resetSomeMap(registryAccess, isServer ? registry4.get() : ClientCon.biomeRainCache.build(registryAccess, CustomRainBuilder.class),
                        CUSTOME_BIOME_RAIN_MAP,
                        (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder.build())),
                        (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                        Map::<SolarTerm, CustomRain>of,
                        (biome, map) -> map
                );
            }
            if (isServer || ClientCon.snowTermCache != null) {
                resetSomeMap(registryAccess, isServer ? registry5.get() : ClientCon.snowTermCache.build(registryAccess, CustomSnowTerm.class),
                        CUSTOM_SNOW_TERM_MAP,
                        (customRainBuilder -> Pair.of(customRainBuilder.biomes(), customRainBuilder)),
                        (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                        () -> (CustomSnowTerm) null,
                        (biome, map) -> map
                );
            }
            putTag(registryAccess, isServer);
        }
    }

    public static <T, U, R, S> void resetSomeMap(RegistryAccess registryAccess,
                                                 Iterable<T> registry,
                                                 Map<Biome, S> useMap,
                                                 Function<T, Pair<HolderSet<Biome>, U>> biomeTransfer,
                                                 BiConsumer<Map<Biome, R>, Pair<Holder<Biome>, U>> singleDeal,
                                                 Supplier<R> emptyInstance,
                                                 BiFunction<Biome, R, S> mapSaver) {
        useMap.clear();
        Map<Biome, R> biomeUIdentityHashMap = new IdentityHashMap<>();

        for (var value : registry) {
            var pair = biomeTransfer.apply(value);
            for (Holder<Biome> next : pair.getFirst()) {
                singleDeal.accept(biomeUIdentityHashMap, Pair.of(next, pair.getSecond()));
                // biomeUIdentityHashMap.put(next.value(), singleDeal.apply(pair));
            }
        }

        Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
        var objects = emptyInstance.get();
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
                useMap.put(biome, mapSaver.apply(biome, biomeUIdentityHashMap.getOrDefault(biome, objects))))
        );
    }

    // public static void resetSeasonPhaseMap(RegistryAccess registryAccess, Iterable<SeasonCycle> seasonCycles, Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> useMap) {
    //     useMap.clear();
    //     Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> biomeListMap = new IdentityHashMap<>();
    //
    //     for (var entry : seasonCycles) {
    //         EnumMap<SolarTerm, Holder<SeasonPhase>> combine = entry.localMapping().combine();
    //         for (Holder<Biome> next : entry.biomes()) {
    //             biomeListMap.put(next.value(), combine);
    //         }
    //     }
    //
    //     Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
    //     Map<SolarTerm, Holder<SeasonPhase>> objects = Map.of();
    //     biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
    //             useMap.put(biome, biomeListMap.getOrDefault(biome, objects)))
    //     );
    // }
    //
    // public static void resetBiomeClimateMap(RegistryAccess registryAccess, Iterable<BiomesClimateSettings> biomesClimateSettings, Map<Biome, BiomeClimateSettings> useMap) {
    //     useMap.clear();
    //
    //     Map<Biome, List<BiomesClimateSettings>> biomeListMap = new IdentityHashMap<>();
    //     for (var value : biomesClimateSettings) {
    //         for (Holder<Biome> next : value.biomes()) {
    //             List<BiomesClimateSettings> biomesClimateSettingsList =
    //                     biomeListMap.computeIfAbsent(next.value(), k -> new ArrayList<>());
    //             biomesClimateSettingsList.add(value);
    //         }
    //     }
    //     Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
    //     List<BiomesClimateSettings> EMPTY_LIST = List.of();
    //     biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
    //             useMap.put(biome, new BiomeClimateSettings(biome, biomeListMap.getOrDefault(biome, EMPTY_LIST))))
    //     );
    // }

    public static final BiomeClimateSettings EMPTY = new BiomeClimateSettings();

    // not really need to know if is server for 1.20
    @Deprecated
    public static BiomeClimateSettings getBiomeClimateSettings(Biome biome, boolean isServer) {
        return BIOME_CLIMATE_MAP.getOrDefault(biome, EMPTY);
    }


    public static Map<SolarTerm, CustomRain> getCustomRain(Biome biome, boolean isServer) {
        return CUSTOME_BIOME_RAIN_MAP.getOrDefault(biome, Map.of());
    }

    public static @Nullable ISnowTerm getCustomSnowTerm(Biome biome, boolean isServer) {
        return CUSTOM_SNOW_TERM_MAP.getOrDefault(biome, null);
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

package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeFilters;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.RegistryFilter;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
import java.util.stream.Collectors;

public class BiomeClimateManager {
    // 由于在1.20时代，客户端与单人服务器端对象未分离，因此这里不能作为评判依据
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> SEASON_PHASE_MAP = new IdentityHashMap<>();

    public static final Map<Biome, TagKey<Biome>> BIOME_COLOR_TAG_KEY_MAP = new IdentityHashMap<>(128);

    // biome rain
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();

    // snow term
    public static final Map<Biome, ISnowTerm> CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();

    public static final Map<Biome, Holder<Biome>> WEATHER_REGION_MAP = new IdentityHashMap<>();

    public static void resetBiomeTags(RegistryAccess registryAccess, boolean isServer) {
        Optional<Registry<Biome>> registry = registryAccess.registry(Registries.BIOME);
        if (registry.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(Registries.BIOME);
        } else {
            putTag(registryAccess, isServer);
            putColorTag(registryAccess, isServer);
            resetAgroTag(registryAccess, isServer);
        }
    }

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        Optional<Registry<BiomesClimateSettings>> registry = registryAccess.registry(ESRegistries.BIOME_CLIMATE_SETTING);
        var registry2 = registryAccess.registry(ESRegistries.SEASON_PHASE);
        var registry3 = registryAccess.registry(ESRegistries.SEASON_CYCLE);
        var registry4 = registryAccess.registry(ESRegistries.BIOME_RAIN);
        var registry5 = registryAccess.registry(ESRegistries.SNOW_TERM);
        var registry6 = isServer ? registryAccess.registry(ESRegistries.WEATHER_REGION) : null;
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
        } else if (registry6 != null && registry6.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.WEATHER_REGION);
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
            if (isServer) {
                resetSomeMap(registryAccess, registry6.get(),
                        WEATHER_REGION_MAP,
                        (customRainBuilder -> Pair.of(customRainBuilder.sub(), customRainBuilder.core())),
                        (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                        () -> (Holder<Biome>) null,
                        (biome, map) -> map
                );
            }

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
        return ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindTag() != ClimateTypeBiomeTags.RAINLESS;
    }


    public static @Nullable Holder<Biome> getHolder(RegistryAccess registryAccess, Biome biome) {
        return registryAccess.registryOrThrow(Registries.BIOME)
                .holders()
                .filter(biomeReference -> biomeReference.value() == biome)
                .findFirst().orElse(null);
    }

    public static @Nullable Holder<Biome> getHolder(Registry<Biome> registryAccess, Biome biome) {
        return registryAccess
                .holders()
                .filter(biomeReference -> biomeReference.value() == biome)
                .findFirst().orElse(null);
    }

    public static TagKey<Biome> getTag(Biome biome) {
        // return getTag(WeatherManager.getMainServerLevel(), biome);
        return BIOME_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.RAINLESS);
    }

    public static TagKey<Biome> getColorTag(Biome biome) {
        return BIOME_COLOR_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.NONE_COLOR_CHANGE);
    }

    public static Holder<Biome> getWeatherRegionOnwer(Biome biome) {
        return WEATHER_REGION_MAP.getOrDefault(biome, null);
    }

    public static void resetAgroTag(RegistryAccess registryAccess, boolean isServer) {
        applyBiomeTags(
                registryAccess,
                new HashSet<>(ClimateTypeBiomeTags.OVERWORLD_AGRO_BIOME_TYPES),
                ClimateTypeFilters.OVERWORLD_AGRO_BIOME_PRESENT
        );
    }

    public static void putColorTag(RegistryAccess registryAccess, boolean isServer) {
        applyBiomeTags(
                registryAccess,
                BIOME_COLOR_TAG_KEY_MAP,
                new HashSet<>(ClimateTypeBiomeTags.BIOME_COLOR_TYPES),
                ClimateTypeFilters.COLOR_BIOME_PRESENT,
                (holder) -> ClimateTypeBiomeTags.NONE_COLOR_CHANGE,
                (biome, tag) -> ((IBiomeTagHolder) (Object) biome).eclipticseasons$setColorTag(tag)
        );
    }

    public static void putTag(RegistryAccess registryAccess, boolean isServer) {
        // set small
        for (Biome biome : SMALL_BIOME_MAP.entrySet().stream().filter(entry -> entry.getValue() == isServer).map(Map.Entry::getKey).toList()) {
            SMALL_BIOME_MAP.remove(biome);
        }
        var biomeRegistry = registryAccess.registry(Registries.BIOME);
        if (biomeRegistry.isPresent()) {
            Optional<HolderSet.Named<Biome>> biomeNamed = biomeRegistry.get().getTag(ClimateTypeBiomeTags.IS_SMALL);
            if (biomeNamed.isPresent()) {
                for (var holder : biomeNamed.get()) {
                    SMALL_BIOME_MAP.put(holder.value(), isServer);
                    ((IBiomeTagHolder) (Object) holder.value()).eclipticseasons$setSmall(true);
                }
            }
        }

        // basic
        applyBiomeTags(
                registryAccess,
                BIOME_TAG_KEY_MAP,
                new HashSet<>(ClimateTypeBiomeTags.BIOME_TYPES),
                ClimateTypeFilters.BIOME_PRESENT,
                (holder) -> {
                    int size = ClimateTypeBiomeTags.COMMON_BIOME_TYPES.size();
                    int index = Mth.clamp(Mth.floor(holder.value().getModifiedClimateSettings().downfall() * size), 0, size - 1);
                    if (!holder.value().getModifiedClimateSettings().hasPrecipitation()) index = 0;
                    return ClimateTypeBiomeTags.COMMON_BIOME_TYPES.get(index);
                },
                (biome, tag) -> ((IBiomeTagHolder) (Object) biome).eclipticseasons$setTag(tag)
        );
    }

    public static void applyBiomeTags(
            RegistryAccess registryAccess,
            Set<TagKey<Biome>> knownTags,
            Map<TagKey<Biome>, RegistryFilter<Biome>> filters
    ) {
        applyBiomeTags(
                registryAccess,
                new IdentityHashMap<>(),
                knownTags,
                filters,
                (holder) -> null,
                (biome, tag) -> {
                }
        );
    }

    public static void applyBiomeTags(
            RegistryAccess registryAccess,
            Map<Biome, TagKey<Biome>> useMap,
            Set<TagKey<Biome>> knownTags,
            Map<TagKey<Biome>, RegistryFilter<Biome>> filters,
            Function<Holder<Biome>, TagKey<Biome>> defaultTag,
            BiConsumer<Biome, TagKey<Biome>> callback
    ) {
        useMap.clear();
        var biomeRegistry = registryAccess.registry(Registries.BIOME);
        if (biomeRegistry.isEmpty()) return;
        Registry<Biome> registry = biomeRegistry.get();
        Set<Holder<Biome>> biomeNotSet = new HashSet<>();

        for (var holder : registry.holders().toList()) {
            var tag = knownTags.stream().filter(holder::is).findFirst();
            if (tag.isPresent()) {
                useMap.put(holder.value(), tag.get());
            } else {
                biomeNotSet.add(holder);
            }
        }

        for (var entry : filters.entrySet()) {
            for (Holder<Biome> holder : entry.getValue().toHolders(registry)) {
                useMap.putIfAbsent(holder.value(), entry.getKey());
                biomeNotSet.remove(holder);
            }
        }

        for (var holder : biomeNotSet) {
            TagKey<Biome> apply = defaultTag.apply(holder);
            if (apply != null) useMap.put(holder.value(), apply);
        }

        useMap.forEach(callback);

        updateTagInVanilla(knownTags, useMap, registry);
    }

    public static void updateTagInVanilla(Set<TagKey<Biome>> biomeTypes, Map<Biome, TagKey<Biome>> useMap, Registry<Biome> biomeRegistry) {
        if (CommonConfig.Debug.disableUniqueRebindingBiomeTags.get()) return;

        Map<TagKey<Biome>, List<Holder<Biome>>> biomeMap = biomeRegistry.getTags()
                .filter(p -> !biomeTypes.contains(p.getFirst()))
                .collect(Collectors.toMap(
                        Pair::getFirst,
                        p -> p.getSecond().stream().toList()
                ));

        useMap.forEach((biome, biomeTagKey) -> {
            Holder<Biome> holder = getHolder(biomeRegistry, biome);
            if (holder != null) {
                List<Holder<Biome>> holders = biomeMap.computeIfAbsent(biomeTagKey, (b) -> new ArrayList<>());
                holders.add(holder);
            }
        });
        biomeRegistry.bindTags(biomeMap);
    }


    public static void clearOnClientExitOrServerClose() {
        BiomeClimateManager.WEATHER_REGION_MAP.clear();
        BiomeClimateManager.BIOME_CLIMATE_MAP.clear();
        BiomeClimateManager.SMALL_BIOME_MAP.clear();
        BiomeClimateManager.BIOME_TAG_KEY_MAP.clear();

        BiomeClimateManager.SEASON_PHASE_MAP.clear();
        BiomeClimateManager.CUSTOME_BIOME_RAIN_MAP.clear();
        BiomeClimateManager.CUSTOM_SNOW_TERM_MAP.clear();
    }
}

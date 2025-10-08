package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeFilters;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.RegistryFilter;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class BiomeClimateManager {
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public final static Map<Biome, BiomeClimateSettings> CLIENT_CLIMATE_MAP = new IdentityHashMap<>();

    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, TagKey<Biome>> CLIENT_BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);

    public static final Map<Biome, TagKey<Biome>> BIOME_COLOR_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, TagKey<Biome>> CLIENT_BIOME_COLOR_TAG_KEY_MAP = new IdentityHashMap<>(128);

    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> SEASON_PHASE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, Map<SolarTerm, Holder<SeasonPhase>>> CLIENT_SEASON_PHASE_MAP = new IdentityHashMap<>();

    // biome rain
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();
    public static final Map<Biome, Map<SolarTerm, CustomRain>> CLIENT_CUSTOME_BIOME_RAIN_MAP = new IdentityHashMap<>();

    // snow term
    public static final Map<Biome, ISnowTerm> CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();
    public static final Map<Biome, ISnowTerm> CLIENT_CUSTOM_SNOW_TERM_MAP = new IdentityHashMap<>();

    public static final Map<Biome, Holder<Biome>> WEATHER_REGION_MAP = new IdentityHashMap<>();

    // snow line
    public static final Map<Biome, Integer> SNOW_LINE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, Integer> CLIENT_SNOW_LINE_MAP = new IdentityHashMap<>();

    public static void resetBiomeTags(RegistryAccess registryAccess, boolean isServer) {
        putTag(registryAccess, isServer);
        putColorTag(registryAccess, isServer);
        resetAgroTag(registryAccess, isServer);
    }

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        // resetBiomeClimateMap(registryAccess, isServer ? BIOME_CLIMATE_MAP : CLIENT_CLIMATE_MAP);
        // resetSeasonPhaseMap(registryAccess, isServer ? SEASON_PHASE_MAP : CLIENT_SEASON_PHASE_MAP);
        if (isServer) {
            resetSomeMap(registryAccess, ESRegistries.WEATHER_REGION,
                    WEATHER_REGION_MAP,
                    (customRainBuilder -> Pair.of(customRainBuilder.sub(), customRainBuilder.core())),
                    (map, pair) -> map.put(pair.getFirst().value(), pair.getSecond()),
                    () -> (Holder<Biome>) null,
                    (biome, map) -> map
            );
        }

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
                () -> new Enum2ObjectMap<SolarTerm, Holder<SeasonPhase>>(SolarTerm.class),
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

        setSnowLine(registryAccess, isServer ? SNOW_LINE_MAP : CLIENT_SNOW_LINE_MAP);
    }

    public static void setSnowLine(RegistryAccess registryAccess, Map<Biome, Integer> biomeIntegerMap) {
        Optional<Registry<Biome>> biomeRegistry = registryAccess.registry(Registries.BIOME);
        if (biomeRegistry.isPresent()) {
            biomeIntegerMap.clear();
            for (var serializable : CommonConfig.Snow.biomeSnowLines.get()) {
                String biomeOrTag = serializable.get(0) + "";
                int snowLineHeight = Integer.parseInt(serializable.get(1) + "");
                if (biomeOrTag.startsWith("#")) {
                    TagKey<Biome> biomeTagKey = TagKey.create(Registries.BIOME, ResourceLocation.parse(biomeOrTag.substring(1, biomeOrTag.length() - 1)));
                    Optional<HolderSet.Named<Biome>> tag = biomeRegistry.get().getTag(biomeTagKey);
                    if (tag.isPresent()) {
                        for (Holder<Biome> biomeHolder : tag.get()) {
                            biomeIntegerMap.putIfAbsent(biomeHolder.value(), snowLineHeight);
                        }
                    }
                } else {
                    Optional<Holder.Reference<Biome>> holder = biomeRegistry.get().getHolder(ResourceKey.create(Registries.BIOME, ResourceLocation.parse(biomeOrTag)));
                    if (holder.isPresent()) {
                        biomeIntegerMap.putIfAbsent(holder.get().value(), snowLineHeight);
                    }
                }
            }
        }
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
            Set<Map.Entry<ResourceKey<T>, T>> entries = biomesClimateSettings.entrySet();

            // sort
            Optional<Holder.Reference<T>> holder = biomesClimateSettings.getHolder(0);
            if (holder.isPresent() && holder.get().value() instanceof Comparable<?>) {
                List<Map.Entry<ResourceKey<T>, T>> sortedEntries = entries.stream()
                        .sorted(Comparator.comparing(e -> (Comparable) e.getValue()))
                        .toList();
                entries = new LinkedHashSet<>(sortedEntries);
            }

            entries = ESSortInfo.sorted(entries);

            for (var entry : entries) {
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

    public static final BiomeClimateSettings EMPTY = new BiomeClimateSettings();

    public static BiomeClimateSettings getBiomeClimateSettings(Biome biome, boolean isServer) {
        BiomeClimateSettings biomeClimateSettings = isServer ?
                BIOME_CLIMATE_MAP.get(biome) :
                CLIENT_CLIMATE_MAP.get(biome);
        return biomeClimateSettings == null ? EMPTY : biomeClimateSettings;
    }

    public static Map<SolarTerm, CustomRain> getCustomRain(Biome biome, boolean isServer) {
        Map<SolarTerm, CustomRain> solarTermCustomRainMap = isServer ?
                CUSTOME_BIOME_RAIN_MAP.get(biome) :
                CLIENT_CUSTOME_BIOME_RAIN_MAP.get(biome);
        return solarTermCustomRainMap == null ? Map.of() : solarTermCustomRainMap;
    }

    public static @Nullable ISnowTerm getCustomSnowTerm(Biome biome, boolean isServer) {
        return isServer ?
                CUSTOM_SNOW_TERM_MAP.get(biome) :
                CLIENT_CUSTOM_SNOW_TERM_MAP.get(biome);
    }

    public static int getSnowLine(Biome biome, boolean isServer) {
        Integer i = isServer ?
                SNOW_LINE_MAP.get(biome) :
                CLIENT_SNOW_LINE_MAP.get(biome);
        return i == null ? Integer.MAX_VALUE : i;
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    @Deprecated(forRemoval = true)
    public static void updateTemperature(Level level, SolarTerm solarTermIndex) {
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
        TagKey<Biome> biomeTagKey = CLIENT_BIOME_TAG_KEY_MAP.getOrDefault(biome, null);
        if (biomeTagKey != null) return biomeTagKey;
        return BIOME_TAG_KEY_MAP.getOrDefault(biome, ClimateTypeBiomeTags.RAINLESS);
    }

    public static TagKey<Biome> getColorTag(Biome biome) {
        TagKey<Biome> biomeTagKey = CLIENT_BIOME_COLOR_TAG_KEY_MAP.getOrDefault(biome, null);
        if (biomeTagKey != null) return biomeTagKey;
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
                isServer ? BIOME_COLOR_TAG_KEY_MAP : CLIENT_BIOME_COLOR_TAG_KEY_MAP,
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


            for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.BIOME_TYPES) {
                TagKey<Biome> oldTag = ClimateTypeBiomeTags.create(biomeType.location().getPath().replace("rain/", ""));
                Optional<HolderSet.Named<Biome>> oldTagApplied = biomeRegistry.get().getTag(oldTag);
                if (oldTagApplied.isPresent() && oldTagApplied.get().size() > 0) {
                    String message = "[%s] was deprecated now, please use [%s] instead.".formatted(
                            oldTag.location(), biomeType.location());
                    message += "\nBiome list: " + String.join(",", oldTagApplied.get().stream()
                            .map(h -> h.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse(null))
                            .filter(Objects::nonNull)
                            .toList());
                    EclipticSeasons.LOGGER.error(message);
                }
            }
        }

        // basic
        applyBiomeTags(
                registryAccess,
                isServer ? BIOME_TAG_KEY_MAP : CLIENT_BIOME_TAG_KEY_MAP,
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
        BiomeClimateManager.CLIENT_CLIMATE_MAP.clear();
        BiomeClimateManager.CLIENT_BIOME_TAG_KEY_MAP.clear();
        BiomeClimateManager.SEASON_PHASE_MAP.clear();
        BiomeClimateManager.CLIENT_SEASON_PHASE_MAP.clear();
        BiomeClimateManager.CUSTOME_BIOME_RAIN_MAP.clear();
        BiomeClimateManager.CLIENT_CUSTOME_BIOME_RAIN_MAP.clear();
        BiomeClimateManager.CUSTOM_SNOW_TERM_MAP.clear();
        BiomeClimateManager.CLIENT_CUSTOM_SNOW_TERM_MAP.clear();
        BiomeClimateManager.BIOME_COLOR_TAG_KEY_MAP.clear();
        BiomeClimateManager.CLIENT_BIOME_COLOR_TAG_KEY_MAP.clear();
    }

    public static boolean isServerInstance(Biome value) {
        return BIOME_CLIMATE_MAP.containsKey(value);
    }
}

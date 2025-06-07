package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeClimateSettings;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class BiomeClimateManager {
    public final static Map<Biome, BiomeClimateSettings> BIOME_CLIMATE_MAP = new IdentityHashMap<>();
    public final static Map<Biome, BiomeClimateSettings> CLIENT_CLIMATE_MAP = new IdentityHashMap<>();
    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, TagKey<Biome>> CLIENT_BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);
    public static final Map<Biome, Boolean> SMALL_BIOME_MAP = new IdentityHashMap<>(16);

    public static void resetBiomeTemps(RegistryAccess registryAccess, boolean isServer) {
        resetBiomeClimateMap(registryAccess, isServer ? BIOME_CLIMATE_MAP : CLIENT_CLIMATE_MAP);
        putTag(registryAccess, isServer);
    }

    public static void resetBiomeClimateMap(RegistryAccess registryAccess, Map<Biome, BiomeClimateSettings> useMap) {
        useMap.clear();
        Registry<BiomesClimateSettings> biomesClimateSettings = registryAccess.registryOrThrow(ESRegistries.BIOME_CLIMATE_SETTING);
        Map<Biome, List<BiomesClimateSettings>> biomeListMap = new IdentityHashMap<>();
        for (Map.Entry<ResourceKey<BiomesClimateSettings>, BiomesClimateSettings> entry : biomesClimateSettings.entrySet()) {
            BiomesClimateSettings value = entry.getValue();
            for (Holder<Biome> next : value.biomes()) {
                List<BiomesClimateSettings> biomesClimateSettingsList =
                        biomeListMap.computeIfAbsent(next.value(), k -> new ArrayList<>());
                biomesClimateSettingsList.add(value);
            }
        }
        Optional<Registry<Biome>> biomes = registryAccess.registry(Registries.BIOME);
        List<BiomesClimateSettings> objects = List.of();
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
                useMap.put(biome, new BiomeClimateSettings(biome, biomeListMap.getOrDefault(biome, objects))))
        );
    }

    private static final BiomeClimateSettings EMPTY = new BiomeClimateSettings();

    public static BiomeClimateSettings getBiomeClimateSettings(Biome biome, boolean isServer) {
        return isServer ?
                BIOME_CLIMATE_MAP.getOrDefault(biome, EMPTY) :
                CLIENT_CLIMATE_MAP.getOrDefault(biome, EMPTY);
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    @Deprecated(forRemoval = true)
    public static void updateTemperature(Level level, SolarTerm solarTermIndex) {
        // boolean isServer = !level.isClientSide();
        // level.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        // {
        //     float temperature = biome.getModifiedClimateSettings().temperature() > SNOW_LEVEL ?
        //             Math.max(SNOW_LEVEL + 0.001F, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange()) :
        //             Math.min(SNOW_LEVEL, biome.getModifiedClimateSettings().temperature() + solarTermIndex.getTemperatureChange());
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
        //         Math.max(SNOW_LEVEL + 0.001F, temperatureBiome + solarTermIndex.getTemperatureChange()) :
        //         Math.min(SNOW_LEVEL, temperatureBiome + solarTermIndex.getTemperatureChange());
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
    }

    public static boolean isServerInstance(Biome value) {
        return BIOME_CLIMATE_MAP.containsKey(value);
    }
}

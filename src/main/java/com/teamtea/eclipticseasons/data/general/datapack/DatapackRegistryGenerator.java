package com.teamtea.eclipticseasons.data.general.datapack;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.DataPackRegistriesHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryGenerator extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.WETTER, WetterStructureRegistry::bootstrap)
            .add(ESRegistries.CROP, CropRegistry::bootstrap)
            .add(ESRegistries.AGRO_CLIMATE, AgroClimateRegistry::bootstrap)
            .add(ESRegistries.BIOME_CLIMATE_SETTING, BiomeClimateSettingsRegistry::bootstrap)
            .add(ESRegistries.SEASON_QUEST, SeasonQuestRegistry::bootstrap)
            .add(ESRegistries.HUMIDITY_CONTROL, HumidityControlRegistry::bootstrap)
            .add(ESRegistries.SNOW_DEFINITIONS, SnowDefinitionsRegistry::bootstrap)
            .add(ESRegistries.SEASON_PHASE, SeasonPhaseRegistry::bootstrap)
            .add(ESRegistries.SEASON_CYCLE, SeasonCycleRegistry::bootstrap)
            .add(ESRegistries.EXTRA_INFO, ESSortInfoRegistry::bootstrap)
            .add(ESRegistries.WEATHER_EFFECT, WeatherEffectRegistry::bootstrap)
            .add(ESRegistries.BIOME_RAIN, BiomeRainRegistry::bootstrap)
            .add(ESRegistries.SPECIAL_DAYS, SpecialDaysRegistry::bootstrap)
            ;

    public DatapackRegistryGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

    public static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, RegistrySetBuilder datapackEntriesBuilder) {
        var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
        }));
        return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }
}
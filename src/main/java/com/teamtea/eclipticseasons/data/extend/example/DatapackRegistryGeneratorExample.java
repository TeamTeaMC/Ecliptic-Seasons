package com.teamtea.eclipticseasons.data.extend.example;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryGeneratorExample extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.SNOW_TERM, SnowTermRegistry::bootstrap2)
            .add(ESRegistries.WEATHER_REGION, WeatherRegionRegistry::bootstrap2)
            .add(ESRegistries.SEASON_DEFINITION, SeasonDefinitionRegistry::bootstrap2)
            .add(ESRegistries.SNOW_DEFINITIONS, SnowDefinitionsRegistry::bootstrap2)
            .add(ESRegistries.WEATHER_EFFECT, WeatherEffectRegistry::bootstrap2)
            .add(ESRegistries.BIOME_RAIN, BiomeRainRegistry::bootstrap2)
            ;

    public DatapackRegistryGeneratorExample(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

    @Override
    public String getName() {
        return super.getName()+" Example";
    }
}
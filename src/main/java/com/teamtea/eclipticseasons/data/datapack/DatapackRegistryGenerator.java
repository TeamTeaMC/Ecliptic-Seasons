package com.teamtea.eclipticseasons.data.datapack;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryGenerator extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.CROP, CropRegistry::bootstrap)
            .add(ESRegistries.AGRO_CLIMATE, AgroClimateRegistry::bootstrap)
            .add(ESRegistries.BIOME_CLIMATE_SETTING, BiomeClimateSettingsRegistry::bootstrap)
            .add(ESRegistries.SEASON_QUEST, SeasonQuestRegistry::bootstrap)
            .add(ESRegistries.HUMIDITY_CONTROL, HumidityControlRegistry::bootstrap)
            ;

    public DatapackRegistryGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }


}
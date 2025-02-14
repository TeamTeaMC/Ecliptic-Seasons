package com.teamtea.eclipticseasons.data.datapack;



import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.BiomeClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.CropRegistry;
import com.teamtea.eclipticseasons.common.registry.WetterStructureRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryGenerator extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.WETTER, WetterStructureRegistry::bootstrap)
            .add(ESRegistries.BIOME_CLIMATE, BiomeClimateRegistry::bootstrap)
            .add(ESRegistries.CROP, CropRegistry::bootstrap)

            ;

    public DatapackRegistryGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

}
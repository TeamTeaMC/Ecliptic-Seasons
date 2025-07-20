package com.teamtea.eclipticseasons.data.extend.example;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryGeneratorExample extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.BIOME_RAIN, BiomeRainRegistry::bootstrap2)
            .add(ESRegistries.SNOW_TERM, SnowTermRegistry::bootstrap2)
            .add(ESRegistries.CROP, CropRegistry::bootstrap2)
            ;

    public DatapackRegistryGeneratorExample(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

    @Override
    public String getName() {
        return super.getName()+" Example";
    }
}
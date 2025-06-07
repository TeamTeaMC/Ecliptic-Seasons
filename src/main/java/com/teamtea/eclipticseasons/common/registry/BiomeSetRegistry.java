package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.BiomeSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class BiomeSetRegistry {

    public static final ResourceKey<BiomeSet> SEASONAL = createKey("seasonal");
    public static final ResourceKey<BiomeSet> HOT_MONSOONAL = createKey("hot/monsoonal");
    public static final ResourceKey<BiomeSet> RAINLESS = createKey("rainless");
    public static final ResourceKey<BiomeSet> ARID = createKey("arid");
    public static final ResourceKey<BiomeSet> DROUGHTY = createKey("droughty");
    public static final ResourceKey<BiomeSet> SOFT = createKey("soft");
    public static final ResourceKey<BiomeSet> RAINY = createKey("rainy");

    private static ResourceKey<BiomeSet> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_SET, EclipticSeasons.rl(name));
    }


    public static void bootstrap(BootstrapContext<BiomeSet> context) {

    }

}

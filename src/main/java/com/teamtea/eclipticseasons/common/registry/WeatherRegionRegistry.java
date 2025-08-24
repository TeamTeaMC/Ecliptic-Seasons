package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;

public class WeatherRegionRegistry {
    public static final ResourceKey<WeatherRegion> PLAINS = createKey("plains");
    public static final ResourceKey<WeatherRegion> FOREST = createKey("forest");

    private static ResourceKey<WeatherRegion> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_REGION, EclipticSeasons.rl(name));
    }


    public static void bootstrap2(BootstapContext<WeatherRegion> context) {
        var getter = context.lookup(Registries.BIOME);
        var BIOME_REGISTRY_LOOKUP = new AgroClimateRegistry.BiomeRegistryLookup(getter);
        context.register(PLAINS, new WeatherRegion(
                getter.getOrThrow(Biomes.PLAINS),
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.SUNFLOWER_PLAINS))
        ));

        context.register(FOREST, new WeatherRegion(
               Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.FOREST),
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.FLOWER_FOREST),
                        Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.DARK_FOREST),
                        Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.BIRCH_FOREST))
        ));
    }
}

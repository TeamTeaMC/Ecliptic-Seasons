package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

public class WeatherRegionRegistry {
    public static final ResourceKey<WeatherRegion> PLAINS = createKey("plains");
    public static final ResourceKey<WeatherRegion> FOREST = createKey("forest");

    private static ResourceKey<WeatherRegion> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_REGION, EclipticSeasons.rl(name));
    }

    @SafeVarargs
    private static <T> HolderSet<T> and(HolderSet<T>... values) {
        return new AndHolderSet<>(values);
    }

    @SafeVarargs
    private static <T> HolderSet<T> or(HolderSet<T>... values) {
        return new OrHolderSet<>(values);
    }

    public static void bootstrap2(BootstrapContext<WeatherRegion> context) {
        var getter = context.lookup(Registries.BIOME);
        var BIOME_REGISTRY_LOOKUP = new AgroClimateRegistry.BiomeRegistryLookup(getter);
        context.register(PLAINS, new WeatherRegion(
                getter.getOrThrow(Biomes.PLAINS),
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.SUNFLOWER_PLAINS))
        ));

        context.register(FOREST, new WeatherRegion(
                getter.getOrThrow(Biomes.FOREST),
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.FLOWER_FOREST),
                        Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.DARK_FOREST),
                        Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.BIRCH_FOREST))
        ));
    }
}

package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.weather.WeatherDimension;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;

public class WeatherDimensionRegistry {
    public static final ResourceKey<WeatherDimension> PLAINS = createKey("plains");

    private static ResourceKey<WeatherDimension> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_DIMENSION, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<WeatherDimension> context) {
        var getter = context.lookup(Registries.BIOME);
        context.register(PLAINS, new WeatherDimension(
                getter.getOrThrow(Biomes.PLAINS),
                Level.OVERWORLD
        ));
    }
}

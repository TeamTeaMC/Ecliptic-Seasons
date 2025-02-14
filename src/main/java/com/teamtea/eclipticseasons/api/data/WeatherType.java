package com.teamtea.eclipticseasons.api.data;


import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public record WeatherType(
        int maxLength,
        Biome.Precipitation precipitation
) {
}

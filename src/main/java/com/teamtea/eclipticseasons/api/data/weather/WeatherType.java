package com.teamtea.eclipticseasons.api.data.weather;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.TestOnly;

public record WeatherType(
        int maxLength,int minLength,
        Biome.Precipitation precipitation,
        WeatherManager.SnowRenderStatus snowRenderStatus
) {
}

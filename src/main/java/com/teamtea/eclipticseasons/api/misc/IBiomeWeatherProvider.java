package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;

public interface IBiomeWeatherProvider {
    ArrayList<WeatherManager.BiomeWeather> es$get();

    void es$set(ArrayList<WeatherManager.BiomeWeather> biomeWeathers);

    @Nullable
    Holder<Biome> es$getCoreBiome();

    void es$setCoreBiome(Holder<Biome> biomeHolder);
}

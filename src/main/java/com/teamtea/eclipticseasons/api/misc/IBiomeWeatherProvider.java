package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;

import java.util.ArrayList;

public interface IBiomeWeatherProvider {
    ArrayList<WeatherManager.BiomeWeather> es$get();

    void es$set(ArrayList<WeatherManager.BiomeWeather> biomeWeathers);

    float es$getAverageRainLevel(float delta);

    void es$setAverageRainLevel(float value);

    void es$setAverageThunderLevel(float value);

    float es$getAverageThunderLevel(float delta);

}

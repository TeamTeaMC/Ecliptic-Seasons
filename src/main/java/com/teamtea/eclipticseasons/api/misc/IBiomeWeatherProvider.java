package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;

import java.util.ArrayList;

public interface IBiomeWeatherProvider {
    ArrayList<WeatherManager.BiomeWeather> es$get();

    void es$set(ArrayList<WeatherManager.BiomeWeather> biomeWeathers);
}

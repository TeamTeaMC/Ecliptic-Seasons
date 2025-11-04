package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.FogEffect;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class WeatherEffectRegistry {
    public static final ResourceKey<WeatherEffect> THIN_FOG = createKey("thin_fog");

    private static ResourceKey<WeatherEffect> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_EFFECT, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstapContext<WeatherEffect> context) {
        context.register(THIN_FOG, FogEffect.builder().density(0.5f).build());

    }
}

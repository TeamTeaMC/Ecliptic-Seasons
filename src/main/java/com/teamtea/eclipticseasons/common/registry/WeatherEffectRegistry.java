package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class WeatherEffectRegistry {
    public static final ResourceKey<WeatherEffect> THIN_FOG = createKey("thin_fog");
    public static final ResourceKey<WeatherEffect> JUST_FOG = createKey("just_fog");

    public static final ResourceKey<WeatherEffect> SNOW = createKey("snow");
    public static final ResourceKey<WeatherEffect> RAIN = createKey("rain");

    public static final ResourceKey<WeatherEffect> MULTI = createKey("multi");

    private static ResourceKey<WeatherEffect> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_EFFECT, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstrapContext<WeatherEffect> context) {
        context.register(THIN_FOG, FogEffect.builder().build());
        context.register(JUST_FOG, FogEffect.builder().density(0.52f).replace(true).build());
        context.register(SNOW, SnowEffect.builder().build());
        context.register(RAIN, RainEffect.builder().build());
        context.register(MULTI, CompositeEffect.builder()
                .content(SnowEffect.builder().build())
                .content(FogEffect.builder().density(0.52f).replace(false).build())
                .build());
    }
}

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


    public static final ResourceKey<WeatherEffect> LIGHT_RAIN_SNOW = createKey("light_rain_snow");
    public static final ResourceKey<WeatherEffect> MODERATE_RAIN_SNOW = createKey("moderate_rain_snow");
    public static final ResourceKey<WeatherEffect> MISTY_LIGHT_RAIN_SNOW = createKey("misty_light_rain_snow");
    public static final ResourceKey<WeatherEffect> MISTY_MODERATE_RAIN_SNOW = createKey("misty_moderate_rain_snow");

    private static ResourceKey<WeatherEffect> createKey(String name) {
        return ResourceKey.create(ESRegistries.WEATHER_EFFECT, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<WeatherEffect> context) {
        context.register(THIN_FOG, FogEffect.builder().build());
        context.register(JUST_FOG, FogEffect.builder().density(0.52f).replace(true).build());
        context.register(SNOW, SnowEffect.builder().build());
        context.register(RAIN, RainEffect.builder().build());
        context.register(MULTI, CompositeEffect.builder()
                .content(SnowEffect.builder().build())
                .content(FogEffect.builder().density(0.52f).replace(false).build())
                .build());


        CompositeEffect.CompositeEffectBuilder light = CompositeEffect.builder()
                .content(AmountEffect.builder().amount(0.35f).build())
                .content(RainTextureEffect.builder().texture(EclipticSeasons.rl("textures/environment/thin_rain.png")).build())
                .content(SnowTextureEffect.builder().texture(EclipticSeasons.rl("textures/environment/thin_snow.png")).build());

        CompositeEffect.CompositeEffectBuilder middle = CompositeEffect.builder()
                .content(AmountEffect.builder().amount(0.45f).build())
                .content(RainTextureEffect.builder().texture(EclipticSeasons.rl("textures/environment/middle_rain.png")).build())
                .content(SnowTextureEffect.builder().texture(EclipticSeasons.rl("textures/environment/middle_snow.png")).build());

        context.register(LIGHT_RAIN_SNOW, light
                .build());

        context.register(MODERATE_RAIN_SNOW, middle
                .build());

        context.register(MISTY_LIGHT_RAIN_SNOW, light
                .content(FogEffect.builder().density(0.35f).replace(false).build())
                .build());

        context.register(MISTY_MODERATE_RAIN_SNOW, middle
                .content(FogEffect.builder().density(0.52f).replace(false).build())
                .build());
    }
}

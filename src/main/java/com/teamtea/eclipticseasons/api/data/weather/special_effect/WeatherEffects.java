package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class WeatherEffects {
    public static final Map<Identifier, MapCodec<? extends WeatherEffect>> EFFECTS = new HashMap<>();

    public static final Identifier NONE = EclipticSeasons.rl("none");
    public static final Identifier FOG = EclipticSeasons.rl("fog");
    public static final Identifier SNOW = EclipticSeasons.rl("snow");
    public static final Identifier RAIN = EclipticSeasons.rl("rain");
    public static final Identifier COMPOSITE = EclipticSeasons.rl("composite");
    public static final Identifier RAIN_TEXTURE = EclipticSeasons.rl("rain_texture");
    public static final Identifier SNOW_TEXTURE = EclipticSeasons.rl("snow_texture");
    public static final Identifier AMOUNT = EclipticSeasons.rl("amount");

    public static void register(Identifier id, MapCodec<? extends WeatherEffect> codec) {
        EFFECTS.put(id, codec);
    }

    static {
        register(NONE, NoneEffect.CODEC);
        register(FOG, FogEffect.CODEC);
        register(SNOW, SnowEffect.CODEC);
        register(RAIN, RainEffect.CODEC);
        register(COMPOSITE, CompositeEffect.CODEC);
        register(RAIN_TEXTURE, RainTextureEffect.CODEC);
        register(SNOW_TEXTURE, SnowTextureEffect.CODEC);
        register(AMOUNT, AmountEffect.CODEC);
    }
}

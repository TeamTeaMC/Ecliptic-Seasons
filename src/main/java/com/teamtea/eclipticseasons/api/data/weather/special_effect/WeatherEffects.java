package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class WeatherEffects {
    public static final Map<ResourceLocation, MapCodec<? extends WeatherEffect>> EFFECTS = new HashMap<>();

    public static final ResourceLocation NONE = EclipticSeasons.rl("none");
    public static final ResourceLocation FOG = EclipticSeasons.rl("fog");


    public static void register(ResourceLocation id, MapCodec<? extends WeatherEffect> codec) {
        EFFECTS.put(id, codec);
    }

    static {
        register(NONE, NoneEffect.CODEC);
        register(FOG, FogEffect.CODEC);
    }
}

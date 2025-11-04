package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.resources.ResourceLocation;

public interface WeatherEffect {
    Codec<WeatherEffect> CODEC = Codec.STRING
            .xmap(s -> s.contains(":") ? ResourceLocation.tryParse(s) : EclipticSeasons.rl(s),
                    r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
            .dispatch("type", WeatherEffect::getType, c-> WeatherEffects.EFFECTS.get(c).codec());

    ResourceLocation getType();


    MapCodec<? extends WeatherEffect> codec();


}

package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public interface WeatherEffect {
    Codec<WeatherEffect> CODEC = Codec.lazyInitialized(() ->
            Codec.STRING
                    .xmap(s -> s.contains(":") ? Identifier.parse(s) : EclipticSeasons.rl(s),
                            r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
                    .dispatch("type", WeatherEffect::getType, WeatherEffects.EFFECTS::get));

    Identifier getType();


    MapCodec<? extends WeatherEffect> codec();


    default boolean shouldChangePrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return false;
    }

    default Biome.Precipitation getModifiedPrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return original;
    }

    default boolean withFog() {
        return false;
    }

    default float getFogDensity(Level level, BlockPos pos) {
        return 0f;
    }
}

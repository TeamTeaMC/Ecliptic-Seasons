package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@Builder
@Data
public class RainEffect implements WeatherEffect {

    public static final MapCodec<RainEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.stable(new RainEffect()));


    @Override
    public boolean shouldChangePrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return true;
    }

    @Override
    public Biome.Precipitation getModifiedPrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return Biome.Precipitation.RAIN;
    }

    @Override
    public Identifier getType() {
        return WeatherEffects.RAIN;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }


}

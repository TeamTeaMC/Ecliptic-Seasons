package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;


public class NoneEffect implements WeatherEffect {
    public static final NoneEffect INSTANCE = new NoneEffect();

    public static final MapCodec<NoneEffect> CODEC = RecordCodecBuilder
            .mapCodec(ins -> ins.stable(INSTANCE));


    @Override
    public Identifier getType() {
        return WeatherEffects.NONE;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean shouldChangePrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return true;
    }

    @Override
    public Biome.Precipitation getModifiedPrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return Biome.Precipitation.NONE;
    }
}

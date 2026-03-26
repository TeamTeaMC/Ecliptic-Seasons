package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.Codec;
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
public class FogEffect implements WeatherEffect {

    public static final MapCodec<FogEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.FLOAT.optionalFieldOf("density", 0.5f).forGetter(o -> o.density),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(o -> o.replace)
    ).apply(ins, FogEffect::new));

    private final float density;
    private final boolean replace;

    @Override
    public boolean shouldChangePrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return !isPrecipitation && replace;
    }

    @Override
    public Biome.Precipitation getModifiedPrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        return isPrecipitation ? original : Biome.Precipitation.NONE;
    }

    @Override
    public Identifier getType() {
        return WeatherEffects.FOG;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean withFog() {
        return true;
    }

    @Override
    public float getFogDensity(Level level, BlockPos pos) {
        return density;
    }
}

package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Builder
@Data
public class CompositeEffect implements WeatherEffect {

    public static final MapCodec<CompositeEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            WeatherEffect.CODEC.listOf().fieldOf("contents").forGetter(o -> o.contents)
    ).apply(ins, CompositeEffect::new));

    @Singular
    private List<WeatherEffect> contents;

    @Override
    public Identifier getType() {
        return WeatherEffects.COMPOSITE;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean shouldChangePrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        for (WeatherEffect content : contents) {
            if (content.shouldChangePrecipitation(level, biome, pos, isPrecipitation, original)) return true;
        }
        return false;
    }

    @Override
    public Biome.Precipitation getModifiedPrecipitation(Level level, Biome biome, BlockPos pos, boolean isPrecipitation, Biome.Precipitation original) {
        for (WeatherEffect content : contents) {
            if (content.shouldChangePrecipitation(level, biome, pos, isPrecipitation, original))
                return content.getModifiedPrecipitation(level, biome, pos, isPrecipitation, original);
        }
        return original;
    }

    @Override
    public boolean withFog() {
        for (WeatherEffect content : contents) {
            if (content.withFog()) return true;
        }
        return false;
    }

    @Override
    public float getFogDensity(Level level, BlockPos pos) {
        float result = 0f;
        int count = 0;
        for (WeatherEffect content : contents) {
            if (content.withFog()) {
                result += content.getFogDensity(level, pos);
                count++;
            }
        }
        return count > 0 ? result / count : result;
    }

    @Override
    public boolean shouldChangeTexture(boolean rain) {
        return anyApply(WeatherEffect::shouldChangeTexture, rain);
    }

    @Override
    public Identifier onTextureBinding(Identifier original, boolean rain) {
        return apply(original, WeatherEffect::shouldChangeTexture, WeatherEffect::onTextureBinding, rain);
    }

    @Override
    public boolean shouldChangeAmount(boolean rain) {
        return anyApply(WeatherEffect::shouldChangeAmount, rain);
    }

    @Override
    public float getModifiedAmount(float amount, boolean rain) {
        return apply(amount, WeatherEffect::shouldChangeAmount, WeatherEffect::getModifiedAmount, rain);
    }

    public boolean anyApply(BiFunction<WeatherEffect, Boolean, Boolean> apply, boolean rain) {
        for (WeatherEffect content : contents) {
            if (apply.apply(content, rain))
                return true;
        }
        return false;
    }

    public <T> T apply(T original, BiFunction<WeatherEffect, Boolean, Boolean> should, BBiFunction<WeatherEffect, T, Boolean, T> apply, boolean rain) {
        for (WeatherEffect content : contents) {
            if (should.apply(content, rain))
                original = apply.apply(content, original, rain);
        }
        return original;
    }

    @FunctionalInterface
    public interface BBiFunction<T, U, E, R> {
        R apply(T t, U u, E e);
    }
}

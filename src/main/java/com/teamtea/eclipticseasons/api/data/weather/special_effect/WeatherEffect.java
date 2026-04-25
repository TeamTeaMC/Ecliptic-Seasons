package com.teamtea.eclipticseasons.api.data.weather.special_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

public interface WeatherEffect {
    Codec<WeatherEffect> CODEC = CodecUtil.lazyInitialized(() -> Codec.STRING
            .xmap(s -> s.contains(":") ? ResourceLocation.tryParse(s) : EclipticSeasons.rl(s),
                    r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
            .dispatch("type", WeatherEffect::getType, c -> WeatherEffects.EFFECTS.get(c).codec()));

    ResourceLocation getType();


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

    @ApiStatus.Experimental
    default boolean shouldChangeTexture(boolean rain) {
        return false;
    }

    @ApiStatus.Experimental
    default ResourceLocation onTextureBinding(ResourceLocation original, boolean rain) {
        return original;
    }

    @ApiStatus.Experimental
    default boolean shouldChangeAmount(boolean rain) {
        return false;
    }

    @ApiStatus.Experimental
    default float getModifiedAmount(float amount, boolean rain) {
        return amount;
    }
}

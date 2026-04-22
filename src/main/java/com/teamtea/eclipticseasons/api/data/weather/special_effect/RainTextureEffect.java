package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;

@Builder
@Data
public class RainTextureEffect implements WeatherEffect {

    public static final MapCodec<RainTextureEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(o -> o.texture)
    ).apply(ins, RainTextureEffect::new));

    private final ResourceLocation texture;

    @Override
    public ResourceLocation getType() {
        return WeatherEffects.RAIN_TEXTURE;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean shouldChangeTexture(boolean rain) {
        return rain;
    }

    @Override
    public ResourceLocation onTextureBinding(ResourceLocation original, boolean rain) {
        return texture;
    }
}

package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.Identifier;

@Builder
@Data
public class SnowTextureEffect implements WeatherEffect {

    public static final MapCodec<SnowTextureEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Identifier.CODEC.fieldOf("texture").forGetter(o -> o.texture)
    ).apply(ins, SnowTextureEffect::new));

    private final Identifier texture;

    @Override
    public Identifier getType() {
        return WeatherEffects.SNOW_TEXTURE;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }


    @Override
    public boolean shouldChangeTexture(boolean rain) {
        return !rain;
    }

    @Override
    public Identifier onTextureBinding(Identifier original, boolean rain) {
        return texture;
    }
}

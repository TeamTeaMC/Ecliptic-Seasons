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
public class RainTextureEffect implements WeatherEffect {

    public static final MapCodec<RainTextureEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Identifier.CODEC.fieldOf("texture").forGetter(o -> o.texture)
    ).apply(ins, RainTextureEffect::new));

    private final Identifier texture;

    @Override
    public Identifier getType() {
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
    public Identifier onTextureBinding(Identifier original, boolean rain) {
        return texture;
    }
}

package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;

@Builder
@Data
public class FogEffect implements WeatherEffect {
    public static final MapCodec<FogEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.FLOAT.fieldOf("density").forGetter(o -> o.density)
    ).apply(ins, FogEffect::new));



    private final float density;

    @Override
    public ResourceLocation getType() {
        return WeatherEffects.FOG;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }


}

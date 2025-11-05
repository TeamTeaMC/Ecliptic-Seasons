package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;


public class NoneEffect implements WeatherEffect {
    public static final NoneEffect INSTANCE = new NoneEffect();

    public static final MapCodec<NoneEffect> CODEC = RecordCodecBuilder
            .mapCodec(ins -> ins.stable(INSTANCE));


    @Override
    public ResourceLocation getType() {
        return WeatherEffects.NONE;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }


}

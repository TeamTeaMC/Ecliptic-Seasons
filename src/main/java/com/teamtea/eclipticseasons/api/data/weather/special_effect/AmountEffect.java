package com.teamtea.eclipticseasons.api.data.weather.special_effect;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.Identifier;

import java.util.Optional;

@Builder
@Data
public class AmountEffect implements WeatherEffect {

    public static final MapCodec<AmountEffect> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.floatRange(0, 1).fieldOf("amount").forGetter(o -> o.amount),
            Codec.BOOL.optionalFieldOf("rain").forGetter(o -> o.rain)
    ).apply(ins, AmountEffect::new));

    private final float amount;
    @Builder.Default
    private final Optional<Boolean> rain = Optional.empty();

    @Override
    public Identifier getType() {
        return WeatherEffects.AMOUNT;
    }

    @Override
    public MapCodec<? extends WeatherEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean shouldChangeAmount(boolean rain) {
        return this.rain.isEmpty() || this.rain.get() == rain;
    }

    @Override
    public float getModifiedAmount(float amount, boolean rain) {
        return this.amount;
    }
}

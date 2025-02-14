package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;

@TestOnly
public record CropHumidControl(
        Humidity humidity,
        float grow_chance,
        float death_chance,
        float fertile_chance,
        Optional<BlockState> deadState
) {

    public static final Codec<CropHumidControl> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("index").forGetter(c -> c.humidity.ordinal()),
            Codec.FLOAT.fieldOf("grow_chance").orElse(1f).forGetter(CropHumidControl::grow_chance),
            Codec.FLOAT.fieldOf("death_chance").orElse(0f).forGetter(CropHumidControl::death_chance),
            Codec.FLOAT.fieldOf("fertile_chance").orElse(1f).forGetter(CropHumidControl::fertile_chance),
            BlockState.CODEC.optionalFieldOf("deadState").forGetter(CropHumidControl::deadState)
    ).apply(ins, ((i, rate, canSurvive, canFertile, deadState) ->
            new CropHumidControl(Humidity.collectValues()[i], rate, canSurvive, canFertile, deadState)
    )));
}

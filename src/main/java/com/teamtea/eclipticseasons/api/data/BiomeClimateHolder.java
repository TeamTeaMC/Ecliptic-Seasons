package com.teamtea.eclipticseasons.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

@TestOnly
public record BiomeClimateHolder (
        Optional<Float> temperature,
        Optional<Float> rainfall,
        List<BiomeClimate> biomeClimates
) {

    public static final Codec<BiomeClimateHolder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.optionalFieldOf("temperature").forGetter(BiomeClimateHolder::temperature),
            Codec.FLOAT.optionalFieldOf("rainfall").forGetter(BiomeClimateHolder::rainfall),
            BiomeClimate.CODEC.listOf().fieldOf("biome_climates").forGetter(BiomeClimateHolder::biomeClimates)
    ).apply(ins, BiomeClimateHolder::new));
}


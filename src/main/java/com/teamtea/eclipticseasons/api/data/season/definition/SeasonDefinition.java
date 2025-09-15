package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Optional;


public record SeasonDefinition(
        Optional<HolderSet<Biome>> biomes,
        SolarTermValueMap<List<ChangeMode>> changes
) {

    public static final Codec<SeasonDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes").forGetter(SeasonDefinition::biomes),
            SolarTermValueMap.codec(CodecUtil.listFrom(ChangeMode.CODEC)).fieldOf("changes").forGetter(SeasonDefinition::changes)
    ).apply(ins, SeasonDefinition::new));


}

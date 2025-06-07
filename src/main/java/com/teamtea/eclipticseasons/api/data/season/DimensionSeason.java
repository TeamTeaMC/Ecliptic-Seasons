package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;

import java.util.List;

public record DimensionSeason(
        Holder<Level> dimension,
        List<SolarTerm> solarTerms
) {

    public static final Codec<DimensionSeason> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderCodec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimensionSeason::dimension),
            ESExtraCodec.SOLAR_TERM.listOf().fieldOf("solar_terms").forGetter(DimensionSeason::solarTerms)
    ).apply(ins, DimensionSeason::new));
}

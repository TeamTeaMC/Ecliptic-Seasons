package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;


public record CustomSnowTerm(
        HolderSet<Biome> biomes,
        SolarTerm start,
        SolarTerm end
) implements ISnowTerm {

    public static final Codec<CustomSnowTerm> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(CustomSnowTerm::biomes),
            ESExtraCodec.SOLAR_TERM.fieldOf("start").forGetter(CustomSnowTerm::start),
            ESExtraCodec.SOLAR_TERM.fieldOf("end").forGetter(CustomSnowTerm::end)
    ).apply(ins, CustomSnowTerm::new));

    public static final Codec<CustomSnowTerm> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SOLAR_TERM.fieldOf("start").forGetter(CustomSnowTerm::start),
            ESExtraCodec.SOLAR_TERM.fieldOf("end").forGetter(CustomSnowTerm::end)
    ).apply(ins, ((solarTerm, solarTerm2) -> new CustomSnowTerm(HolderSet.direct(), solarTerm, solarTerm2))));

    @Override
    public SolarTerm getStart() {
        return start;
    }

    @Override
    public SolarTerm getEnd() {
        return end;
    }
}

package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

public record SeasonCycle(
        HolderSet<Biome> biomes,
        SolarTermValueMap<Holder<SeasonPhase>> localMapping
) {

    public static final Codec<SeasonCycle> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(SeasonCycle::biomes),
            SolarTermValueMap.codec(CodecUtil.holderCodec(ESRegistries.SEASON_PHASE)).fieldOf("phases").forGetter(SeasonCycle::localMapping)
    ).apply(ins, SeasonCycle::new));

    public boolean matches(Holder<Biome> biomeHolder) {
        return biomes.contains(biomeHolder);
    }
}

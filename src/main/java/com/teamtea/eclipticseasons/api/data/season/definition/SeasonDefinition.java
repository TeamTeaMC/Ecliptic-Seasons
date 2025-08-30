package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record SeasonDefinition(
        Optional<HolderSet<Biome>> biomes,
        SolarTermValueMap<List<ChangeMode>> changes
) {

    public static final Codec<SeasonDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes").forGetter(SeasonDefinition::biomes),
            SolarTermValueMap.codec(CodecUtil.listFrom(ChangeMode.CODEC)).fieldOf("changes").forGetter(SeasonDefinition::changes)
    ).apply(ins, SeasonDefinition::new));


}

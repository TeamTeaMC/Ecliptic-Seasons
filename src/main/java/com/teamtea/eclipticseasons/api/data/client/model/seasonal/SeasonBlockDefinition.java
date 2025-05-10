package com.teamtea.eclipticseasons.api.data.client.model.seasonal;


import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.client.model.LocalSeasonStatusModel;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.BiPredicate;

@Data
public class SeasonBlockDefinition implements HolderMappable<HolderSet<Block>, SeasonBlockDefinition> {

    public static final Codec<SeasonBlockDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BLOCK).fieldOf("blocks").forGetter(o -> o.blocks),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes", HolderSet.direct()).forGetter(o -> o.biomes),
            Slice.CODEC.listOf().fieldOf("slices").forGetter(o -> o.slices)
    ).apply(ins, SeasonBlockDefinition::new));

    private final HolderSet<Block> blocks;
    private final HolderSet<Biome> biomes;
    private final List<Slice> slices;

    private Enum2ObjectMap<SolarTerm, FlatSlice> flatSliceEnumMap = null;

    public boolean hasBuild() {
        return flatSliceEnumMap != null;
    }

    public SeasonBlockDefinition build() {
        flatSliceEnumMap = new Enum2ObjectMap<>(SolarTerm.class);
        for (Slice slice : slices) {
            BiPredicate<SolarTerm, Slice> biPredicate =
                    slice.season != Season.NONE ? ((SolarTerm s, Slice var) -> s.getSeason() == var.season) :
                            slice.start != SolarTerm.NONE && slice.end != SolarTerm.NONE ? ((SolarTerm s, Slice var) -> s.isInTerms(var.start, var.end)) :
                                    (s, v) -> false;
            FlatSlice flatSlice = new FlatSlice(slice.mid,slice.emptyAbove);
            for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                if (biPredicate.test(solarTerm, slice))
                    flatSliceEnumMap.put(solarTerm, flatSlice);
            }
        }
        return this;
    }

    @Override
    public Pair<HolderSet<Block>, SeasonBlockDefinition> asHolderMapping() {
        return Pair.of(blocks,build());
    }


    @Builder
    @Data
    public static class Slice {
        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
                ResourceLocation.CODEC.fieldOf("mid").forGetter(o -> o.mid),
                Codec.BOOL.optionalFieldOf("empty_above",true).forGetter(o -> o.emptyAbove)
        ).apply(ins, Slice::new));

        @Builder.Default
        private final SolarTerm start = SolarTerm.NONE;
        @Builder.Default
        private final SolarTerm end = SolarTerm.NONE;
        @Builder.Default
        private final Season season = Season.NONE;
        @Builder.Default
        private final ResourceLocation mid = LocalSeasonStatusModel.EMPTY;
        @Builder.Default
        private final boolean emptyAbove = true;
    }


    public record FlatSlice(ResourceLocation mid,boolean emptyAbove) {
    }


}

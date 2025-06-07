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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO  support dh changes
@Data
public class SeasonBlockDefinition implements HolderMappable<HolderSet<Block>, SeasonBlockDefinition> {

    public static final Codec<SeasonBlockDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BLOCK).fieldOf("blocks").forGetter(o -> o.blocks),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes", HolderSet.empty()).forGetter(o -> o.biomes),
            Slice.CODEC.listOf().fieldOf("slices").forGetter(o -> o.slices)
    ).apply(ins, SeasonBlockDefinition::new));

    private final HolderSet<Block> blocks;
    private final HolderSet<Biome> biomes;
    private final List<Slice> slices;

    private Enum2ObjectMap<SolarTerm, List<FlatSliceHolder>> flatSliceEnumMap = null;

    public boolean hasBuild() {
        return flatSliceEnumMap != null;
    }

    public SeasonBlockDefinition build() {
        flatSliceEnumMap = new Enum2ObjectMap<>(SolarTerm.class);
        for (Slice slice : slices) {
            // BiPredicate<SolarTerm, Slice> biPredicate =
            //         slice.season != Season.NONE ? ((SolarTerm s, Slice var) -> s.getSeason() == var.season) :
            //                 slice.start != SolarTerm.NONE && slice.end != SolarTerm.NONE ? ((SolarTerm s, Slice var) -> s.isInTerms(var.start, var.end)) :
            //                         (s, v) -> false;
            FlatSlice flatSlice = new FlatSlice(
                    slice.mid.equals(LocalSeasonStatusModel.EMPTY) ? null : slice.mid
                    , slice.emptyAbove, slice.transitionModels.equals(Slice.EMPTY_PAIR) ? null : slice.transitionModels);

            SolarTerm start = slice.start.isValid() ? slice.start :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getFirstSolarTerm() :
                                    slice.startSeason.isValid() ? slice.endSeason.getFirstSolarTerm() : SolarTerm.NONE;

            SolarTerm end = slice.end.isValid() ? slice.end :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getEndSolarTerm() :
                                    slice.endSeason.isValid() ? slice.endSeason.getEndSolarTerm() : SolarTerm.NONE;

            if (start.isValid() && end.isValid()) {
                FlatSliceHolder flatSliceHolder = new FlatSliceHolder(start, end, flatSlice);
                for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                    if (solarTerm.isInTerms(start, end))
                        flatSliceEnumMap.compute(solarTerm, (solarTerm1, flatSliceHolders) -> {
                            if (flatSliceHolders == null) flatSliceHolders = new ArrayList<>();
                            flatSliceHolders.add(flatSliceHolder);
                            return flatSliceHolders;
                        });
                }
            }
        }
        return this;
    }

    @Override
    public Pair<HolderSet<Block>, SeasonBlockDefinition> asHolderMapping() {
        return Pair.of(blocks, build());
    }


    @Builder
    @Data
    public static class Slice {
        public static final Pair<ResourceLocation, ResourceLocation> EMPTY_PAIR = Pair.of(LocalSeasonStatusModel.EMPTY, LocalSeasonStatusModel.EMPTY);

        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
                ResourceLocation.CODEC.optionalFieldOf("mid", LocalSeasonStatusModel.EMPTY).forGetter(o -> o.mid),
                CodecUtil.pairCodec(ResourceLocation::parse, ResourceLocation::parse).optionalFieldOf("transition_models", EMPTY_PAIR).forGetter(o -> o.transitionModels),
                Codec.BOOL.optionalFieldOf("empty_above", true).forGetter(o -> o.emptyAbove)
        ).apply(ins, Slice::new));

        @Builder.Default
        private final SolarTerm start = SolarTerm.NONE;
        @Builder.Default
        private final SolarTerm end = SolarTerm.NONE;
        @Builder.Default
        private final SolarTerm solarTerm = SolarTerm.NONE;
        @Builder.Default
        private final Season startSeason = Season.NONE;
        @Builder.Default
        private final Season endSeason = Season.NONE;
        @Builder.Default
        private final Season season = Season.NONE;
        @Builder.Default
        private final ResourceLocation mid = LocalSeasonStatusModel.EMPTY;
        @Builder.Default
        private final Pair<ResourceLocation, ResourceLocation> transitionModels = EMPTY_PAIR;
        @Builder.Default
        private final boolean emptyAbove = true;
    }

    public record FlatSliceHolder(
            SolarTerm start, SolarTerm end,
            FlatSlice flatSlice) {
    }


    public record FlatSlice(@Nullable ResourceLocation mid, boolean emptyAbove,
                            @Nullable Pair<ResourceLocation, ResourceLocation> transitionModels) {
    }


}

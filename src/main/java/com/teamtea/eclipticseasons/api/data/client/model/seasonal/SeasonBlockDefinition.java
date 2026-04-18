package com.teamtea.eclipticseasons.api.data.client.model.seasonal;


import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.client.model.block.LocalSeasonStatusModel;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO  support dh changes
@Data
public class SeasonBlockDefinition implements HolderMappable<HolderSet<Block>, SeasonBlockDefinition> {

    public static final Identifier GRASS_BLOCK = EclipticSeasons.rl("grass_block");

    public static final Codec<SeasonBlockDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BLOCK).fieldOf("blocks").forGetter(o -> o.blocks),
            CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(o -> o.climate),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes", HolderSet.empty()).forGetter(o -> o.biomes),
            Slice.CODEC.listOf().fieldOf("slices").forGetter(o -> o.slices)
    ).apply(ins, SeasonBlockDefinition::new));

    private final HolderSet<Block> blocks;
    private final Optional<Holder<AgroClimaticZone>> climate;
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

            AgroClimaticZone climate = getClimate().map(Holder::value).orElse(null);

            SolarTerm start = slice.start.isValid() ? slice.start :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getFirstSolarTerm(climate) :
                                    slice.startSeason.isValid() ? slice.endSeason.getFirstSolarTerm(climate) : SolarTerm.NONE;

            SolarTerm end = slice.end.isValid() ? slice.end :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getEndSolarTerm(climate) :
                                    slice.endSeason.isValid() ? slice.endSeason.getEndSolarTerm(climate) : SolarTerm.NONE;

            if (start.isValid() && end.isValid()) {
                FlatSliceHolder firstSliceHolder;
                FlatSliceHolder otherHolder;
                if (start != end && (flatSlice.transitionModels != null)) {
                    firstSliceHolder = new FlatSliceHolder(start, start, flatSlice);
                    otherHolder = new FlatSliceHolder(start.getNextSolarTerm(), end,
                            new FlatSlice(flatSlice.transitionModels.getSecond(), flatSlice.emptyAbove, null));
                } else {
                    firstSliceHolder = new FlatSliceHolder(start, end, flatSlice);
                    otherHolder = firstSliceHolder;
                }

                for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                    if (start == solarTerm) {
                        flatSliceEnumMap.compute(solarTerm, (solarTerm1, flatSliceHolders) -> {
                            if (flatSliceHolders == null) flatSliceHolders = new ArrayList<>();
                            flatSliceHolders.add(firstSliceHolder);
                            return flatSliceHolders;
                        });
                    } else if (solarTerm.isInTerms(start, end)) {
                        flatSliceEnumMap.compute(solarTerm, (solarTerm1, flatSliceHolders) -> {
                            if (flatSliceHolders == null) flatSliceHolders = new ArrayList<>();
                            flatSliceHolders.add(otherHolder);
                            return flatSliceHolders;
                        });
                    }
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
        public static final Pair<Identifier, Identifier> EMPTY_PAIR = Pair.of(LocalSeasonStatusModel.EMPTY, LocalSeasonStatusModel.EMPTY);

        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
                Identifier.CODEC.optionalFieldOf("mid", LocalSeasonStatusModel.EMPTY).forGetter(o -> o.mid),
                CodecUtil.pairCodec(Identifier::parse, Identifier::parse).optionalFieldOf("transition_models", EMPTY_PAIR).forGetter(o -> o.transitionModels),
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
        private final Identifier mid = LocalSeasonStatusModel.EMPTY;
        @Builder.Default
        private final Pair<Identifier, Identifier> transitionModels = EMPTY_PAIR;
        @Builder.Default
        private final boolean emptyAbove = true;
    }

    public record FlatSliceHolder(
            SolarTerm start, SolarTerm end,
            FlatSlice flatSlice) {
    }


    public record FlatSlice(@Nullable Identifier mid, boolean emptyAbove,
                            @Nullable Pair<Identifier, Identifier> transitionModels) {
    }


}

package com.teamtea.eclipticseasons.api.data.client.model.seasonal;


import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.client.model.block.LocalSeasonStatusModel;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.jspecify.annotations.Nullable;
import java.util.*;


@Data
public class SeasonalTexture {

    public static final Codec<SeasonalTexture> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.listFrom(Identifier.CODEC).optionalFieldOf("target", List.of()).forGetter(o -> o.parent),
            CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(o -> o.climate),
            Codec.either(CodecUtil.listFrom(Identifier.CODEC), TagKey.hashedCodec(Registries.BIOME)).optionalFieldOf("biomes").forGetter(o -> o.biomes),
            Slice.CODEC.listOf().fieldOf("slices").forGetter(o -> o.slices)
    ).apply(ins, SeasonalTexture::new));

    private final List<Identifier> parent;
    private final Optional<Holder<AgroClimaticZone>> climate;
    private final Optional<Either<List<Identifier>, TagKey<Biome>>> biomes;
    private final List<Slice> slices;

    private Enum2ObjectMap<SolarTerm, FlatSliceHolder> flatSliceEnumMap = null;

    public boolean hasBuild() {
        return flatSliceEnumMap != null;
    }

    public SeasonalTexture build(Identifier Identifier) {
        flatSliceEnumMap = new Enum2ObjectMap<>(SolarTerm.class);
        for (Slice slice : slices) {
            FlatSlice flatSlice = new FlatSlice(
                    slice.textures.isEmpty() ? null : slice.textures
                    , slice.tintMap, slice.transitionMaterials.isEmpty() ? null : slice.transitionMaterials);
            FlatSlice snowFlatSlice = new FlatSlice(
                    slice.snowTextures.isEmpty() ? null : slice.snowTextures
                    , slice.snowTintMap, slice.snowTransitionMaterials.isEmpty() ? null : slice.snowTransitionMaterials);

            AgroClimaticZone climate = getClimate().map(Holder::value).orElse(null);

            SolarTerm start = slice.start.isValid() ? slice.start :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getFirstSolarTerm(climate) :
                                    slice.startSeason.isValid() ? slice.endSeason.getFirstSolarTerm(climate) : null;

            SolarTerm end = slice.end.isValid() ? slice.end :
                    slice.solarTerm.isValid() ? slice.solarTerm :
                            slice.season.isValid() ? slice.season.getEndSolarTerm(climate) :
                                    slice.endSeason.isValid() ? slice.endSeason.getEndSolarTerm(climate) : null;

            if (start == null && end == null) {
                start = SolarTerm.BEGINNING_OF_SPRING;
                end = SolarTerm.GREATER_COLD;
            }

            if (start != null && end != null && start.isValid() && end.isValid()) {
                FlatSliceHolder firstSliceHolder = new FlatSliceHolder(start, end, flatSlice, snowFlatSlice);
                FlatSliceHolder otherHolder;
                if (start != end && (flatSlice.transitionModels != null || snowFlatSlice.transitionModels != null)) {
                    firstSliceHolder = new FlatSliceHolder(start, start, flatSlice, snowFlatSlice);
                    otherHolder = new FlatSliceHolder(start.getNextSolarTerm(), end,
                            new FlatSlice(flatSlice.transitionModels == null ? flatSlice.mid : flatSlice.transitionModels.stream().map(Pair::getSecond).toList(), flatSlice.tintMap, null),
                            new FlatSlice(snowFlatSlice.transitionModels == null ? snowFlatSlice.mid : snowFlatSlice.transitionModels.stream().map(Pair::getSecond).toList(), flatSlice.tintMap, null));
                } else {
                    otherHolder = firstSliceHolder;
                }
                for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                    if (start == solarTerm) flatSliceEnumMap.put(start, firstSliceHolder);
                    else if (solarTerm.isInTerms(start, end)) flatSliceEnumMap.put(solarTerm, otherHolder);
                }
            }
        }
        return this;
    }


    @Builder
    @Data
    public static class Slice {
        public static final Pair<Identifier, Identifier> EMPTY_PAIR = Pair.of(LocalSeasonStatusModel.EMPTY, LocalSeasonStatusModel.EMPTY);

        public static final Codec<Map<String, Identifier>> MATERIALS = CodecUtil.mapCodec(Codec.STRING, Identifier.CODEC);
        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
                CodecUtil.listFrom(MATERIALS).optionalFieldOf("textures", List.of()).forGetter(o -> o.textures),
                CodecUtil.listFrom(MATERIALS.listOf(2, 2).xmap(c -> Pair.of(c.get(0), c.get(1)), p -> List.of(p.getFirst(), p.getSecond())))
                        .optionalFieldOf("transition_textures", List.of()).forGetter(o -> o.transitionMaterials),
                CodecUtil.listFrom(MATERIALS).optionalFieldOf("snow_textures", List.of()).forGetter(o -> o.snowTextures),
                CodecUtil.listFrom(MATERIALS.listOf(2, 2).xmap(c -> Pair.of(c.get(0), c.get(1)), p -> List.of(p.getFirst(), p.getSecond())))
                        .optionalFieldOf("snow_transition_textures", List.of()).forGetter(o -> o.snowTransitionMaterials),
                CodecUtil.mapCodec(Codec.STRING, Codec.INT).optionalFieldOf("tint", Map.of()).forGetter(o -> o.tintMap),
                CodecUtil.mapCodec(Codec.STRING, Codec.INT).optionalFieldOf("snow_tint", Map.of()).forGetter(o -> o.snowTintMap)
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
        private final List<Map<String, Identifier>> textures = List.of();
        @Builder.Default
        private final List<Pair<Map<String, Identifier>, Map<String, Identifier>>> transitionMaterials = List.of();
        @Builder.Default
        private final List<Map<String, Identifier>> snowTextures = List.of();
        @Builder.Default
        private final List<Pair<Map<String, Identifier>, Map<String, Identifier>>> snowTransitionMaterials = List.of();
        @Builder.Default
        private final Map<String, Integer> tintMap = Map.of();
        @Builder.Default
        private final Map<String, Integer> snowTintMap = Map.of();
    }

    public record FlatSliceHolder(
            SolarTerm start, SolarTerm end,
            FlatSlice flatSlice,
            FlatSlice snowSlice) {
    }


    public record FlatSlice(@Nullable List<Map<String, Identifier>> mid, Map<String, Integer> tintMap,
                            @Nullable List<Pair<Map<String, Identifier>, Map<String, Identifier>>> transitionModels) {
    }


}

package com.teamtea.eclipticseasons.api.data.client.model.seasonal;


import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.client.model.LocalSeasonStatusModel;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Data
public class SeasonalTexture {

    public static final Codec<SeasonalTexture> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.listFrom(ResourceLocation.CODEC).optionalFieldOf("target", List.of()).forGetter(o -> o.parent),
            Codec.either(CodecUtil.listFrom(ResourceLocation.CODEC), TagKey.hashedCodec(Registries.BIOME)).optionalFieldOf("biomes").forGetter(o -> o.biomes),
            Slice.CODEC.listOf().fieldOf("slices").forGetter(o -> o.slices)
    ).apply(ins, SeasonalTexture::new));

    private final List<ResourceLocation> parent;
    private final Optional<Either<List<ResourceLocation>, TagKey<Biome>>> biomes;
    private final List<Slice> slices;

    private Enum2ObjectMap<SolarTerm, FlatSliceHolder> flatSliceEnumMap = null;

    public boolean hasBuild() {
        return flatSliceEnumMap != null;
    }

    public SeasonalTexture build(ResourceLocation resourceLocation) {
        flatSliceEnumMap = new Enum2ObjectMap<>(SolarTerm.class);
        for (Slice slice : slices) {
            FlatSlice flatSlice = new FlatSlice(
                    slice.textures.isEmpty() ? null : slice.textures
                    , slice.emptyAbove, slice.transitionMaterials.isEmpty() ? null : slice.transitionMaterials);

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
                            // if (flatSliceHolders == null) flatSliceHolders = new ArrayList<>();
                            // flatSliceHolders.add(flatSliceHolder);
                            return flatSliceHolder;
                        });
                }
            }
        }
        return this;
    }


    @Builder
    @Data
    public static class Slice {
        public static final Pair<ResourceLocation, ResourceLocation> EMPTY_PAIR = Pair.of(LocalSeasonStatusModel.EMPTY, LocalSeasonStatusModel.EMPTY);

        public static final Codec<Map<String, ResourceLocation>> MATERIALS = CodecUtil.mapCodec(Codec.STRING, ResourceLocation.CODEC);
        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
                CodecUtil.listFrom(MATERIALS).optionalFieldOf("textures", List.of()).forGetter(o -> o.textures),
                CodecUtil.listFrom(MATERIALS.listOf().flatXmap(
                                c -> {
                                    if (c.size() == 2) return DataResult.success(Pair.of(c.get(0), c.get(1)));
                                    else return DataResult.error(() -> "Unknown Size " + c.size());
                                },
                                p -> DataResult.success(List.of(p.getFirst(), p.getSecond()))))
                        .optionalFieldOf("transition_textures", List.of()).forGetter(o -> o.transitionMaterials),
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
        private final List<Map<String, ResourceLocation>> textures = List.of();
        @Builder.Default
        private final List<Pair<Map<String, ResourceLocation>, Map<String, ResourceLocation>>> transitionMaterials = List.of();
        @Builder.Default
        private final boolean emptyAbove = true;
    }

    public record FlatSliceHolder(
            SolarTerm start, SolarTerm end,
            FlatSlice flatSlice) {
    }


    public record FlatSlice(@Nullable List<Map<String, ResourceLocation>> mid, boolean emptyAbove,
                            @Nullable List<Pair<Map<String, ResourceLocation>, Map<String, ResourceLocation>>> transitionModels) {
    }


}

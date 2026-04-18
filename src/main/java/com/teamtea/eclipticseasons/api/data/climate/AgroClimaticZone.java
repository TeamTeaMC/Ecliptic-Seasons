package com.teamtea.eclipticseasons.api.data.climate;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link AgroClimaticZone} enum is used to define specific climate requirements for crops, distinguishing them from
 * the broader {@link  com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags ClimateTypeBiomeTags}. While {@link  com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags ClimateTypeBiomeTags} categorizes biomes based on general seasonal and weather patterns
 * (e.g., wet/dry seasons, temperature variations), {@link AgroClimaticZone} focuses on the precise conditions needed
 * for crops to thrive, such as grow rate ranges in seasons and humidity levels.
 * <p>
 * This distinction is necessary because crops in the game may require very specific conditions (like high humidity
 * or extreme heat) that do not necessarily correlate with the broader biome classification, ensuring realistic crop growth
 * mechanics.
 * <p>
 * Additionally, {@link AgroClimaticZone} allows creation of maps to automatically populate data for newly added agroclimate types.
 */
public record AgroClimaticZone(HolderSet<Biome> biomes,
                               Optional<GrowParameter> growParameter,
                               Optional<Map<Either<Season, SolarTerm>, Float>> defaultMapping,
                               Optional<Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>>> mapping,
                               List<Pair<Season, Integer>> seasonalSignalDurations) {

    public static final Codec<Either<Season, SolarTerm>> EITHER_CODEC = Codec.either(ESExtraCodec.SEASON, ESExtraCodec.SOLAR_TERM);
    public static final Codec<Map<Either<Season, SolarTerm>, Float>> EITHER_MAP_PAIR_CODEC = CodecUtil.mapCodec(EITHER_CODEC, Codec.FLOAT);
    public static final Codec<Pair<Either<Season, SolarTerm>, Float>> EITHER_PAIR_CODEC = CodecUtil.pairCodec(EITHER_CODEC, Codec.FLOAT);
    public static final Codec<Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>>> EITHER_MAP_CODEC =
            CodecUtil.mapCodec(EITHER_CODEC, EITHER_PAIR_CODEC.listOf());

    public static final Codec<AgroClimaticZone> CODEC =
            RecordCodecBuilder.create(builder -> builder.group(
                    CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(AgroClimaticZone::biomes),
                    GrowParameter.CODEC.optionalFieldOf("global").forGetter(AgroClimaticZone::growParameter),
                    EITHER_MAP_PAIR_CODEC.optionalFieldOf("default_mapping").forGetter(AgroClimaticZone::defaultMapping),
                    EITHER_MAP_CODEC.optionalFieldOf("mappings").forGetter(AgroClimaticZone::mapping),
                    CodecUtil.pairCodec(ESExtraCodec.SEASON, Codec.INT).listOf().optionalFieldOf("seasonal_signal_durations",List.of()).validate(AgroClimaticZone::checkSeasonalSignalDurations
                    ).forGetter(AgroClimaticZone::seasonalSignalDurations)
            ).apply(builder, AgroClimaticZone::new));

    private static DataResult<List<Pair<Season, Integer>>> checkSeasonalSignalDurations(
            List<Pair<Season, Integer>> pairList) {
        boolean result = true;
        int sum = 0;
        for (Pair<Season, Integer> p : pairList) {
            sum += p.getSecond();
            if (p.getSecond() < 0) result = false;
        }
        int count = sum;
        StringBuilder stringBuilder = new StringBuilder();
        if (count == 0 || count == 24) {
        } else {
            result = false;
        }

        return !result
                ? DataResult.error(
                () -> "Term total length is " + count + stringBuilder, List.of()
        )
                : DataResult.success(pairList);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    public GrowParameter buildFromList(CropGrowControl templateGrowth,
                                       List<Pair<Either<Season, SolarTerm>, Float>> mappings) {
        return buildFromList(null, templateGrowth, mappings);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    public GrowParameter getGrowParameterFromMapping(CropGrowControl templateGrowth,
                                                     SolarTerm solarTerm) {
        return getGrowParameterFromMapping(null, templateGrowth, solarTerm);
    }

    public GrowParameter buildFromList(BlockState state,
                                       CropGrowControl templateGrowth,
                                       List<Pair<Either<Season, SolarTerm>, Float>> mappings) {
        GrowParameter growParameterResult = null;
        if (mappings != null
                && templateGrowth != null) {
            float chance = 0;
            boolean any = false;
            for (int m = 0, listSize = mappings.size(); m < listSize; m++) {
                Pair<Either<Season, SolarTerm>, Float> eitherFloatMap = mappings.get(m);
                Either<Season, SolarTerm> key = eitherFloatMap.getFirst();
                if (key.right().isPresent()) {
                    GrowParameter orDefault = templateGrowth.getGrowParameter(key.right().get(), state);
                    if (orDefault != null) {
                        chance += orDefault.grow_chance() * eitherFloatMap.getSecond();
                        any = true;
                    }
                } else if (key.left().isPresent()) {
                    GrowParameter orDefault = templateGrowth.getGrowParameter(key.left().get(), state);
                    if (orDefault != null) {
                        chance += orDefault.grow_chance() * eitherFloatMap.getSecond();
                        any = true;
                    }
                }
            }
            if (any) {
                growParameterResult = GrowParameter.builder().growChance(chance).end();
            }
        }
        return growParameterResult;
    }


    public GrowParameter getGrowParameterFromMapping(BlockState state,
                                                     CropGrowControl templateGrowth,
                                                     SolarTerm solarTerm) {
        GrowParameter growParameterResult = null;
        if (this.mapping().isPresent()) {
            Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> eitherListMap = this.mapping().get();
            List<Pair<Either<Season, SolarTerm>, Float>> list = eitherListMap.getOrDefault(Either.right(solarTerm), null);
            growParameterResult = buildFromList(state, templateGrowth, list);
            if (growParameterResult == null) {
                list = eitherListMap.getOrDefault(Either.left(solarTerm.getSeason()), null);
                growParameterResult = buildFromList(state, templateGrowth, list);
            }
        }

        if (growParameterResult == null
                && this.defaultMapping().isPresent()) {
            ArrayList<Pair<Either<Season, SolarTerm>, Float>> pairs = new ArrayList<>();
            for (Map.Entry<Either<Season, SolarTerm>, Float> s : defaultMapping().get().entrySet()) {
                Pair<Either<Season, SolarTerm>, Float> eitherFloatPair = Pair.of(s.getKey(), s.getValue());
                pairs.add(eitherFloatPair);
            }
            growParameterResult = buildFromList(state, templateGrowth, pairs);
        }

        if (growParameterResult == null && this.growParameter().isPresent()) {
            growParameterResult = this.growParameter().get();
        }
        return growParameterResult;
    }

    public static String getDescriptionId(@NonNull Identifier Identifier) {
        return ESRegistries.createLangKey(ESRegistries.AGRO_CLIMATE, Identifier);
    }


    public static Builder builder(HolderSet<Biome> biomes) {
        return new Builder(biomes);
    }

    public static class Builder {
        private HolderSet<Biome> biomes;
        private GrowParameter growParameter;
        private Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> mapping;
        private Map<Either<Season, SolarTerm>, Float> defaultMapping;
        private List<Pair<Season, Integer>> localSeason = new ArrayList<>();

        private Builder(HolderSet<Biome> biomes) {
            this.biomes = biomes;
        }

        public Builder growParameter(GrowParameter growParameter) {
            this.growParameter = growParameter;
            return this;
        }

        public Builder mapping(Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> mapping) {
            this.mapping = mapping;
            return this;
        }

        public Builder defaultMapping(Pair<Either<Season, SolarTerm>, Float> pair) {
            Map<Either<Season, SolarTerm>, Float> mapping2 = new LinkedHashMap<>(24);
            mapping2.put(pair.getFirst(), pair.getSecond());
            this.defaultMapping = mapping2;
            return this;
        }

        public Builder add(Season season, int length) {
            this.localSeason.add(Pair.of(season, length));
            return this;
        }

        public AgroClimaticZone end() {
            if (biomes == null) {
                throw new IllegalArgumentException("Biomes must not be null");
            }
            return new AgroClimaticZone(biomes, Optional.ofNullable(growParameter), Optional.ofNullable(defaultMapping), Optional.ofNullable(mapping), localSeason);
        }
    }
}

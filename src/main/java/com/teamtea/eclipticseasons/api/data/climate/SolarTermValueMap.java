package com.teamtea.eclipticseasons.api.data.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record SolarTermValueMap<T>(
        Optional<T> defaultValue,
        Optional<EnumMap<SolarTerm, T>> solarTermMap,
        Optional<EnumMap<Season, T>> seasonMap
) {

    public static final Codec<SolarTermValueMap<Float>> FLOAT_CODEC = getSolarTermValueMapCodec(Codec.FLOAT);
    public static final Codec<SolarTermValueMap<Integer>> INT_CODEC = getSolarTermValueMapCodec(Codec.INT);

    private static <B> Codec<SolarTermValueMap<B>> getSolarTermValueMapCodec(Codec<B> codec) {
        return RecordCodecBuilder.create(ins -> ins.group(
                codec.optionalFieldOf("default").forGetter(SolarTermValueMap::defaultValue),
                CodecUtil.enumMapCodec(StringRepresentable.fromEnum(SolarTerm::collectValues), codec, SolarTerm.class)
                        .optionalFieldOf("solar_terms").forGetter(SolarTermValueMap::solarTermMap),
                CodecUtil.enumMapCodec(StringRepresentable.fromEnum(Season::collectValues), codec, Season.class)
                        .optionalFieldOf("seasons").forGetter(SolarTermValueMap::seasonMap)
        ).apply(ins, SolarTermValueMap::new));
    }

    public static final EnumMap<Season, List<SolarTerm>> SEASON_TO_SOLAR_TERMS_MAP = new EnumMap<>(Arrays.stream(SolarTerm.collectValues())
            .collect(Collectors.groupingBy(SolarTerm::getSeason)));

    public EnumMap<SolarTerm, T> combine() {
        EnumMap<SolarTerm, T> map = new EnumMap<>(SolarTerm.class);
        if (solarTermMap().isPresent()) {
            map.putAll(solarTermMap().get());
        }
        if (seasonMap().isPresent()) {
            seasonMap().get().forEach(
                    (season, t) -> {
                        List<SolarTerm> associatedSolarTerms = SEASON_TO_SOLAR_TERMS_MAP.get(season);
                        if (associatedSolarTerms != null) {
                            for (SolarTerm solarTerm : associatedSolarTerms) {
                                map.putIfAbsent(solarTerm, t);
                            }
                        }
                    }
            );
        }
        if (defaultValue().isPresent()) {
            for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                map.putIfAbsent(solarTerm, defaultValue().get());
            }
        }
        return map;
    }


    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private T defaultValue;
        private EnumMap<SolarTerm, T> solarTermMap;
        private EnumMap<Season, T> seasonMap;

        private Builder() {
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> solarTermMap(EnumMap<SolarTerm, T> solarTermMap) {
            this.solarTermMap = solarTermMap;
            return this;
        }

        public Builder<T> seasonMap(EnumMap<Season, T> seasonMap) {
            this.seasonMap = seasonMap;
            return this;
        }

        public Builder<T> putSolarTerm(SolarTerm term, T value) {
            if (this.solarTermMap == null) {
                this.solarTermMap = new EnumMap<>(SolarTerm.class);
            }
            this.solarTermMap.put(term, value);
            return this;
        }

        public Builder<T> putSeason(Season season, T value) {
            if (this.seasonMap == null) {
                this.seasonMap = new EnumMap<>(Season.class);
            }
            this.seasonMap.put(season, value);
            return this;
        }

        public SolarTermValueMap<T> build() {
            return new SolarTermValueMap<>(
                    Optional.ofNullable(defaultValue),
                    Optional.ofNullable(solarTermMap),
                    Optional.ofNullable(seasonMap)
            );
        }
    }
}

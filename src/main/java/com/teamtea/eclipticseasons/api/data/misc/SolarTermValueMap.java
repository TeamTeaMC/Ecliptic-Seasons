package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SolarTermValueMap<T>(
        Optional<T> defaultValue,
        Optional<EnumMap<SolarTerm, T>> solarTermMap,
        Optional<EnumMap<Season, T>> seasonMap
) {

    public static final Codec<SolarTermValueMap<Float>> FLOAT_CODEC = codec(Codec.FLOAT);
    public static final Codec<SolarTermValueMap<Integer>> INT_CODEC = codec(Codec.INT);

    public static <B> Codec<SolarTermValueMap<B>> codec(Codec<B> codec) {
        return RecordCodecBuilder.create(ins -> ins.group(
                codec.optionalFieldOf("default").forGetter(SolarTermValueMap::defaultValue),
                CodecUtil.enumMapCodec(ESExtraCodec.SOLAR_TERM, codec, SolarTerm.class)
                        .optionalFieldOf("solar_terms").forGetter(SolarTermValueMap::solarTermMap),
                CodecUtil.enumMapCodec(ESExtraCodec.SEASON, codec, Season.class)
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

    public static <K extends Enum<K>, T, V> Enum2ObjectMap<K, V> convertToEnum2ObjectMapBase(
            Class<K> keyType, EnumMap<K, T> source, Function<T, V> converter) {
        Enum2ObjectMap<K, V> target = new Enum2ObjectMap<>(keyType, null);
        for (var entry : source.entrySet()) {
            V apply = converter.apply(entry.getValue());
            if (apply != null) {
                target.put(entry.getKey(), apply);
            }
        }
        return target;
    }

    public static <T, V> Enum2ObjectMap<SolarTerm, V> convertToEnum2ObjectMap(
            EnumMap<SolarTerm, T> source, Function<T, V> converter) {
        return convertToEnum2ObjectMapBase(SolarTerm.class, source, converter);
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

        public Optional<SolarTermValueMap<T>> ofBuild() {
            return Optional.of(new SolarTermValueMap<>(
                    Optional.ofNullable(defaultValue),
                    Optional.ofNullable(solarTermMap),
                    Optional.ofNullable(seasonMap)
            ));
        }
    }
}

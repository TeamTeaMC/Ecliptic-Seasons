package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// todo we need a season mapping
public record SolarTermValueMap<T>(
        Optional<T> defaultValue,
        Optional<Enum2ObjectMap<SolarTerm, T>> solarTermMap,
        Optional<Holder<AgroClimaticZone>> climate,
        Optional<Enum2ObjectMap<Season, T>> seasonMap
) {

    public static final Codec<SolarTermValueMap<Float>> FLOAT_CODEC = codec(Codec.FLOAT);
    public static final Codec<SolarTermValueMap<Integer>> INT_CODEC = codec(Codec.INT);

    public static <B> Codec<SolarTermValueMap<B>> codec(Codec<B> codec) {
        return RecordCodecBuilder.create(ins -> ins.group(
                codec.optionalFieldOf("default").forGetter(SolarTermValueMap::defaultValue),
                CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SOLAR_TERM, codec, SolarTerm.class)
                        .optionalFieldOf("solar_terms").forGetter(SolarTermValueMap::solarTermMap),
                CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE)
                        .optionalFieldOf("climate").forGetter(SolarTermValueMap::climate),
                CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SEASON, codec, Season.class)
                        .optionalFieldOf("seasons").forGetter(SolarTermValueMap::seasonMap)
        ).apply(ins, SolarTermValueMap::new));
    }

    public static final Enum2ObjectMap<Season, List<SolarTerm>> SEASON_TO_SOLAR_TERMS_MAP = convertToEnum2ObjectMapBase(
            Season.class, new EnumMap<>(Arrays.stream(SolarTerm.collectValues())
                    .collect(Collectors.groupingBy(SolarTerm::getSeason))), (c)->c
    );

    public Enum2ObjectMap<SolarTerm, T> combine() {
        Enum2ObjectMap<SolarTerm, T> map = new Enum2ObjectMap<>(SolarTerm.class);
        if (solarTermMap().isPresent()) {
            map.putAll(solarTermMap().get());
        }

        if (seasonMap().isPresent()) {
            Enum2ObjectMap<Season, List<SolarTerm>> usemap;
            if (climate.isPresent()) {
                usemap = new Enum2ObjectMap<>(Season.class);
                usemap.put(Season.NONE, List.of(SolarTerm.NONE));
                int i = 0;
                for (Pair<Season, Integer> pair : climate.get().value().seasonalSignalDurations()) {
                    List<SolarTerm> solarTerms = usemap.computeIfAbsent(pair.getFirst(), (c) -> new ArrayList<>());
                    solarTerms.addAll(Arrays.asList(SolarTerm.collectValues())
                            .subList(i, pair.getSecond() + i));
                    i += pair.getSecond();
                }
            } else {
                usemap = SEASON_TO_SOLAR_TERMS_MAP;
            }

            seasonMap().get().forEach(
                    (season, t) -> {
                        List<SolarTerm> associatedSolarTerms = usemap.get(season);
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
        Enum2ObjectMap<K, V> target = new Enum2ObjectMap<>(keyType);
        for (var entry : source.entrySet()) {
            V apply = converter.apply(entry.getValue());
            if (apply != null) {
                target.put(entry.getKey(), apply);
            }
        }
        return target;
    }

    public static <K extends Enum<K>, T, V> Enum2ObjectMap<K, V> convertToEnum2ObjectMap(
            Class<K> keyType, Enum2ObjectMap<K, T> source, Function<T, V> converter) {
        Enum2ObjectMap<K, V> target = new Enum2ObjectMap<>(keyType);
        for (var entry : source.entrySet()) {
            V apply = converter.apply(entry.getValue());
            if (apply != null) {
                target.put(entry.getKey(), apply);
            }
        }
        return target;
    }


    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private T defaultValue;
        private Enum2ObjectMap<SolarTerm, T> solarTermMap;
        private Enum2ObjectMap<Season, T> seasonMap;
        private Holder<AgroClimaticZone> climate;

        private Builder() {
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> solarTermMap(Enum2ObjectMap<SolarTerm, T> solarTermMap) {
            this.solarTermMap = solarTermMap;
            return this;
        }

        public Builder<T> seasonMap(Enum2ObjectMap<Season, T> seasonMap) {
            this.seasonMap = seasonMap;
            return this;
        }

        public Builder<T> putSolarTerm(SolarTerm term, T value) {
            if (this.solarTermMap == null) {
                this.solarTermMap = new Enum2ObjectMap<>(SolarTerm.class);
            }
            this.solarTermMap.put(term, value);
            return this;
        }

        public Builder<T> putSeason(Season season, T value) {
            if (this.seasonMap == null) {
                this.seasonMap = new Enum2ObjectMap<>(Season.class);
            }
            this.seasonMap.put(season, value);
            return this;
        }

        public void climate(Holder<AgroClimaticZone> climate) {
            this.climate = climate;
        }

        public SolarTermValueMap<T> build() {
            return new SolarTermValueMap<>(
                    Optional.ofNullable(defaultValue),
                    Optional.ofNullable(solarTermMap),
                    Optional.ofNullable(climate),
                    Optional.ofNullable(seasonMap)
            );
        }

        public Optional<SolarTermValueMap<T>> ofBuild() {
            return Optional.of(new SolarTermValueMap<>(
                    Optional.ofNullable(defaultValue),
                    Optional.ofNullable(solarTermMap),
                    Optional.ofNullable(climate),
                    Optional.ofNullable(seasonMap)
            ));
        }
    }
}

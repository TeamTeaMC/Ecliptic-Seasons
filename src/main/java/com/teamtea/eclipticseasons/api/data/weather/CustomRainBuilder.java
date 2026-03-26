package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record CustomRainBuilder(
        HolderSet<Biome> biomes,
        SolarTermValueMap<List<Weather>> weathers
) {

    public static final Codec<CustomRainBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(CustomRainBuilder::biomes),
            SolarTermValueMap.codec(CodecUtil.listFrom(Weather.CODEC)).fieldOf("weathers").forGetter(CustomRainBuilder::weathers)
    ).apply(ins, CustomRainBuilder::new));

    public Map<SolarTerm, CustomRain> build() {
        return weathers.combine().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    List<CustomRain.Weather> weatherList = e.getValue().stream().map(w -> CustomRain.Weather.of(e.getKey(), w)).toList();
                                    return new CustomRain(
                                            e.getKey().ordinal(),
                                            weatherList,
                                            weatherList.size() == 1 && weatherList.getFirst().timePeriod().isEmpty() ? Optional.of(weatherList.getFirst()) : Optional.empty(),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getRainChance).average().orElse(0),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getThunderChance).average().orElse(0),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getSnowAccumulationSpeed).average().orElse(1),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getSnowMeltSpeed).average().orElse(1)
                                    );
                                },
                                (a, b) -> b,
                                () -> new Enum2ObjectMap<>(SolarTerm.class)
                        )
                );
    }

    @Builder
    @Data
    public static class Weather {

        public static final Codec<Weather> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                IntProviders.POSITIVE_CODEC.optionalFieldOf("rain").forGetter(Weather::getRain),
                IntProviders.POSITIVE_CODEC.optionalFieldOf("rain_delay").forGetter(Weather::getRainDelay),
                IntProviders.POSITIVE_CODEC.optionalFieldOf("thunder").forGetter(Weather::getThunder),
                IntProviders.POSITIVE_CODEC.optionalFieldOf("thunder_delay").forGetter(Weather::getThunderDelay),
                Codec.FLOAT.fieldOf("rain_chance").forGetter(Weather::getRainChance),
                Codec.FLOAT.optionalFieldOf("thunder_chance", 0f).forGetter(Weather::getThunderChance),
                StringRepresentable.fromEnum(TimePeriod::collectValues).listOf().optionalFieldOf("time_periods", List.of()).forGetter(Weather::getTimePeriod),
                CodecUtil.holderCodec(ESRegistries.WEATHER_EFFECT).optionalFieldOf("special_effect").forGetter(Weather::getSpecialEffect),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("weight", 10).forGetter(Weather::getWeight),
                Codec.floatRange(0, 100).optionalFieldOf("snow_accumulation_speed", 1f).forGetter(Weather::getSnowAccumulationSpeed),
                Codec.floatRange(0, 100).optionalFieldOf("snow_melt_speed", 1f).forGetter(Weather::getSnowMeltSpeed)
        ).apply(ins, Weather::new));

        @Builder.Default
        private final Optional<IntProvider> rain = Optional.empty();
        @Builder.Default
        private final Optional<IntProvider> rainDelay = Optional.empty();
        @Builder.Default
        private final Optional<IntProvider> thunder = Optional.empty();
        @Builder.Default
        private final Optional<IntProvider> thunderDelay = Optional.empty();
        @Builder.Default
        private final float rainChance = 0;
        @Builder.Default
        private final float thunderChance = 0;
        @Builder.Default
        private final List<TimePeriod> timePeriod = List.of();
        @Builder.Default
        private final Optional<Holder<WeatherEffect>> specialEffect = Optional.empty();
        @Builder.Default
        private final int weight = 10;
        @Builder.Default
        private final float snowAccumulationSpeed = 1;
        @Builder.Default
        private final float snowMeltSpeed = 1;

    }


}

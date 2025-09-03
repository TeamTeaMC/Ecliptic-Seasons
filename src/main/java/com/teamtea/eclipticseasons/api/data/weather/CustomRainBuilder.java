package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.biome.Biome;

import java.util.EnumMap;
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
            SolarTermValueMap.codec(CodecUtil.listFrom(CustomRainBuilder.Weather.CODEC)).fieldOf("weathers").forGetter(CustomRainBuilder::weathers)
    ).apply(ins, CustomRainBuilder::new));

    public static final Codec<CustomRainBuilder> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            SolarTermValueMap.codec(CodecUtil.listFrom(CustomRainBuilder.Weather.CODEC)).fieldOf("weathers").forGetter(CustomRainBuilder::weathers)
    ).apply(ins, (m) -> new CustomRainBuilder(HolderSet.direct(), m)));

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
                                            weatherList.size() == 1 && weatherList.get(0).timePeriod().isEmpty() ? Optional.of(weatherList.get(0)) : Optional.empty(),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getRainChance).average().orElse(0),
                                            (float) weatherList.stream().mapToDouble(CustomRain.Weather::getThunderChance).average().orElse(0)
                                    );
                                },
                                (a, b) -> b,
                                () -> new Enum2ObjectMap<>(SolarTerm.class)
                        )
                );
    }

    public record Weather(
            // float rainLevel, float thunderLevel,
            // Biome.Precipitation precipitation,
            // WeatherManager.SnowStatus snowStatus,
            Optional<IntProvider> rain,
            Optional<IntProvider> rainDelay,
            Optional<IntProvider> thunder,
            Optional<IntProvider> thunderDelay,
            float rainChance,
            float thunderChance,
            List<TimePeriod> timePeriod) {

        public Weather(
                Optional<IntProvider> rain,
                Optional<IntProvider> rainDelay,
                Optional<IntProvider> thunder,
                float rainChance,
                float thunderChance,
                List<TimePeriod> timePeriod) {
            this(rain, rainDelay, thunder, Optional.empty(),
                    rainChance, thunderChance, timePeriod);
        }

        public Weather(
                float rainChance,
                float thunderChance,
                List<TimePeriod> timePeriod) {
            this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    rainChance, thunderChance, timePeriod);
        }

        public Weather(
                float rainChance,
                float thunderChance) {
            this(rainChance, thunderChance, List.of());
        }

        public static final Codec<Weather> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                // Codec.FLOAT.fieldOf("rain_level").forGetter(Weather::rainLevel),
                // Codec.FLOAT.fieldOf("thunder_level").forGetter(Weather::thunderLevel),
                // Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(Weather::precipitation),
                // WeatherManager.SnowStatus.CODEC.fieldOf("snow_status").forGetter(Weather::snowStatus),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("rain").forGetter(CustomRainBuilder.Weather::rain),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("rain_delay").forGetter(CustomRainBuilder.Weather::rainDelay),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("thunder").forGetter(CustomRainBuilder.Weather::thunder),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("thunder_delay").forGetter(CustomRainBuilder.Weather::thunderDelay),
                Codec.FLOAT.fieldOf("rain_chance").forGetter(CustomRainBuilder.Weather::rainChance),
                Codec.FLOAT.optionalFieldOf("thunder_chance",0f).forGetter(CustomRainBuilder.Weather::thunderChance),
                StringRepresentable.fromEnum(TimePeriod::collectValues).listOf().optionalFieldOf("time_periods",List.of()).forGetter(CustomRainBuilder.Weather::timePeriod)
        ).apply(ins, CustomRainBuilder.Weather::new));
    }


}

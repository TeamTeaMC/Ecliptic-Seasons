package com.teamtea.eclipticseasons.api.data.weather;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CustomRain(int ordinal,
                         List<Weather> weatherList,
                         Optional<BiomeRain> defaultWeather,
                         float rainChance,
                         float thunderChance) implements BiomeRain {
    @Override
    public float getRainChance() {
        return defaultWeather.map(BiomeRain::getRainChance)
                .orElseGet(this::rainChance);
    }

    @Override
    public float getThunderChance() {
        return defaultWeather.map(BiomeRain::getThunderChance)
                .orElseGet(this::thunderChance);
    }

    @Override
    public int getRainDuration(final RandomSource random) {
        return defaultWeather.map(biomeRain -> biomeRain.getRainDuration(random))
                .orElseGet(() -> BiomeRain.super.getRainDuration(random));
    }

    @Override
    public int getRainDelay(final RandomSource random) {
        return defaultWeather.map(biomeRain -> biomeRain.getRainDelay(random))
                .orElseGet(() -> BiomeRain.super.getRainDelay(random));
    }

    @Override
    public int getThunderDuration(final RandomSource random) {
        return defaultWeather.map(biomeRain -> biomeRain.getThunderDuration(random))
                .orElseGet(() -> BiomeRain.super.getThunderDuration(random));
    }

    @Override
    public int getThunderDelay(final RandomSource random) {
        return defaultWeather.map(biomeRain -> biomeRain.getThunderDelay(random))
                .orElseGet(() -> BiomeRain.super.getThunderDelay(random));
    }

    @Override
    public BiomeRain resolve(Level level) {
        if (defaultWeather.isPresent())
            return defaultWeather.get();
        if (weatherList.isEmpty()) return FlatRain.NONE;
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(level.getTimeOfDay(1));
        List<Weather> selectList = new ArrayList<>();
        for (var weather : weatherList) {
            if (weather.timePeriod().isEmpty() || weather.timePeriod().contains(timePeriod)) {
                selectList.add(weather);
            }
        }
        if (selectList.isEmpty()) return FlatRain.NONE;
        return selectList.get(level.getRandom().nextInt(selectList.size()));
    }

    public record Weather(
            int ordinal,
            Optional<IntProvider> rain,
            Optional<IntProvider> rainDelay,
            Optional<IntProvider> thunder,
            Optional<IntProvider> thunderDelay,
            float rainChance,
            float thunderChance,
            List<TimePeriod> timePeriod
    ) implements BiomeRain {

        public static Weather of(SolarTerm solarTerm, CustomRainBuilder.Weather weather) {
            return new Weather(
                    solarTerm.ordinal(),
                    weather.rain(),
                    weather.rainDelay(),
                    weather.thunder(),
                    weather.thunderDelay(),
                    weather.rainChance(),
                    weather.thunderChance(),
                    weather.timePeriod()
            );
        }

        @Override
        public int getRainDuration(final RandomSource random) {
            return this.rain.map(provider -> provider.sample(random))
                    .orElseGet(() -> BiomeRain.super.getRainDuration(random));
        }

        @Override
        public int getRainDelay(final RandomSource random) {
            return this.rainDelay.map(provider -> provider.sample(random))
                    .orElseGet(() -> BiomeRain.super.getRainDelay(random));
        }

        @Override
        public int getThunderDuration(final RandomSource random) {
            return this.thunder.map(provider -> provider.sample(random))
                    .orElseGet(() -> BiomeRain.super.getThunderDuration(random));
        }

        @Override
        public int getThunderDelay(final RandomSource random) {
            return this.thunderDelay.map(provider -> provider.sample(random))
                    .orElseGet(() -> BiomeRain.super.getThunderDelay(random));
        }

    }
}

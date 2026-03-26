package com.teamtea.eclipticseasons.api.data.weather;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import lombok.Data;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.TimeCommand;
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
                         float thunderChance,
                         float snowAccumulationSpeed,
                         float snowMeltSpeed) implements BiomeRain {
    @Override
    public float getSnowAccumulationSpeed() {
        return defaultWeather.map(BiomeRain::getSnowAccumulationSpeed)
                .orElseGet(this::snowAccumulationSpeed);
    }

    @Override
    public float getSnowMeltSpeed() {
        return defaultWeather.map(BiomeRain::getSnowMeltSpeed)
                .orElseGet(this::snowMeltSpeed);
    }

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
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
        List<Weather> selectList = new ArrayList<>();
        int allWeights = 0;
        for (var weather : weatherList) {
            if (weather.timePeriod().isEmpty() || weather.timePeriod().contains(timePeriod)) {
                selectList.add(weather);
                allWeights += weather.weight();
            }
        }
        if (selectList.isEmpty()) return FlatRain.NONE;
        if (selectList.size() == 1) return selectList.getFirst();
        int result = level.getRandom().nextInt(allWeights);
        for (Weather weather : selectList) {
            result -= weather.weight();
            if (result <= 0) {
                return weather;
            }
        }
        return FlatRain.NONE;
    }

    @Override
    public boolean isResolvable() {
        return true;
    }

    @Override
    public List<BiomeRain> resolveOrderedList() {
        return new ArrayList<>(weatherList);
    }

    public record Weather(
            int ordinal,
            Optional<IntProvider> rain,
            Optional<IntProvider> rainDelay,
            Optional<IntProvider> thunder,
            Optional<IntProvider> thunderDelay,
            float rainChance,
            float thunderChance,
            List<TimePeriod> timePeriod,
            Optional<Holder<WeatherEffect>> specialEffect,
            int weight,
            float snowAccumulationSpeed,
            float snowMeltSpeed) implements BiomeRain {

        public static Weather of(SolarTerm solarTerm, CustomRainBuilder.Weather weather) {
            return new Weather(
                    solarTerm.ordinal(),
                    weather.getRain(),
                    weather.getRainDelay(),
                    weather.getThunder(),
                    weather.getThunderDelay(),
                    weather.getRainChance(),
                    weather.getThunderChance(),
                    weather.getTimePeriod(),
                    weather.getSpecialEffect(),
                    weather.getWeight(),
                    weather.getSnowAccumulationSpeed(),
                    weather.getSnowMeltSpeed()
            );
        }

        @Override
        public float getThunderChance() {
            return thunderChance();
        }

        @Override
        public float getRainChance() {
            return rainChance();
        }

        @Override
        public float getSnowAccumulationSpeed() {
            return snowAccumulationSpeed();
        }

        @Override
        public float getSnowMeltSpeed() {
            return snowMeltSpeed();
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

        @Override
        public boolean hasSpecialEffect() {
            return specialEffect.isPresent();
        }

        @Override
        public Holder<WeatherEffect> getSpecialEffect() {
            return specialEffect.orElseGet(BiomeRain.super::getSpecialEffect);
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }
}

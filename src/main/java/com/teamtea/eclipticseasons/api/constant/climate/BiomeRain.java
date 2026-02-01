package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.NoneEffect;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

public interface BiomeRain {
    int ordinal();

    float DEFAULT_RAIN_CHANE = 0;
    float DEFAULT_THUNDER_CHANCE = 0;

    @Deprecated(forRemoval = true, since = "0.12")
    public default float getRainChane() {
        return DEFAULT_RAIN_CHANE;
    }

    public default float getRainChance() {
        return getRainChane();
    }

    public default float getThunderChance() {
        return DEFAULT_THUNDER_CHANCE;
    }

    public default float getSnowAccumulationSpeed() {
        return 1;
    }

    public default float getSnowMeltSpeed() {
        return 1;
    }

    public default SolarTerm getSolarTerm() {
        return SolarTerm.collectValues()[this.ordinal()];
    }

    public default Season getSeason() {
        return Season.collectValues()[this.ordinal() / 6];
    }

    public default BiomeRain resolve(Level level) {
        return this;
    }

    public default int getRainDuration(RandomSource random) {
        return ServerLevel.RAIN_DURATION.sample(random);
    }

    public default int getRainDelay(RandomSource random) {
        return ServerLevel.RAIN_DELAY.sample(random);
    }

    public default int getThunderDuration(RandomSource random) {
        return ServerLevel.THUNDER_DURATION.sample(random);
    }

    public default int getThunderDelay(RandomSource random) {
        return ServerLevel.THUNDER_DELAY.sample(random);
    }

    public default boolean hasSpecialEffect() {
        return false;
    }

    public default Holder<WeatherEffect> getSpecialEffect() {
        return null;
    }

    public default boolean isDynamic() {
        return false;
    }


    public default boolean isResolvable() {
        return false;
    }

    public default List<BiomeRain> resolveOrderedList() {
        return List.of(this);
    }

}

package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public interface BiomeRain {
    int ordinal();

    float DEFAULT_RAIN_CHANE = 0;
    float DEFAULT_THUNDER_CHANCE = 0;

    @Deprecated(forRemoval = true,since = "0.12")
    public default float getRainChane() {
        return DEFAULT_RAIN_CHANE;
    }

    public default float getRainChance() {
        return getRainChane();
    }

    public default float getThunderChance() {
        return DEFAULT_THUNDER_CHANCE;
    }

    public default SolarTerm getSolarTerm() {
        return SolarTerm.collectValues()[this.ordinal()];
    }

    public default Season getSeason() {
        return Season.collectValues()[this.ordinal() / 6];
    }

    public default BiomeRain cast(Level level) {
        return this;
    }

    public default int sampleRain(RandomSource random) {
        return ServerLevel.RAIN_DURATION.sample(random);
    }

    public default int sampleRainDelay(RandomSource random) {
        return ServerLevel.RAIN_DELAY.sample(random);
    }

    public default int sampleThunder(RandomSource random) {
        return ServerLevel.THUNDER_DURATION.sample(random);
    }

    public default int sampleThunderDelay(RandomSource random) {
        return ServerLevel.THUNDER_DELAY.sample(random);
    }

}

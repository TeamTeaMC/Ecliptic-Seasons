package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.Locale;

public enum TemperateRain implements BiomeRain {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0.3F),
    RAIN_WATER(0.5F, 0.08f),
    INSECTS_AWAKENING(0.55F, 0.15f),
    SPRING_EQUINOX(0.5F, 0.1f),
    FRESH_GREEN(0.65F, 0.05f),
    GRAIN_RAIN(0.75F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.9F),
    LESSER_FULLNESS(0.7F, 0.1f),
    GRAIN_IN_EAR(0.6F, 0.15f),
    SUMMER_SOLSTICE(0.7F, 0.25f),
    LESSER_HEAT(0.65F, 0.2f),
    GREATER_HEAT(0.5F, 0.05f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.42F),
    END_OF_HEAT(0.4F),
    WHITE_DEW(0.35F),
    AUTUMNAL_EQUINOX(0.32F),
    COLD_DEW(0.3F),
    FIRST_FROST(0.25F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0.3F),
    LIGHT_SNOW(0.4F, 0.05f),
    HEAVY_SNOW(0.5F),
    WINTER_SOLSTICE(0.45F),
    LESSER_COLD(0.4F),
    GREATER_COLD(0.2F),

    NONE(0.0F, 0.0F);

    private final float rainChane;
    private final float thunderChance;

    TemperateRain(float rainChane) {
        this(rainChane, 0);
    }

    TemperateRain(float rainChane, float thunderChance) {
        this.rainChane = rainChane;
        this.thunderChance = thunderChance;
    }

    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public float getRainChance() {
        return rainChane;
    }

    @Override
    public float getThunderChance() {
        return thunderChance;
    }

    @Override
    public SolarTerm getSolarTerm() {
        return SolarTerm.collectValues()[this.ordinal()];
    }

    @Override
    public Season getSeason() {
        return Season.collectValues()[this.ordinal() / 6];
    }

    private static final TemperateRain[] values = TemperateRain.values();
    public static TemperateRain[] collectValues() {
        return values;
    }
}

package com.teamtea.eclipticseasons.api.constant.climate.seasonal;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.Locale;

public enum ColdRain implements BiomeRain {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0.1F),
    RAIN_WATER(0.3F, 0.008f),
    INSECTS_AWAKENING(0.25F, 0.015f),
    SPRING_EQUINOX(0.2F, 0.01f),
    FRESH_GREEN(0.35F, 0.005f),
    GRAIN_RAIN(0.45F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.5F),
    LESSER_FULLNESS(0.5F, 0.08f),
    GRAIN_IN_EAR(0.6F, 0.05f),
    SUMMER_SOLSTICE(0.75F, 0.15f),
    LESSER_HEAT(0.8F, 0.1f),
    GREATER_HEAT(0.7F, 0.025f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.32F),
    END_OF_HEAT(0.3F),
    WHITE_DEW(0.15F),
    AUTUMNAL_EQUINOX(0.12F),
    COLD_DEW(0.3F),
    FIRST_FROST(0.25F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0.3F),
    LIGHT_SNOW(0.1F),
    HEAVY_SNOW(0.2F),
    WINTER_SOLSTICE(0.25F),
    LESSER_COLD(0.45F),
    GREATER_COLD(0.35F),

    NONE(0.0F, 0.0F);

    private final float rainChane;
    private final float thunderChance;

    ColdRain(float rainChane) {
        this(rainChane, 0);
    }

    ColdRain(float rainChane, float thunderChance) {
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

    private static final ColdRain[] values = ColdRain.values();
    public static ColdRain[] collectValues() {
        return values;
    }
}

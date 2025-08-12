package com.teamtea.eclipticseasons.api.constant.climate.seasonal;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.Locale;

public enum HotRain implements BiomeRain {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0.2F),
    RAIN_WATER(0.35F, 0.04f),
    INSECTS_AWAKENING(0.35F, 0.05f),
    SPRING_EQUINOX(0.2F, 0.01f),
    FRESH_GREEN(0.35F, 0.005f),
    GRAIN_RAIN(0.45F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.5F),
    LESSER_FULLNESS(0.45F, 0.1f),
    GRAIN_IN_EAR(0.95F, 0.5f),
    SUMMER_SOLSTICE(0.9F, 0.28f),
    LESSER_HEAT(0.85F, 0.3f),
    GREATER_HEAT(0.75F, 0.25f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.72F, 0.2f),
    END_OF_HEAT(0.7F, 0.1f),
    WHITE_DEW(0.65F, 0.08f),
    AUTUMNAL_EQUINOX(0.62F, 0.05f),
    COLD_DEW(0.5F, 0.02f),
    FIRST_FROST(0.45F, 0.005f),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0.3F),
    LIGHT_SNOW(0.2F),
    HEAVY_SNOW(0.3F),
    WINTER_SOLSTICE(0.25F),
    LESSER_COLD(0.2F),
    GREATER_COLD(0.15F),

    NONE(0.0F, 0.0F);

    private final float rainChane;
    private final float thunderChance;

    HotRain(float rainChane) {
        this(rainChane, 0);
    }

    HotRain(float rainChane, float thunderChance) {
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

    private static final HotRain[] values = HotRain.values();
    public static HotRain[] collectValues() {
        return values;
    }
}

package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.Arrays;

public enum SnowTerm implements ISnowTerm {

    T1(SolarTerm.NONE, SolarTerm.NONE, 1f),
    T095(SolarTerm.GREATER_COLD, SolarTerm.GREATER_COLD, 0.95f),
    T08(SolarTerm.WINTER_SOLSTICE, SolarTerm.GREATER_COLD, 0.81f),
    T07(SolarTerm.HEAVY_SNOW, SolarTerm.GREATER_COLD, 0.7f),
    T06(SolarTerm.LIGHT_SNOW, SolarTerm.GREATER_COLD, 0.6f),
    T05(SolarTerm.BEGINNING_OF_WINTER, SolarTerm.GREATER_COLD, 0.5f),
    T04(SolarTerm.FIRST_FROST, SolarTerm.GREATER_COLD, 0.4f),
    T03(SolarTerm.COLD_DEW, SolarTerm.BEGINNING_OF_SPRING, 0.3f),
    T02(SolarTerm.AUTUMNAL_EQUINOX, SolarTerm.RAIN_WATER, 0.2f),
    T015(SolarTerm.WHITE_DEW, SolarTerm.INSECTS_AWAKENING, 0.15f),
    T01(SolarTerm.BEGINNING_OF_AUTUMN, SolarTerm.FRESH_GREEN, 0.1f),
    T005(SolarTerm.GREATER_HEAT, SolarTerm.GRAIN_RAIN, 0.005f),
    T000(SolarTerm.LESSER_HEAT, SolarTerm.BEGINNING_OF_SUMMER, 0.0f),
    // need reverse
    TN(SolarTerm.SUMMER_SOLSTICE, SolarTerm.GRAIN_IN_EAR, Float.NEGATIVE_INFINITY),
    NONE(SolarTerm.NONE, SolarTerm.NONE, 2f);

    private final SolarTerm start;
    private final SolarTerm end;
    private final float temp;

    SnowTerm(SolarTerm start, SolarTerm end, float temp) {
        this.start = start;
        this.end = end;
        this.temp = temp;
    }

    @Override
    public SolarTerm getStart() {
        return start;
    }

    @Override
    public SolarTerm getEnd() {
        return end;
    }

    private static final SnowTerm[] snowTerms = SnowTerm.values();

    public static SnowTerm[] collectValues() {
        return snowTerms;
    }

    private static final SnowTerm[] validSnowTerms = Arrays.stream(SnowTerm.values())
            .filter(s -> s != NONE).toArray(SnowTerm[]::new);

    public static SnowTerm[] collectValidValues() {
        return validSnowTerms;
    }

    public static SnowTerm get(float biomeTmep, float tempChange) {
        float oldT = biomeTmep;
        biomeTmep = biomeTmep + tempChange;
        for (SnowTerm value : SnowTerm.collectValidValues()) {
            // if (value.stable) {
            //     if (biomeTmep >= value.temp) return value;
            // } else {
            //     float min = value.temp + value.hotAdd;
            //     float max = value.temp + value.coldAdd;
            //     float tempLimitDynamic = min + randomSource.nextFloat() * (max - min);
            //     if (biomeTmep >= tempLimitDynamic) return value;
            // }
            if (biomeTmep >= value.temp)
                return value;
        }
        return SnowTerm.NONE;
    }
}

package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.Arrays;

public enum SnowTerm implements ISnowTerm {

    T1(SolarTerm.NONE, SolarTerm.NONE, 1.0f),
    T095(SolarTerm.GREATER_COLD, SolarTerm.GREATER_COLD, 0.95f),
    T015(SolarTerm.BEGINNING_OF_WINTER, SolarTerm.GREATER_COLD, 0.15f),
    T000(SolarTerm.BEGINNING_OF_AUTUMN, SolarTerm.GRAIN_IN_EAR, 0f),
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

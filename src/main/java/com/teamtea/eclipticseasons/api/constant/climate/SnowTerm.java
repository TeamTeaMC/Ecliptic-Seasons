package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

public enum SnowTerm {

    T1(SolarTerm.NONE, SolarTerm.NONE),
    T08(SolarTerm.WINTER_SOLSTICE, SolarTerm.LESSER_COLD),
    T06(SolarTerm.LIGHT_SNOW, SolarTerm.GREATER_COLD),
    T05(SolarTerm.BEGINNING_OF_WINTER, SolarTerm.GREATER_COLD),
    T04(SolarTerm.FIRST_FROST, SolarTerm.GREATER_COLD),
    T03(SolarTerm.COLD_DEW, SolarTerm.BEGINNING_OF_SPRING),
    T02(SolarTerm.AUTUMNAL_EQUINOX, SolarTerm.RAIN_WATER),
    T015(SolarTerm.WHITE_DEW, SolarTerm.INSECTS_AWAKENING),
    T01(SolarTerm.BEGINNING_OF_AUTUMN, SolarTerm.FRESH_GREEN),
    T005(SolarTerm.GREATER_HEAT, SolarTerm.GRAIN_RAIN),
    T001(SolarTerm.LESSER_HEAT, SolarTerm.BEGINNING_OF_SUMMER),
    T0(SolarTerm.SUMMER_SOLSTICE, SolarTerm.SUMMER_SOLSTICE),
    NONE(SolarTerm.NONE, SolarTerm.NONE);

    private final SolarTerm start;
    private final SolarTerm end;

    SnowTerm(SolarTerm start, SolarTerm end) {
        this.start = start;
        this.end = end;
    }

    public SolarTerm getStart() {
        return start;
    }

    public SolarTerm getEnd() {
        return end;
    }

    public boolean maySnow(SolarTerm solarTerm) {
        return solarTerm.isInTerms(getStart(),getEnd());
    }
}

package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

public interface ISnowTerm {

    SolarTerm getStart();

    SolarTerm getEnd();

    default boolean maySnow(SolarTerm solarTerm) {
        return solarTerm.isInTerms(getStart(), getEnd());
    }
}

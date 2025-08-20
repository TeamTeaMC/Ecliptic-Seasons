package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

public interface ISnowTerm {

    SolarTerm getStart();

    SolarTerm getEnd();

    default ISnowTerm cast(float tempChange) {
        return this;
    }

    default boolean maySnow(SolarTerm solarTerm) {
        return solarTerm.isInTerms(getStart(), getEnd());
    }

    @Deprecated
    default boolean maySnow(SolarTerm solarTerm, float tempChange) {
        return cast(tempChange).maySnow(solarTerm);
    }
}

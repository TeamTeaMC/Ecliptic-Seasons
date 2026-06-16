package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;

public interface ISnowyGetter extends ISnowyGetterProvider {
    BiomeHolder getBiomeHolder();

    SnowyStatusKeeper getSnowyStatusKeeper();

    NoneSnowArea getNoneSnowArea();
}

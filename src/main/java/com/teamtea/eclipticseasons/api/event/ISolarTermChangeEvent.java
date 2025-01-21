package com.teamtea.eclipticseasons.api.event;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.world.level.Level;

public interface ISolarTermChangeEvent extends IESEvent{
    Level getLevel();

    SolarTerm getNewSolarTerm();

    SolarTerm getOldSolarTerm();

    int getSolarDays();


}

package com.teamtea.eclipticseasons.common.handler;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.World;


public class SolarUtil {


    public static SolarDataManager getProvider(World level) {
        return SolarHolders.getSaveData(level);
    }

    public static Season getSeason(World level) {
        return SolarHolders.getSaveDataLazy(level).map(data -> data.getSolarTerm().getSeason()).orElse(Season.NONE);
    }

    public static SolarTerm getSolarTerm(World level) {
        return SolarHolders.getSaveDataLazy(level).map(SolarDataManager::getSolarTerm).orElse(SolarTerm.NONE);
    }
}

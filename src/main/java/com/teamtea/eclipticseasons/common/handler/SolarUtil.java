package com.teamtea.eclipticseasons.common.handler;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;


@Deprecated(forRemoval = true)
public class SolarUtil {

    @Deprecated(forRemoval = true)
    public static SolarDataManager getProvider(Level level) {
        return SolarHolders.getSaveData(level);
    }

    @Deprecated(forRemoval = true)
    public static Season getSeason(Level level) {
        return SolarHolders.getSaveDataLazy(level).map(data -> data.getSolarTerm().getSeason()).orElse(Season.NONE);
    }

    @Deprecated(forRemoval = true)
    public static SolarTerm getSolarTerm(Level level) {
        return SolarHolders.getSaveDataLazy(level).map(SolarDataManager::getSolarTerm).orElse(SolarTerm.NONE);
    }
}

package com.teamtea.eclipticseasons.common.handler;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;


/**
 * SolarDataManager is an inner class should not use.
 * <p>
 * For season and solar term query, use {@link com.teamtea.eclipticseasons.api.EclipticSeasonsApi} instead
 * **/
@Deprecated(forRemoval = true,since = "0.8")
public class SolarUtil {
    @Deprecated(forRemoval = true,since = "0.8")
    public static @Nullable SolarDataManager getProvider(Level level) {
        return SolarHolders.getSaveData(level);
    }

    @Deprecated(forRemoval = true,since = "0.8")
    public static Season getSeason(Level level) {
        return EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason();
    }

    @Deprecated(forRemoval = true,since = "0.8")
    public static SolarTerm getSolarTerm(Level level) {
        return EclipticSeasonsApi.getInstance().getSolarTerm(level);
    }
}

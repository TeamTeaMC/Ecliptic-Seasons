package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.World;


public class SimpleUtil {
    public static void testTime(Runnable runnable) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000 * 100; i++) {
            runnable.run();
        }
        EclipticSeasons.logger(System.currentTimeMillis() - time);
    }

    @Deprecated
    public static SolarTerm getNowSolarTerm(World level) {
        return EclipticUtil.getNowSolarTerm(level);
    }

    @Deprecated
    public static boolean isDay(World level) {
        return EclipticUtil.isDay(level);
    }

    @Deprecated
    public static boolean isNight(World level) {
        return EclipticUtil.isNight(level);
    }

    @Deprecated
    public static int getNightTime(World level) {
        return EclipticUtil.getNightTime(level);
    }

    @Deprecated
    public static boolean isNoon(World level) {
        return EclipticUtil.isNoon(level);
    }

    @Deprecated
    public static boolean isEvening(World level) {
        return EclipticUtil.isEvening(level);
    }
}

package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import net.minecraft.world.level.Level;


public class SimpleUtil {
    public static void testTime(Runnable runnable) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000 * 100; i++) {
            runnable.run();
        }
        EclipticSeasons.logger(System.currentTimeMillis() - time);
    }

    @Deprecated(forRemoval = true)
    public static SolarTerm getNowSolarTerm(Level level) {
        return EclipticUtil.getNowSolarTerm(level);
    }

    @Deprecated(forRemoval = true)
    public static boolean isDay(Level level) {
        return EclipticUtil.isDay(level);
    }

    @Deprecated(forRemoval = true)
    public static boolean isNight(Level level) {
        return EclipticUtil.isNight(level);
    }

    @Deprecated(forRemoval = true)
    public static int getNightTime(Level level) {
        return EclipticUtil.getNightTime(level);
    }

    @Deprecated(forRemoval = true)
    public static boolean isNoon(Level level) {
        return EclipticUtil.isNoon(level);
    }

    @Deprecated(forRemoval = true)
    public static boolean isEvening(Level level) {
        return EclipticUtil.isEvening(level);
    }
}

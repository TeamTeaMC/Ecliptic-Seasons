package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EclipticUtil {
    public static SolarTerm getNowSolarTerm(Level level) {
        var sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTerm();
        return SolarTerm.NONE;
    }

    public static boolean isDay(Level level) {
        long dayTime = level.dimensionType().fixedTime().orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        long halfTermTime = termTime / 2;
        if (termTime <= 12000) {
            return 6000 - (halfTermTime) < dayTime && dayTime < 6000 + (halfTermTime);
        } else return dayTime >= 24000 + (6000 - (halfTermTime))
                || dayTime <= 6000 + (halfTermTime);
    }

    public static boolean isNight(Level level) {
        return !isDay(level);
    }

    public static int getNightTime(Level level) {
        long termTime = getNowSolarTerm(level).getDayTime();
        return (int) (6000 + (termTime / 2));
    }

    public static boolean isNoon(Level level) {
        long dayTime = level.dimensionType().fixedTime().orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        return 6000 - (termTime / 6) < dayTime && dayTime < 6000 + (termTime / 4);
    }

    public static boolean isEvening(Level level) {
        long dayTime = level.dimensionType().fixedTime().orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        return 6000 + (termTime * 2 / 5) < dayTime && dayTime < 6000 + (termTime / 2) + (24000 - termTime) * 3 / 4;
    }


    public static EclipticSeasonsApi INSTANCE;

    static {
        INSTANCE = new EclipticSeasonsApi() {
            @Override
            public SolarTerm getSolarTerm(Level level) {
                return EclipticUtil.getNowSolarTerm(level);
            }

            @Override
            public boolean isDay(Level level) {
                return EclipticUtil.isDay(level);
            }

            @Override
            public boolean isNight(Level level) {
                return EclipticUtil.isNight(level);
            }

            @Override
            public int getNightTime(Level level) {
                return EclipticUtil.getNightTime(level);
            }

            @Override
            public boolean isNoon(Level level) {
                return EclipticUtil.isNoon(level);
            }

            @Override
            public boolean isEvening(Level level) {
                return EclipticUtil.isEvening(level);
            }

            @Override
            public boolean isSnowySurfaceAt(Level level, BlockPos pos) {
                long seed = level.getBlockState(pos).getSeed(pos);
                return MapChecker.shouldSnowAt(level, pos, level.getBlockState(pos), level.getRandom(), seed);
            }
        };
    }
}

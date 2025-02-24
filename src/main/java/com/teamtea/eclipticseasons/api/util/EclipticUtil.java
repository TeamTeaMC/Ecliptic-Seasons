package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EclipticUtil {
    public static SolarTerm getNowSolarTerm(World level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTerm();
        return SolarTerm.NONE;
    }

    public static int getNowSolarDay(World level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTermsDay();
        return 0;
    }

    public static int getTimeInSolarTerm(World level) {
        return EclipticUtil.getNowSolarDay(level) -
                CommonConfig.Season.lastingDaysOfEachTerm.get() * EclipticUtil.getNowSolarTerm(level).ordinal();
    }

    public static boolean isDay(World level) {
        long dayTime = level.dimensionType().fixedTime.orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        long halfTermTime = termTime / 2;
        if (termTime <= 12000) {
            return 6000 - (halfTermTime) < dayTime && dayTime < 6000 + (halfTermTime);
        } else return dayTime >= 24000 + (6000 - (halfTermTime))
                || dayTime <= 6000 + (halfTermTime);
    }

    public static boolean isNight(World level) {
        return !isDay(level);
    }

    public static int getNightTime(World level) {
        long termTime = getNowSolarTerm(level).getDayTime();
        return (int) (6000 + (termTime / 2));
    }

    public static boolean isNoon(World level) {
        long dayTime = level.dimensionType().fixedTime.orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        return 6000 - (termTime / 6) < dayTime && dayTime < 6000 + (termTime / 4);
    }

    public static boolean isEvening(World level) {
        long dayTime = level.dimensionType().fixedTime.orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long termTime = getNowSolarTerm(level).getDayTime();
        return 6000 + (termTime * 2 / 5) < dayTime && dayTime < 6000 + (termTime / 2) + (24000 - termTime) * 3 / 4;
    }

    public static Humidity getHumidityAt(World level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos);
        Temperature temperatureLevel = Temperature.getTemperatureLevel(standBiome.getTemperature(pos));
        Rainfall rainfall = Rainfall.getRainfallLevel(standBiome.getDownfall());
        return Humidity.getHumid(rainfall, temperatureLevel);
    }

    public static Rainfall getRainfallAt(World level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos);
        return Rainfall.getRainfallLevel(standBiome.getDownfall());
    }

    public static Temperature getTemperatureAt(World level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos);
        return Temperature.getTemperatureLevel(standBiome.getTemperature(pos));
    }

}

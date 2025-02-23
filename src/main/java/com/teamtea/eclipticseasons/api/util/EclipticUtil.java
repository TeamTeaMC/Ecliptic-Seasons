package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
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

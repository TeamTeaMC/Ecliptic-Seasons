package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class EclipticUtil {
    public static final EclipticSeasonsApi INSTANCE = new EclipticSeasonsApi() {
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
        public boolean isRainOrSnowAt(Level level, BlockPos pos) {
            if (!WeatherManager.hasWeatherAt(level, pos)) {
                return false;
            } else {
                return WeatherManager.getRainOrSnow(level, level.getBiome(pos).value(), pos) != Biome.Precipitation.NONE;
            }
        }

        @Override
        public boolean isRainAt(Level level, BlockPos pos) {
            return level.isRainingAt(pos);
        }

        @Override
        public boolean isSnowAt(Level level, BlockPos pos) {
            if (!WeatherManager.hasWeatherAt(level, pos)) {
                return false;
            } else {
                return WeatherManager.getRainOrSnow(level, level.getBiome(pos).value(), pos) == Biome.Precipitation.SNOW;
            }
        }

        @Override
        public boolean isThunderAt(Level level, BlockPos pos) {
            return WeatherManager.isThunderAt(level, pos);
        }

        @Override
        public Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos) {
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
            pos = new BlockPos(pos.getX(), height, pos.getZ());
            return WeatherManager.getPrecipitationAt(level, level.getBiome(pos).value(), pos);
        }
    };

    public static SolarTerm getNowSolarTerm(Level level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTerm();
        return SolarTerm.NONE;
    }

    public static int getNowSolarDay(Level level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTermsDay();
        return 0;
    }

    public static int getTimeInSolarTerm(Level level) {
        return EclipticUtil.getNowSolarDay(level) -
                CommonConfig.Season.lastingDaysOfEachTerm.get() * EclipticUtil.getNowSolarTerm(level).ordinal();
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

    public static Humidity getHumidityAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        Temperature temperatureLevel = Temperature.getTemperatureLevel(standBiome.getTemperature(pos));
        Rainfall rainfall = Rainfall.getRainfallLevel(standBiome.getModifiedClimateSettings().downfall());
        return Humidity.getHumid(rainfall, temperatureLevel);
    }

    public static Rainfall getRainfallAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Rainfall.getRainfallLevel(standBiome.getModifiedClimateSettings().downfall());
    }

    public static Temperature getTemperatureAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Temperature.getTemperatureLevel(standBiome.getTemperature(pos));
    }

}

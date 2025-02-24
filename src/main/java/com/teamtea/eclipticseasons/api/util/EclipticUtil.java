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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public class EclipticUtil {
    public static final EclipticSeasonsApi INSTANCE = new EclipticSeasonsApi() {
        @Override
        public SolarTerm getSolarTerm(World level) {
            return EclipticUtil.getNowSolarTerm(level);
        }

        @Override
        public boolean isDay(World level) {
            return EclipticUtil.isDay(level);
        }

        @Override
        public boolean isNight(World level) {
            return EclipticUtil.isNight(level);
        }

        @Override
        public int getNightTime(World level) {
            return EclipticUtil.getNightTime(level);
        }

        @Override
        public boolean isNoon(World level) {
            return EclipticUtil.isNoon(level);
        }

        @Override
        public boolean isEvening(World level) {
            return EclipticUtil.isEvening(level);
        }


        @Override
        public boolean isRainOrSnowAt(World level, BlockPos pos) {
            if (!WeatherManager.hasWeatherAt(level, pos)) {
                return false;
            } else {
                return WeatherManager.getRainOrSnow(level, level.getBiome(pos), pos) != Biome.RainType.NONE;
            }
        }

        @Override
        public boolean isRainAt(World level, BlockPos pos) {
            return level.isRainingAt(pos);
        }

        @Override
        public boolean isSnowAt(World level, BlockPos pos) {
            if (!WeatherManager.hasWeatherAt(level, pos)) {
                return false;
            } else {
                return WeatherManager.getRainOrSnow(level, level.getBiome(pos), pos) == Biome.RainType.SNOW;
            }
        }

        @Override
        public boolean isThunderAt(World level, BlockPos pos) {
            return WeatherManager.isThunderAt(level, pos);
        }

        @Override
        public Biome.RainType getPrecipitationAt(World level, BlockPos pos) {
            int height = level.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
            pos = new BlockPos(pos.getX(), height, pos.getZ());
            return WeatherManager.getPrecipitationAt(level, level.getBiome(pos), pos);
        }
    };

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

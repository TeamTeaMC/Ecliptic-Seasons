package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class EclipticUtil {
    public static SolarTerm getNowSolarTerm(Level level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
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

    public static boolean useSolarWeather() {
        return CommonConfig.isUseSolarWeather();
    }

    public static boolean isSolarWeatherClosed() {
        return !useSolarWeather();
    }

    public static WeatherMode getWeatherMode(Level level) {
        if (!useSolarWeather()) return WeatherMode.DEFAULT;
        return MapChecker.isValidDimension(level) ? WeatherMode.BIOME : WeatherMode.DEFAULT;
    }

    public static boolean hasLocalWeather(Level level) {
        return getWeatherMode(level) != WeatherMode.DEFAULT;
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

            @Override
            public boolean isSnowyBlock(Level level, BlockState state, BlockPos pos) {
                return MapColorReplacer.getTopSnowColor(level, state, pos) != null;
            }

            @Override
            public boolean isRainOrSnowAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return WeatherManager.isRainingOrSnowAt(level, pos);

                // use this to check if underground
                if (!level.isRaining()) {
                    return false;
                }
                if (!level.canSeeSky(pos)) {
                    return false;
                } else return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();

            }

            @Override
            public boolean isRainAt(Level level, BlockPos pos) {
                // if (hasLocalWeather(level))
                // use mc method we have fixed it
                return level.isRainingAt(pos);

                // use this to check if underground
                // if (!level.isRaining()) {
                //     return false;
                // } else if (!level.canSeeSky(pos)) {
                //     return false;
                // } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                //     return false;
                // } else {
                //     return this.getPrecipitationAt(level, pos) == Biome.Precipitation.RAIN;
                // }
            }

            @Override
            public boolean isSnowAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return isHereSnowy(level, pos);
                if (!level.isRaining()) {
                    return false;
                } else if (!level.canSeeSky(pos)) {
                    return false;
                } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                    return false;
                } else {
                    return this.getPrecipitationAt(level, pos) == Biome.Precipitation.SNOW;
                }
            }

            @Override
            public boolean isThunderAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return WeatherManager.isThunderAt(level, pos);

                // use this to check if underground
                if (!level.isThundering()) {
                    return false;
                }
                if (!level.canSeeSky(pos)) {
                    return false;
                } else return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();
            }

            @Override
            public Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return WeatherManager.getPrecipitationAt(level, MapChecker.getSurfaceBiome(level, pos).value(), pos);
                return VanillaWeather.handlePrecipitationAt(level, MapChecker.getSurfaceBiome(level, pos).value(), pos);
            }
        };
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

    public static boolean isHereWithSnow(Level level, BlockPos pos) {
        return WeatherManager.getSnowDepthAtBiome(level, MapChecker.getSurfaceBiome(level, pos).value()) > 0;
    }

    public static boolean isHereSunny(Level level, BlockPos pos) {
        return WeatherManager.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.NONE;
    }

    public static boolean isHereRainy(Level level, BlockPos pos) {
        return WeatherManager.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isHereSnowy(Level level, BlockPos pos) {
        return WeatherManager.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.SNOW;
    }

    public static float getTemperatureFloat(Level level, Biome biome, BlockPos blockPos) {
        return biome.getTemperature(blockPos) + getNowSolarTerm(level).getTemperatureChange();
    }

    public static Humidity getHumidityAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        Temperature temperatureLevel = Temperature.getTemperatureLevel(getTemperatureFloat(level, standBiome, pos));
        Rainfall rainfall = Rainfall.getRainfallLevel(standBiome.getModifiedClimateSettings().downfall());
        return Humidity.getHumid(rainfall, temperatureLevel);
    }

    public static Rainfall getRainfallAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Rainfall.getRainfallLevel(standBiome.getModifiedClimateSettings().downfall());
    }

    public static Temperature getTemperatureAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Temperature.getTemperatureLevel(getTemperatureFloat(level, standBiome, pos));
    }
}

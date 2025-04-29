package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
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

            @Deprecated
            @Override
            public boolean isSnowySurfaceAt(Level level, BlockPos pos) {
                if (CommonConfig.Season.snowyWinter.get()) {
                    BlockState state = level.getBlockState(pos);
                    return MapChecker.shouldSnowAt(level, pos, state, level.getRandom(), state.getSeed(pos));
                }
                return false;
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
        return getTemperatureFloat(level, getNowSolarTerm(level), biome, blockPos, !level.isClientSide());
    }

    public static float getTemperatureFloat(Level level, SolarTerm solarTerm, Biome biome, BlockPos blockPos, boolean isServer) {
        float modify = solarTerm.getSeason() == Season.SUMMER
                && isNoon(level)
                && level.getBrightness(LightLayer.SKY, blockPos.above()) > 12 ? solarTerm.getTemperatureChange() / 2 : 0;
        return biome.getTemperature(blockPos) +
                BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getTemperatureChange(solarTerm) + modify;
    }

    public static float getTemperatureFloatConstant(SolarTerm solarTerm, Biome biome, boolean isServer) {
        return BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getTemperature(solarTerm);
    }

    public static float getDownfallFloat(Level level, Biome biome, BlockPos blockPos) {
        return getDownfallFloat(level, getNowSolarTerm(level), biome, blockPos, !level.isClientSide());
    }

    public static float getDownfallFloat(Level level, SolarTerm solarTerm, Biome biome, BlockPos blockPos, boolean isServer) {
        return BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getDownfall(solarTerm);
    }

    public static float getDownfallFloatConstant(SolarTerm solarTerm, Biome biome, boolean isServer) {
        return BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getDownfall(solarTerm);
    }


    public static Rainfall getRainfallAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Rainfall.getRainfallLevel(getDownfallFloat(level, standBiome, pos));
    }

    public static Temperature getTemperatureAt(Level level, BlockPos pos) {
        Biome standBiome = level.getBiome(pos).value();
        return Temperature.getTemperatureLevel(getTemperatureFloat(level, standBiome, pos));
    }

    public static Humidity getHumidityAt(Level level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        SolarTerm solarTerm = getNowSolarTerm(level);
        boolean serverSide = !level.isClientSide();
        return getHumidityAt(level, solarTerm, biome, pos, serverSide);
    }

    public static Humidity getHumidityConstant(SolarTerm solarTerm, Holder<Biome> biomeHolder, boolean serverSide) {
        Biome standBiome = biomeHolder.value();
        float t = getTemperatureFloatConstant(solarTerm, standBiome, serverSide);
        BiomeRain biomeRain = solarTerm.getBiomeRain(biomeHolder);
        float r = (getDownfallFloatConstant(solarTerm, standBiome, serverSide) * 1.5f + biomeRain.getRainChane() * 0.5f) / 2f;
        return Humidity.getHumid(r, t);
    }

    public static Humidity getHumidityAt(Level level, SolarTerm solarTerm, Holder<Biome> biome, BlockPos pos, boolean serverSide) {
        Biome standBiome = biome.value();
        float t = getTemperatureFloat(level, solarTerm, standBiome, pos, serverSide);
        BiomeRain biomeRain = solarTerm.getBiomeRain(biome);
        float r = (getDownfallFloat(level, solarTerm, standBiome, pos, serverSide) * 1.5f + biomeRain.getRainChane() * 0.5f) / 2f;
        return Humidity.getHumid(r, t);
    }

}

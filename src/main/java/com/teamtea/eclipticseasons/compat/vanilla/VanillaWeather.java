package com.teamtea.eclipticseasons.compat.vanilla;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;


public class VanillaWeather {
    public static boolean isInWinter(Level level) {
        return EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason() == Season.WINTER;
    }

    public static boolean isInSummer(Level level) {
        return EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason() == Season.SUMMER;
    }

    public static void runVanillaSnowyWeather(ServerLevel level, WeatherManager.BiomeWeather biomeWeather, RandomSource random, int size) {
        boolean isRaining = level.isRaining();
        if ((isRaining || level.getRandom().nextInt(5) > 1)) {
            var snow = getSnowStatus(level, biomeWeather.biomeHolder, null);
            if (snow == WeatherManager.SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + 1);
            } else if (snow == WeatherManager.SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - 1);
            }
        }
    }


    public static WeatherManager.SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos) {
        var provider = SolarUtil.getProvider(level);
        var status = WeatherManager.SnowRenderStatus.NONE;
        if (biome.value().hasPrecipitation() && provider != null) {
            var solarTerm = provider.getSolarTerm();
            var snowTerm = SolarTerm.getSnowTerm(biome.value());
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (flag_cold) {
                if (level.isRaining())
                    status = WeatherManager.SnowRenderStatus.SNOW;
            } else {
                status = level.getRandom().nextBoolean() | level.isRaining() ?
                        WeatherManager.SnowRenderStatus.SNOW_MELT : WeatherManager.SnowRenderStatus.NONE;
            }
        }
        return status;
    }

    public static Biome.Precipitation replacePrecipitationIfNeed(Level level, Biome biome, Biome.Precipitation pre) {
        if (!ServerConfig.Weather.useSolarWeather.get()) {
            var solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (pre == Biome.Precipitation.RAIN) {
                if (flag_cold) {
                    pre = Biome.Precipitation.SNOW;
                }
            } else {
                if (!flag_cold) {
                    pre = Biome.Precipitation.RAIN;
                }
            }
        }
        return pre;
    }

    public static Biome.Precipitation handlePrecipitationat(Biome biome, BlockPos pos) {
        var level = getValidLevel(biome);

        return handlePrecipitationat(level,biome,pos);
    }

    public static Biome.Precipitation handlePrecipitationat(Level level,Biome biome, BlockPos pos) {
        var pre = Biome.Precipitation.NONE;
        if (biome.hasPrecipitation()) {
            pre = biome.coldEnoughToSnow(pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
            var solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (pre == Biome.Precipitation.RAIN) {
                if (flag_cold)
                // if (isInWinter(level))
                {
                    pre = Biome.Precipitation.SNOW;
                }
            } else {
                // if (isInSummer(level))
                if (!flag_cold) {
                    pre = Biome.Precipitation.RAIN;
                }
            }
        } else {
            // TODO: 稀疏草原，未来在做，需要考虑
            // if (isInSummer(level)) {
            //     pre = Biome.Precipitation.RAIN;
            // }
        }

        return pre;
    }

    public static Level getValidLevel(Biome biome) {
        boolean isOnServer = isOnServerThread(biome);
        if (isOnServer) {
            return WeatherManager.getMainServerLevel();
        } else return getUsingClientLevel();
    }

    public static boolean isOnServerThread(Biome biome) {
        if (FMLLoader.getDist() == Dist.DEDICATED_SERVER)
            return true;
        return BiomeClimateManager.BIOME_DEFAULT_TEMPERATURE_MAP.containsKey(biome);
    }

    public static Level getUsingClientLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (!(level instanceof ServerLevel)) {
                return level;
            }
        }
        return null;
    }
    private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);

    public static int replaceThunderDelay(Level level, Integer call) {
        switch (EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason()) {
            case SPRING -> {
                return Mth.clamp(call - 10000, 0,THUNDER_DELAY.getMaxValue());
            }
            case SUMMER -> {
                return Mth.clamp(call - 20000, 0, THUNDER_DELAY.getMaxValue());
            }
            case AUTUMN -> {
                return Mth.clamp(call + 20000, 0, THUNDER_DELAY.getMaxValue() + 20000);
            }
            case WINTER -> {
                return Mth.clamp(call + 50000, 0, THUNDER_DELAY.getMaxValue() + 50000);
            }
            default -> {
                return call;
            }
        }
    }

    public static int replaceRainDelay(Level level, Integer call) {
        switch (EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason()) {
            case SPRING -> {
                return Mth.clamp(call - 20000, 0, ServerLevel.RAIN_DELAY.getMaxValue());
            }
            case SUMMER -> {
                return Mth.clamp(call - 10000, 0, ServerLevel.RAIN_DELAY.getMaxValue());
            }
            case AUTUMN -> {
                return Mth.clamp(call + 5000, 0, ServerLevel.RAIN_DELAY.getMaxValue());
            }
            case WINTER -> {
                return Mth.clamp(call + 20000, 0, ServerLevel.RAIN_DELAY.getMaxValue() + 20000);
            }
            default -> {
                return call;
            }
        }
    }

}

package com.teamtea.eclipticseasons.compat.vanilla;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

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
        var status = WeatherManager.SnowRenderStatus.NONE;
        if (biome.value().hasPrecipitation()) {
            boolean flag_cold = isInWinter(level);
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

    public static Biome.Precipitation handlePrecipitationAt(Biome biome, BlockPos pos) {
        var level = getValidLevel(biome);
        return handlePrecipitationAt(level, biome, pos);
    }

    @Deprecated
    public static boolean hasMonsoonalPrecipitation(Biome biome) {
        var level = getValidLevel(biome);
        return hasPrecipitation(level, biome);
    }

    public static boolean hasPrecipitation(Level level, Biome biome) {
        var solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        boolean hasPrecipitation = biome.getModifiedClimateSettings().hasPrecipitation();
        TagKey<Biome> tag = ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindTag();
        if (tag.equals(ClimateTypeBiomeTags.MONSOONAL)) {
            Season season = solarTerm.getSeason();
            if (season == Season.SUMMER || season == Season.AUTUMN) {
                hasPrecipitation = true;
            } else {
                hasPrecipitation = false;
            }
        }
        return hasPrecipitation;
    }

    public static Biome.Precipitation handlePrecipitationAt(Level level, Biome biome, BlockPos pos) {
        var resultPrecipitation = Biome.Precipitation.NONE;
        var solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);

        if (MapChecker.isLoadNearByOnlyServer(level, pos)) {
            biome = MapChecker.getSurfaceBiome(level, pos).value();
        }
        // else {
        //     return biome.coldEnoughToSnow(pos) ?
        //             Biome.Precipitation.SNOW :
        //             Biome.Precipitation.RAIN;
        // }

        boolean hasPrecipitation = hasPrecipitation(level, biome);

        if (hasPrecipitation) {
            resultPrecipitation = biome.coldEnoughToSnow(pos) ?
                    Biome.Precipitation.SNOW :
                    Biome.Precipitation.RAIN;

            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (resultPrecipitation == Biome.Precipitation.RAIN) {
                if (flag_cold) {
                    resultPrecipitation = Biome.Precipitation.SNOW;
                }
            } else {
                if (!flag_cold) {
                    resultPrecipitation = Biome.Precipitation.RAIN;
                }
            }
        }


        return resultPrecipitation;
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
        return BiomeClimateManager.BIOME_TAG_KEY_MAP.containsKey(biome);
    }

    public static Level getUsingClientLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.isClientSide()) {
                return level;
            }
        }
        return null;
    }


    public static int replaceThunderDelay(Level level, Integer call) {
        switch (EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason()) {
            case SPRING -> {
                return Mth.clamp(call - 10000, 0, ServerLevel.THUNDER_DELAY.getMaxValue());
            }
            case SUMMER -> {
                return Mth.clamp(call - 20000, 0, ServerLevel.THUNDER_DELAY.getMaxValue());
            }
            case AUTUMN -> {
                return Mth.clamp(call + 20000, 0, ServerLevel.THUNDER_DELAY.getMaxValue() + 20000);
            }
            case WINTER -> {
                return Mth.clamp(call + 50000, 0, ServerLevel.THUNDER_DELAY.getMaxValue() + 50000);
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

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        return !level.isRaining() ? Biome.Precipitation.NONE :
                handlePrecipitationAt(level, biome, pos);
    }

    // @Deprecated(forRemoval = true)
    // public static boolean canRunSpecialWeather() {
    //     return EclipticUtil.useSolarWeather();
    // }
}

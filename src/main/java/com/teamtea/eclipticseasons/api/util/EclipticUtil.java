package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;

import java.util.Optional;

public class EclipticUtil {

    public static int getDayLengthInMinecraftStatic() {
        // Timelines
        return 24000;
    }

    public static int getDayLengthInMinecraft(Level level) {
        if (true)
            return level
                    .registryAccess()
                    .get(Timelines.OVERWORLD_DAY)
                    .map(Holder::value)
                    .flatMap(Timeline::periodTicks)
                    .orElse(getDayLengthInMinecraftStatic());
        Optional<Holder<WorldClock>> worldClockHolder = level.dimensionType().defaultClock();
        if (worldClockHolder.isEmpty()) return getDayLengthInMinecraftStatic();
        var timelines = level.dimensionType().timelines()
                .stream().filter((t) -> t.value().clock() == worldClockHolder.get()).findFirst();
        if (timelines.isEmpty()) return getDayLengthInMinecraftStatic();
        Holder<Timeline> timelineHolder = timelines.get();
        return timelineHolder.value().periodTicks().orElse(getDayLengthInMinecraftStatic());
    }

    public static SolarTerm getNowSolarTerm(Level level) {
        var sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTerm();
        return SolarTerm.NONE;
    }

    public static int getNowSolarDay(Level level) {
        var sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTermsDay();
        return 0;
    }

    public static int getNowSolarYear(Level level) {
        var sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarYear();
        return 0;
    }

    public static int getTimeInSolarTerm(Level level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getSolarTermDaysInPeriod();
        return 0;
    }

    public static int getBiomeDataVersion(Level level) {
        SolarDataManager sd = SolarHolders.getSaveData(level);
        if (sd != null) return sd.getBiomeDataVersion();
        return 0;
    }

    public static float getSnowTempChange(Level level) {
        if (CommonConfig.Season.dynamicSnowTerm.get()) {
            SolarDataManager sd = SolarHolders.getSaveData(level);
            if (sd != null) return sd.getSolarTempChange();
        }
        return 0f;
    }

    public static boolean isDay(Level level) {
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
        return timePeriod.ordinal() < TimePeriod.NIGHT.ordinal();
    }

    public static boolean isNight(Level level) {
        return !isDay(level);
    }

    public static int getNightTime(Level level) {
        long termTime = getNowSolarTerm(level).getDayTime();
        return (int) (6000 + (termTime / 2));
    }

    public static boolean isNoon(Level level) {
        // long dayTime = level.dimensionType().fixedTime().orElse(SolarAngelHelper.getSolarAngelTime(level, level.getDayTime()));
        long dayTime = level.getDefaultClockTime();
        long termTime = getNowSolarTerm(level).getDayTime();
        return 6000 - (termTime / 6) < dayTime && dayTime < 6000 + (termTime / 4);
    }

    public static boolean isEvening(Level level) {
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
        return timePeriod == TimePeriod.DUSK || timePeriod == TimePeriod.NIGHT;
    }

    public static boolean useSolarWeather() {
        return CommonConfig.isUseSolarWeather();
    }

    public static boolean isSolarWeatherClosed() {
        return !EclipticUtil.useSolarWeather();
    }

    public static WeatherMode getWeatherMode(Level level) {
        if (!useSolarWeather()) return WeatherMode.DEFAULT;
        return MapChecker.isValidDimension(level) ?
                WeatherMode.REGION
                //(CommonConfig.isEnableWeatherRegion() ? WeatherMode.REGION : WeatherMode.BIOME)
                : WeatherMode.DEFAULT;
    }

    public static boolean hasLocalWeather(Level level) {
        return getWeatherMode(level) != WeatherMode.DEFAULT;
    }

    public static boolean canSnowyBlockInteract() {
        return CommonConfig.isSnowInWorld();
    }

    public static EclipticSeasonsApi INSTANCE;

    static {
        INSTANCE = new EclipticSeasonsApi() {
            @Override
            public SolarTerm getSolarTerm(Level level) {
                return EclipticUtil.getNowSolarTerm(level);
            }

            @Override
            public Season getAgroSeason(Level level, BlockPos pos) {
                return AnimalHooks.getUseSeason(level, pos);
            }

            @Override
            public int getSolarDays(Level level) {
                return EclipticUtil.getNowSolarDay(level);
            }

            @Override
            public int getSolarYears(Level level) {
                return EclipticUtil.getNowSolarYear(level);
            }

            @Override
            public int getLastingDaysOfEachTerm(Level level) {
                return CommonConfig.Season.lastingDaysOfEachTerm.get();
            }

            @Override
            public int getTimeInTerm(Level level) {
                return EclipticUtil.getTimeInSolarTerm(level);
            }

            @Override
            public boolean isSeasonEnabled(Level level) {
                return MapChecker.isValidDimension(level);
            }

            @Override
            public boolean hasLocalWeather(Level level) {
                return EclipticUtil.hasLocalWeather(level);
            }

            @Override
            public long getDayTime(Level level) {
                if (MapChecker.isValidDimension(level)
                        && CommonConfig.Season.daylightChange.get()
                        && SolarHolders.getSaveData(level) instanceof SolarDataManager data) {
                    // long worldTime = level.getDayTime();
                    long worldTime = level.getDefaultClockTime();
                    int dayLevelTime = Math.toIntExact((worldTime + 18000) % EclipticUtil.getDayLengthInMinecraft(level)); // 0 for noon; 6000 for sunset; 18000 for sunrise.
                    return dayLevelTime > 12000 && dayLevelTime <= 18000 && data.isTodayLastDay() ?
                            data.getNextSolarTerm().getDayTime() :
                            data.getSolarTerm().getDayTime();
                }
                return 12000L;
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
                if (CommonConfig.isSnowyWinter()) {
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
                if (hasLocalWeather(level)) {
                    if (getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.NONE) {
                        return false;
                    }
                } else {
                    if (!level.isRaining()) {
                        return false;
                    }
                }
                if (!level.canSeeSky(pos)) {
                    return false;
                } else return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();
            }

            @Override
            public boolean isRainAt(Level level, BlockPos pos) {
                return level.isRainingAt(pos);
            }

            @Override
            public boolean isSnowAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level)) {
                    if (getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.NONE) {
                        return false;
                    }
                } else {
                    if (!level.isRaining() || VanillaWeather.handlePrecipitationAt(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) != Biome.Precipitation.SNOW) {
                        return false;
                    }
                }
                if (!level.canSeeSky(pos)) {
                    return false;
                } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                    return false;
                }
                return true;
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


            @Override
            public Biome.Precipitation getCurrentPrecipitationAt(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return EclipticUtil.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos);
                return VanillaWeather.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos);
            }

            @Override
            public boolean isRainingOrSnowing(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) != Biome.Precipitation.NONE;
                return level.isRaining();
            }

            // @Override
            // public boolean isRaining(Level level, BlockPos pos) {
            //     if (hasLocalWeather(level))
            //         return WeatherManager.isRainingAtBiome(level, MapChecker.getSurfaceBiome(level, pos));
            //     return level.isRaining();
            // }
            //
            // @Override
            // public boolean isSnowing(Level level, BlockPos pos) {
            //     if (hasLocalWeather(level))
            //         return WeatherManager.isSnowingAtBiome(level, MapChecker.getSurfaceBiome(level, pos));
            //     return level.isRaining();
            // }

            @Override
            public boolean isThundering(Level level, BlockPos pos) {
                if (hasLocalWeather(level))
                    return WeatherManager.isThunderAtBiome(level, MapChecker.getSurfaceBiome(level, pos));
                return level.isRaining();
            }

            @Override
            public Humidity getBaseHumidity(Level level, BlockPos pos) {
                return getHumidityAt(level, pos);
            }

            @Override
            public Humidity getAdjustedHumidity(ServerLevel level, BlockPos checkPos) {
                float humidity = getHumidityLevelAt(level, checkPos);
                humidity = getHumidityAfterCheck(level, checkPos, humidity);
                return Humidity.getHumid(humidity);
            }

        };
    }

    public static float getHumidityAfterCheck(ServerLevel level, BlockPos checkPos, float env) {
        SolarDataManager data = SolarHolders.getSaveData(level);
        if (data != null) {
            float chance = 0;
            for (int i = 0; i < 20; i++) {
                chance += CropGrowthHandler.isInRoom(level, checkPos, level.getBlockState(checkPos), Optional.empty()) ? 1 : 0;
            }
            if (chance > 8) {
                env += ((data.calculateHumidityModification(checkPos, false)));
            } else if (chance == 0) {
                if (level.isRainingAt(checkPos)) {
                    env += (1);
                }
            }
        }
        return env;
    }

    public static boolean isRainingOrSnowingWithSurfaceBiome(Level level, Holder<Biome> biome, BlockPos pos) {
        if (hasLocalWeather(level))
            return getRainOrSnow(level, biome.value(), pos) != Biome.Precipitation.NONE;
        return level.isRaining();
    }

    public static boolean isHereWithSnow(Level level, BlockPos pos) {
        return WeatherManager.getSnowDepthAtBiome(level, MapChecker.getSurfaceBiome(level, pos).value()) > 0;
    }

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        return level.isClientSide() && ClientWeatherChecker.isBiomeRainyLast(biome) ?
                WeatherManager.getPrecipitationAt(level, biome, pos) :
                WeatherManager.getRainOrSnow(level, biome, pos);
    }

    public static boolean isHereSunny(Level level, BlockPos pos) {
        return getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.NONE;
    }

    public static boolean isHereRainy(Level level, BlockPos pos) {
        return getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isHereSnowy(Level level, BlockPos pos) {
        return getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) == Biome.Precipitation.SNOW;
    }

    public static float getTemperatureFloat(Level level, Biome biome, BlockPos blockPos) {
        return getTemperatureFloat(level, getNowSolarTerm(level), biome, blockPos, !level.isClientSide());
    }

    public static float getTemperatureFloat(Level level, SolarTerm solarTerm, Biome biome, BlockPos blockPos,
                                            boolean isServer) {
        float modify = solarTerm.getSeason() == Season.SUMMER
                && isNoon(level)
                && level.getBrightness(LightLayer.SKY, blockPos.above()) > 12 ?
                solarTerm.getTemperatureChange() / 2 : 0;
        return biome.getTemperature(blockPos, level.getSeaLevel()) +
                BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getTemperatureChange(solarTerm) + modify;
    }

    public static float getTemperatureFloatConstant(SolarTerm solarTerm, Biome biome, boolean isServer) {
        return BiomeClimateManager.getBiomeClimateSettings(biome, isServer).getTemperature(solarTerm);
    }

    public static float getDownfallFloat(Level level, Biome biome, BlockPos blockPos) {
        return getDownfallFloat(level, getNowSolarTerm(level), biome, blockPos, !level.isClientSide());
    }

    public static float getDownfallFloat(Level level, SolarTerm solarTerm, Biome biome, BlockPos blockPos,
                                         boolean isServer) {
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

    // public static Humidity getHumidityWithModify(Level level, BlockPos pos) {
    //     Holder<Biome> biome = level.getBiome(pos);
    //     SolarTerm solarTerm = getNowSolarTerm(level);
    //     boolean serverSide = !level.isClientSide();
    //     Humidity humidityAt = getHumidityAt(level, solarTerm, biome, pos, serverSide);
    //     SolarDataManager saveData = SolarHolders.getSaveData(level);
    //     if(saveData!=null){
    //         float v = saveData.calculateHumidityModification(pos);
    //     }
    //     return humidityAt;
    // }

    public static Humidity getHumidityAt(Level level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        SolarTerm solarTerm = getNowSolarTerm(level);
        boolean serverSide = !level.isClientSide();
        return getHumidityAt(level, solarTerm, biome, pos, serverSide);
    }

    public static Humidity getHumidityConstant(SolarTerm solarTerm, Holder<Biome> biomeHolder, boolean serverSide) {
        Biome standBiome = biomeHolder.value();
        float t = getTemperatureFloatConstant(solarTerm, standBiome, serverSide);
        BiomeRain biomeRain = SolarUtil.getBiomeRain(solarTerm, biomeHolder);
        float r = (getDownfallFloatConstant(solarTerm, standBiome, serverSide) * 1.5f + biomeRain.getRainChance() * 0.5f) / 2f;
        return Humidity.getHumid(r, t);
    }

    public static float getHumidityConstantFloat(SolarTerm solarTerm, Holder<Biome> biomeHolder, boolean serverSide) {
        Biome standBiome = biomeHolder.value();
        float t = getTemperatureFloatConstant(solarTerm, standBiome, serverSide);
        BiomeRain biomeRain = SolarUtil.getBiomeRain(solarTerm, biomeHolder);
        float r = (getDownfallFloatConstant(solarTerm, standBiome, serverSide) * 1.5f + biomeRain.getRainChance() * 0.5f) / 2f;
        return Humidity.getFloatHumidLevel(r, t);
    }

    public static Humidity getHumidityAt(Level level, SolarTerm solarTerm, Holder<Biome> biome, BlockPos pos, boolean serverSide) {
        return Humidity.getHumid(getHumidityLevelAt(level, solarTerm, biome, pos, serverSide));
    }

    public static float getHumidityLevelAt(Level level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        SolarTerm solarTerm = getNowSolarTerm(level);
        boolean serverSide = !level.isClientSide();
        return getHumidityLevelAt(level, solarTerm, biome, pos, serverSide);
    }

    public static float getHumidityLevelAt(Level level, SolarTerm solarTerm, Holder<Biome> biome, BlockPos pos, boolean serverSide) {
        Biome standBiome = biome.value();
        float t = getTemperatureFloat(level, solarTerm, standBiome, pos, serverSide);
        BiomeRain biomeRain = SolarUtil.getBiomeRain(solarTerm, biome);
        float r = (getDownfallFloat(level, solarTerm, standBiome, pos, serverSide) * 1.5f + biomeRain.getRainChance() * 0.5f) / 2f;
        return Humidity.getFloatHumidLevel(r, t);
    }

    public static boolean maySnow(Level level, BlockPos pos) {
        Biome biome = MapChecker.getSurfaceBiome(level, pos).value();
        boolean server = level instanceof ServerLevel;
        ISnowTerm snowTerm = SolarUtil.getSnowTerm(biome, server, EclipticUtil.getSnowTempChange(level));
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        return snowTerm.maySnow(solarTerm, biome, pos, server);
    }
}

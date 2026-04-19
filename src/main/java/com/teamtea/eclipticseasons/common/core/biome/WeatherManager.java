package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.event.BeforeCheckSnowStatusEvent;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import com.teamtea.eclipticseasons.common.network.message.UpdateTempChangeMessage;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.message.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import lombok.Setter;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.*;

public class WeatherManager {

    public static final Map<Level, ArrayList<BiomeWeather>> BIOME_WEATHER_LIST = new IdentityHashMap<>();
    public static final Map<Level, Integer> NEXT_CHECK_BIOME_MAP = new IdentityHashMap<>();
    public static final Map<Level, Map<Biome, BiomeWeather>> BIOME_WEATHER_QUERY_LIST = new IdentityHashMap<>();

    public static ArrayList<BiomeWeather> getBiomeList(Level level) {
        if (level == null) {
            for (ArrayList<BiomeWeather> value : BIOME_WEATHER_LIST.values()) {
                return value;
            }
            return null;
        }
        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            return iBiomeWeatherProvider.es$get();
        }
        return BIOME_WEATHER_LIST.getOrDefault(level, null);
    }

    public static int getWeatherTickFactor(Level level) {
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        int size = biomeList == null ? 1 : biomeList.size();
        size = (int) (size * (Mth.clamp(7f / EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(level), 0.8f, 3f)));
        size = Math.max(1, size);
        return size;
    }

    public static Level fetchLevelIfNull(Level level, Biome biome) {
        level = level != null || !BiomeClimateManager.CLIENT_BIOME_TAG_KEY_MAP.containsKey(biome)
                ? level : ClientCon.getUseLevel();
        return level != null ? level : getMainServerLevel();
    }

    @Nullable
    public static BiomeWeather getBiomeWeather(Level level, Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        return getBiomeWeather(level, biomeHolder.value());
    }

    @Nullable
    public static BiomeWeather getBiomeWeather(Level level, Biome biome) {
        BiomeWeather weather = null;
        // var weatherQueryListOrDefault = BIOME_WEATHER_QUERY_LIST.getOrDefault(level, null);
        // if (weatherQueryListOrDefault != null) {
        //     weather = weatherQueryListOrDefault.getOrDefault(biome, null);
        // }
        // var weatherQueryListOrDefault = BIOME_WEATHER_LIST.getOrDefault(level, null);
        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            var weatherQueryListOrDefault = iBiomeWeatherProvider.es$get();
            if (weatherQueryListOrDefault != null) {
                Object object = biome;
                if (object instanceof IBiomeTagHolder iBiomeTagHolder) {
                    int id = iBiomeTagHolder.eclipticseasons$getBindId();
                    if (weatherQueryListOrDefault.size() > id && id > -1)
                        weather = weatherQueryListOrDefault.get(id);
                }
            }
        }
        return weather;
    }

    public static void onSetWeatherParameters(ServerLevel level, int pClearTime, int pWeatherTime, boolean pIsRaining, boolean pIsThundering) {
        int weatherTickFactor = getWeatherTickFactor(level);
        ((ServerLevelData) (level.getLevelData())).setClearWeatherTime(pClearTime / weatherTickFactor);
        ((ServerLevelData) (level.getLevelData())).setRainTime(pWeatherTime / weatherTickFactor);
        ((ServerLevelData) (level.getLevelData())).setThunderTime(pWeatherTime / weatherTickFactor);
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        if (biomeList != null) {
            for (BiomeWeather biomeWeather : biomeList) {
                setBiomeWeather(level, biomeWeather, pClearTime);
            }
        }
    }

    public static void setBiomeWeather(ServerLevel level, BiomeWeather biomeWeather, int rainTime) {
        biomeWeather.lastRainTime = rainTime > 0 ? level.getGameTime() : biomeWeather.lastRainTime;
    }

    public static boolean isThunderAtBiome(Level level, BlockPos pos) {
        Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, surfaceBiome);
    }

    public static boolean isThunderAtBiome(Level level, Holder<Biome> biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return level.isThundering();
        }
        return false;
    }

    public static boolean isThunderAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, biome);
    }

    public static boolean isRainingUnderSky(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return getRainOrSnow(level, biome.value(), pos) == Biome.Precipitation.RAIN;
    }


    public static boolean isRainingAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return getRainOrSnow(level, biome.value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isRainingOrSnowAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return isRainingOrSnowAtBiome(level, biome);
    }

    public static boolean isRainingOrSnowAtBiome(Level level, Holder<Biome> biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return level.isRaining();
        }
        return false;
    }

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        if (hasNonePrecipitation(biome)) return Biome.Precipitation.NONE;

        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            if (!level.isRaining()) return Biome.Precipitation.NONE;

            // check attach
            // SnowyRemover snowyRemover = level.getChunk(pos).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
            // if (snowyRemover != null) {
            //     SnowyRemover.SnowyFlag snowyFlag = snowyRemover.getSnowyFlag(pos);
            //     if (snowyFlag == SnowyRemover.SnowyFlag.NONE_SNOWY)
            //         return Biome.Precipitation.RAIN;
            //     else if (snowyFlag == SnowyRemover.SnowyFlag.SNOWY_ALWAYS)
            //         return Biome.Precipitation.SNOW;
            // }

            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm, biome, pos, level instanceof ServerLevel);
            Biome.Precipitation precipitation = flag_cold
                    // || BiomeClimateManager.getDefaultTemperature(biome, levelNull instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL
                    ?
                    Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
            if (biomeWeather.effect != null && biomeWeather.effect.value().shouldChangePrecipitation(level, biome, pos, false, precipitation))
                precipitation = biomeWeather.effect.value().getModifiedPrecipitation(level, biome, pos, false, precipitation);
            return precipitation;
        }

        return Biome.Precipitation.NONE;
    }


    public static int getSnowDepthAtBiome(Level level, Biome biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.getSnowDepth();
        }
        return 0;
    }

    public static long getLastRainTimeAtBiome(Level level, Biome biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.lastRainTime;
        }
        return 0;
    }

    public static ServerLevel getMainServerLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD
                    && !level.isClientSide()
                    && level instanceof ServerLevel serverLevel) {
                return serverLevel;
            }
        }
        return null;
    }


    public static Biome.Precipitation getPrecipitationAt(Biome biome, BlockPos pos) {
        var level = fetchLevelIfNull(null, biome);
        if (level != null && CompatModule.CommonConfig.fixBiome.get() && MapChecker.isSmallBiome(biome)) {
            // if (MapChecker.isLoadNearByOnlyServer(level, pos))
            {
                biome = MapChecker.getSurfaceBiome(level, pos).value();
            }
        }
        return getPrecipitationAt(level, biome, pos);
    }

    public static Biome.Precipitation getPrecipitationAt(@Nullable Level levelNull, Biome biome, BlockPos pos) {

        // check if it has predication
        if (hasNonePrecipitation(biome)) return Biome.Precipitation.NONE;

        var level = fetchLevelIfNull(levelNull, biome);

        // var provider = SolarHolders.getSaveData(level);
        // var weathers = getBiomeList(level);

        // Not add 'has' check because we have checked it
        if (level != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, levelNull instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm, biome, pos, level instanceof ServerLevel);
            // var biomes = level.registryAccess().registry(Registries.BIOME).get();
            // var loc = biomes.getKey(biome);
            BiomeWeather biomeWeather = getBiomeWeather(level, biome);
            if (biomeWeather != null) {
                Biome.Precipitation precipitation = flag_cold
                        // || BiomeClimateManager.getDefaultTemperature(biome, levelNull instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL
                        ?
                        Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
                if (biomeWeather.effect != null && biomeWeather.effect.value().shouldChangePrecipitation(level, biome, pos, true, precipitation))
                    precipitation = biomeWeather.effect.value().getModifiedPrecipitation(level, biome, pos, true, precipitation);
                // if (biomeWeather.shouldClear())
                //     return Biome.Precipitation.NONE;
                return precipitation;
            }

        }

        return Biome.Precipitation.NONE;
    }

    public static boolean hasNonePrecipitation(Biome biome) {
        if (!biome.hasPrecipitation()) {
            return true;
        }

        return CommonConfig.Weather.notRainInDesert.get()
                && !biome.getModifiedClimateSettings().hasPrecipitation()
                && BiomeClimateManager.getTag(biome) != ClimateTypeBiomeTags.MONSOONAL;
    }

    public static void createLevelBiomeWeatherList(Level level) {
        var biomes = level.registryAccess().registry(Registries.BIOME);
        if (biomes.isEmpty())
            throw new IllegalStateException("[%s] Minecraft cannot work without Biome registry!".formatted(level.dimension()));
        Registry<Biome> biomes1 = biomes.get();
        var biomesWeathers = new ArrayList<BiomeWeather>(biomes1.size());
        var biomesWeathersArray = new BiomeWeather[biomes1.size()];
        for (Biome biome : biomes1) {
            // biomes.get().holders().toList().getFirst().getDelegate()
            ResourceLocation loc = biomes1.getKey(biome);
            int id = biomes1.getId(biome);
            Optional<Holder.Reference<Biome>> biomesHolder = biomes1.getHolder(loc);
            if (biomesHolder.isPresent()) {
                var biomeWeather = new BiomeWeather(biomesHolder.get());
                biomeWeather.location = loc;
                biomeWeather.id = id;
                // biomesWeathers.set(id, biomeWeather);
                biomesWeathersArray[id] = biomeWeather;
                ((IBiomeTagHolder) (Object) biome).eclipticseasons$setBindId(id);
            }
        }
        biomesWeathers = new ArrayList<>(Arrays.stream(biomesWeathersArray).toList());
        WeatherManager.BIOME_WEATHER_LIST.put(level, biomesWeathers);

        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            iBiomeWeatherProvider.es$set(biomesWeathers);
        }

        // add copy
        Map<Biome, BiomeWeather> biomeBiomeWeatherMap = new IdentityHashMap<>();
        for (BiomeWeather biomesWeather : biomesWeathers) {
            biomeBiomeWeatherMap.put(biomesWeather.biomeHolder.value(), biomesWeather);
        }
        WeatherManager.BIOME_WEATHER_QUERY_LIST.put(level, biomeBiomeWeatherMap);
    }

    public static void informUpdateBiomes(RegistryAccess registryAccess, boolean isServer) {

        WeatherManager.BIOME_WEATHER_LIST.forEach((key, biomeWeathers) -> {
            if ((key instanceof ServerLevel) == isServer) {
                registryAccess.registry(Registries.BIOME)
                        .ifPresent(biomeRegistry -> biomeRegistry
                                .holders().forEach(biomeHolder ->
                                {
                                    ResourceLocation loc = biomeHolder.key().location();
                                    var id = biomeRegistry.getId(biomeHolder.value());
                                    boolean inList = false;
                                    for (BiomeWeather biomeWeather : biomeWeathers) {
                                        if (biomeWeather.biomeHolder.is(loc)) {
                                            biomeWeather.id = id;
                                            biomeWeather.biomeHolder = biomeHolder;
                                            inList = true;
                                            break;
                                        }
                                    }
                                    if (!inList) {
                                        var biomeWeather = new BiomeWeather(biomeHolder);
                                        biomeWeather.location = loc;
                                        biomeWeather.id = id;
                                        biomeWeathers.add(biomeWeather);
                                    }
                                }));
            }
        });

        WeatherManager.BIOME_WEATHER_LIST.forEach((key, value) -> value.sort(Comparator.comparing(c -> c.id)));
    }

    public static void tickPlayerSeasonEffect(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator() ||
                !CommonConfig.Temperature.heatStroke.get()) return;
        Level level = player.level();
        if (MapChecker.isValidDimension(level)
                && level.getRandom().nextInt(150) == 0) {
            var capability = player.getData(AttachmentRegistry.HEAT_STROKE_TICKER);
            capability.tickPlayer(player, level);
        }
    }

    public static void runWeather(ServerLevel level, BiomeWeather biomeWeather, RandomSource random, int size) {
        if (!level.dimensionType().hasSkyLight() || !MapChecker.isValidDimension(level)) return;

        Holder<Biome> onwer = getOwner(level, biomeWeather.biomeHolder);
        if (onwer != null && !onwer.equals(biomeWeather.biomeHolder)) {
            BiomeWeather ownerBiomeWeather = getBiomeWeather(level, onwer);
            if (ownerBiomeWeather != null) {
                biomeWeather.effect = ownerBiomeWeather.effect;
                biomeWeather.setBiomeRain(ownerBiomeWeather.getBiomeRain());
                updateSnowOrMelt(level, biomeWeather, random, size, level.isRaining());
                return;
            }
        }

        if (biomeWeather.getBiomeRain() == FlatRain.NONE) {
            biomeWeather.setBiomeRain(getBiomeRain(level, EclipticUtil.getNowSolarTerm(level), biomeWeather.biomeHolder));
        }
        if (hasNonePrecipitation(biomeWeather.biomeHolder.value()))
            return;

        ServerLevelData weatherData = ((ServerLevelData) level.getLevelData());

        SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
        BiomeRain biomeRain = getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);

        int clearTime = weatherData.getClearWeatherTime();
        int rainTime = weatherData.getRainTime();
        int thunderTime = weatherData.getThunderTime();

        boolean raining = weatherData.isRaining();
        boolean thundering = weatherData.isThundering();

        if (clearTime > 0) {
            clearTime--;
        } else {
            if (rainTime > 0) {
                rainTime--;
                if (thunderTime <= 0) {
                    float weight = biomeRain.getThunderChance()
                            * ((CommonConfig.Weather.thunderChanceMultiplier.get() * 1f) / 100f)
                            * size / 3000f;
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                        thunderTime = biomeRain.getThunderDuration(random) / size;
                    }

                }
            } else {
                float downfall = EclipticUtil.getDownfallFloatConstant(solarTerm, biomeWeather.biomeHolder.value(), !level.isClientSide());
                float weight = biomeRain.getRainChance()
                        * Math.max(0.01f, downfall)
                        * ((CommonConfig.Weather.rainChanceMultiplier.get() * 1f) / 100f);
                if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                    rainTime = biomeRain.getRainDuration(random) / size;
                } else {
                    clearTime = biomeRain.getRainDelay(random) / size;
                }

            }
        }

        if (thunderTime > 0) {
            thunderTime--;
            if (rainTime <= 0) {
                thunderTime = 0;
            }
        }

        thundering = thunderTime > 0;
        raining = clearTime == 0 && rainTime > 0;


        weatherData.setClearWeatherTime(clearTime);
        weatherData.setRainTime(rainTime);
        weatherData.setThunderTime(thunderTime);
        weatherData.setRaining(raining);
        weatherData.setThundering(thundering);

        biomeWeather.setBiomeRain(biomeRain);

        if (weatherData.isRaining()) {
            biomeWeather.effect = biomeWeather.biomeRain.hasSpecialEffect() ?
                    biomeWeather.biomeRain.getSpecialEffect() : null;
        } else {
            biomeWeather.effect = null;
        }

        updateSnowOrMelt(level, biomeWeather, random, size, level.isRaining());
    }

    public static @Nullable Holder<Biome> getOwner(Level level, Holder<Biome> biomeHolder) {
        return level.registryAccess().holderOrThrow(Biomes.PLAINS);
        // return
        //         // level instanceof IBiomeWeatherProvider ibwp && ibwp.es$getCoreBiome() != null ?
        //         // ibwp.es$getCoreBiome() :
        //         BiomeClimateManager.getWeatherRegionOnwer(biomeHolder.value());
    }

    protected static void updateSnowOrMelt(ServerLevel level, BiomeWeather biomeWeather, RandomSource randomSource, int size, boolean rain) {
        if (rain) biomeWeather.lastRainTime = level.getGameTime();
        if ((rain || randomSource.nextInt(5) > 1)) {
            var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder, BlockPos.ZERO, rain);
            float rate = Math.max(1, size >> 6);
            if (snow == SnowRenderStatus.SNOW) {
                rate *= (float) (biomeWeather.getBiomeRain().getSnowAccumulationSpeed() * CommonConfig.Weather.snowAccumulationSpeedMultiplier.get());
                biomeWeather.setSnowDepth(Math.min(100, biomeWeather.snowDepth + rate));
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                rate *= (float) (biomeWeather.getBiomeRain().getSnowMeltSpeed() * CommonConfig.Weather.snowMeltSpeedMultiplier.get());
                biomeWeather.setSnowDepth(Math.max(0, biomeWeather.snowDepth - rate));
            }
        }
    }

    public static BiomeRain getBiomeRain(ServerLevel level, SolarTerm solarTerm, Holder<Biome> biomeWeather) {
        return getBiomeRain(solarTerm, biomeWeather).resolve(level);
    }

    public static BiomeRain getBiomeRain(SolarTerm solarTerm, Holder<Biome> biomeWeather) {
        return solarTerm.getBiomeRain(biomeWeather);
    }

    public static void initNewWorldWeather(ServerLevel level, RandomSource random, SolarTerm solarTerm) {
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        if (biomeList == null || level.isClientSide() || !MapChecker.isValidDimension(level)) return;

        if (CommonConfig.Weather.shouldInitSnowForExtremeColdBiomes.get()) {
            for (BiomeWeather biomeWeather : biomeList) {
                if (biomeWeather.biomeHolder == null
                        || !biomeWeather.biomeHolder.is(ClimateTypeBiomeTags.EXTREME_COLD))
                    continue;
                biomeWeather.setSnowDepth(100);
            }
        }

        if (!CommonConfig.Weather.shouldInitWeather.get()) {
            return;
        }

        int size = getWeatherTickFactor(level);
        SolarTerm lastSolarTerm =
                solarTerm == SolarTerm.NONE ? SolarTerm.NONE :
                        SolarTerm.collectValues()[(solarTerm.ordinal() - 1 + 24) % 24];
        for (BiomeWeather biomeWeather : biomeList) {
            if (hasNonePrecipitation(biomeWeather.biomeHolder.value()))
                continue;

            var snowTerm = SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), !level.isClientSide(), EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            boolean flag_little_cold = snowTerm.maySnow(lastSolarTerm);
            SnowRenderStatus snow = flag_cold ? SnowRenderStatus.SNOW :
                    flag_little_cold ? SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.setSnowDepth(100);
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.setSnowDepth((byte) random.nextInt(50));
            } else biomeWeather.setSnowDepth(0);
        }
    }

    public static void updateAfterSleep(ServerLevel level, long newTime, long oldDayTime) {
        if (newTime > oldDayTime) {
            var ws = WeatherManager.getBiomeList(level);
            if (ws != null) {
                var random = level.getRandom();
                int size = getWeatherTickFactor(level);
                for (BiomeWeather biomeWeather : ws) {
                    for (int i = 0; i < (newTime - oldDayTime) / size; i++) {
                        WeatherManager.runWeather(level, biomeWeather, random, size);
                    }
                }

                if (CommonConfig.Weather.clearAfterSleep.get()) {
                    SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
                    for (BiomeWeather biomeWeather : ws) {
                        BiomeRain biomeRain = getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);
                        biomeWeather.setBiomeRain(biomeRain);
                    }
                }

                if (!level.players().isEmpty()) {
                    WeatherManager.sendBiomePacket(level, ws, level.players());
                }

                SnowyMapChecker.updateAllChunks(level);
            }
        }
        SimpleNetworkHandler.send(new ArrayList<>(level.players()), new EmptyMessage(true));
    }

    public static void onLoggedIn(ServerPlayer serverPlayer, boolean isLogged) {
        if ((serverPlayer instanceof FakePlayer)) return;
        SolarHolders.getSaveDataLazy(serverPlayer.level()).ifPresent(t ->
        {
            SimpleNetworkHandler.send(serverPlayer, new SolarTermsMessage(t.getSolarTermsDay()));
            if ((CommonConfig.Season.enableInform.get())
                    && isLogged
                    && MapChecker.isValidDimension(serverPlayer.level())
                    && t.getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                SolarTerm solarTerm = t.getSolarTerm();
                // if (solarTerm != SolarTerm.NONE)
                SimpleUtil.sendSolarTermMessage(serverPlayer, solarTerm, isLogged);
            }
            SimpleNetworkHandler.send(serverPlayer, new UpdateTempChangeMessage(t.getSolarTempChange()));
        });
        if (serverPlayer.level() instanceof ServerLevel serverLevel)
            WeatherManager.sendBiomePacket(serverLevel, WeatherManager.getBiomeList(serverPlayer.level()), List.of(serverPlayer));
    }

    public static boolean testWeatherCheck(LootContext pContext, WeatherCheck weatherCheck) {
        boolean needThunder = weatherCheck.isThundering().isPresent();
        boolean needRain = weatherCheck.isRaining().isPresent();
        if (needThunder) {
            var pos = pContext.getParamOrNull(LootContextParams.ORIGIN);
            if (pos != null) {
                boolean isThunderAt = isThunderAt(pContext.getLevel(), new BlockPos((int) pos.x, (int) pos.y + 1, (int) pos.z));
                if (weatherCheck.isThundering().get() != isThunderAt) {
                    return false;
                }
            }
        }
        if (needRain) {
            var pos = pContext.getParamOrNull(LootContextParams.ORIGIN);
            if (pos != null) {
                boolean isRainingAt = pContext.getLevel().isRainingAt(new BlockPos((int) pos.x, (int) pos.y + 1, (int) pos.z));
                if (weatherCheck.isRaining().get() != isRainingAt) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void tickPlayerForSeasonCheck(ServerPlayer serverPlayer, SolarTerm st) {
        // if (level.getGameTime() % 200 == 0)
        {
            var solarTermsRecordCa = serverPlayer.getData(AttachmentRegistry.SOLAR_TERMS_RECORD.get());

            // int timeInTerm = EclipticSeasonsApi.getInstance().getTimeInTerm(level);
            // if (timeInTerm != 0) return;

            if (solarTermsRecordCa.addAndCheck(st)) {
            } else ModAdvancements.solarTermsCriterion.get().trigger(serverPlayer);
        }
    }

    public static boolean agentAdvanceWeatherCycle(ServerLevel level, RandomSource random) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return true;
        // }

        int pos = NEXT_CHECK_BIOME_MAP.getOrDefault(level, -1);

        var levelBiomeWeather = getBiomeList(level);

        if (pos >= 0 && levelBiomeWeather != null && pos < levelBiomeWeather.size()) {
            int size = getWeatherTickFactor(level);
            var biomeWeather = levelBiomeWeather.get(pos);

            runWeather(level, biomeWeather, random, size);

            pos++;
        } else {
            pos = 0;
        }
        // Ecliptic.logger(level.getGameTime(),level.getGameTime() & 100);
        if (levelBiomeWeather != null && (level.getGameTime() % 100) == 0 && !level.players().isEmpty()) {
            // EclipticSeasonsMod.logger(level.getGameTime());
            sendBiomePacket(level, levelBiomeWeather, level.players());
        }

        NEXT_CHECK_BIOME_MAP.put(level, pos);
        return true;
    }

    public static void sendBiomePacket(ServerLevel level, ArrayList<BiomeWeather> levelBiomeWeather, List<ServerPlayer> players) {
        if (players.isEmpty()) return;
        Registry<WeatherEffect> weatherEffects = level.registryAccess().registryOrThrow(ESRegistries.WEATHER_EFFECT);
        byte[] snows = new byte[levelBiomeWeather.size()];
        int[] special = new int[levelBiomeWeather.size()];
        int[] weather = new int[levelBiomeWeather.size()];
        for (BiomeWeather biomeWeather : levelBiomeWeather) {
            int index = biomeWeather.id;
            snows[index] = biomeWeather.getSnowDepth();
            special[index] = biomeWeather.effect == null || biomeWeather.effect.getKey() == null ? -1 :
                    weatherEffects.getId(biomeWeather.effect.getKey());
            weather[index] =
                    BiomeRainDispatcher.indexOf(true, biomeWeather.getBiomeRain());
        }
        var msg = new BiomeWeatherMessage(snows, special, weather);
        SimpleNetworkHandler.send(players, msg);
    }

    public enum SnowRenderStatus {
        SNOW,
        SNOW_MELT,
        // RAIN,
        // CLOUD,
        NONE
    }

    public enum SnowStatus implements ITranslatable {
        SNOW,
        // MELT,
        NONE;
        public static final Codec<SnowStatus> CODEC = StringRepresentable.fromEnum(SnowStatus::values);

        @Override
        public Component getTranslation() {
            return Component.translatable("info.eclipticseasons.environment.snow_status." + getName());
        }
    }


    @Deprecated(forRemoval = true, since = "0.12.0.1")
    public static SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos) {
        return getSnowStatus(level, biome, pos, isRainingOrSnowAtBiome(level, biome));
    }

    public static SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos, boolean rain) {
        var status = SnowRenderStatus.NONE;
        if (!hasNonePrecipitation(biome.value())) {
            BeforeCheckSnowStatusEvent result = ESEventHook.modifySnowStatus(level, biome, pos, rain);
            if (result.getStatus() != null) return result.getStatus();
            rain = result.isRain();

            Biome.Precipitation precipitation = getPrecipitationAt(level, biome.value(), pos);
            if (precipitation == Biome.Precipitation.SNOW) {
                if (rain) status = SnowRenderStatus.SNOW;
            } else {
                status = level.getRandom().nextBoolean() | (rain && precipitation == Biome.Precipitation.RAIN) ?
                        SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            }
        }
        return status;
    }

    // Not use the class directly since the field would change the call method in future
    public static class BiomeWeather {
        public Holder<Biome> biomeHolder;
        public int id;

        public ResourceLocation location;
        private float snowDepth = 0;
        private byte b_snowDepth = 0;
        public long lastRainTime = 0;

        // patch
        @Nullable
        public Holder<WeatherEffect> effect = null;

        @Setter
        private BiomeRain biomeRain = FlatRain.NONE;

        public BiomeRain getBiomeRain() {
            return biomeRain == null ? FlatRain.NONE : biomeRain;
        }

        public BiomeWeather(Holder<Biome> biomeHolder) {
            this.biomeHolder = biomeHolder;
        }

        public void setSnowDepth(float snowDepth) {
            this.snowDepth = snowDepth;
            this.b_snowDepth = (byte) snowDepth;
        }

        public byte getSnowDepth() {
            return b_snowDepth;
        }

        @Override
        public String toString() {
            return serializeNBT().toString();
        }


        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("biome", location.toString());
            tag.putLong("lastRainTime", lastRainTime);
            tag.putFloat("snowDepth", snowDepth);
            if (effect != null)
                tag.putString("specialEffect", effect.getKey().location().toString());
            tag.putInt("biomeRain", BiomeRainDispatcher.indexOf(true, biomeRain));
            return tag;
        }


        public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider pRegistries, long oldData) {
            location = ResourceLocation.parse(nbt.getString("biome"));
            lastRainTime = nbt.getLong("lastRainTime");
            // snowDepth = nbt.getFloat("snowDepth");
            setSnowDepth(nbt.getFloat("snowDepth"));
            if (nbt.contains("specialEffect")) {
                effect = pRegistries.holder(ResourceKey.create(ESRegistries.WEATHER_EFFECT, ResourceLocation.parse(nbt.getString("specialEffect"))))
                        .orElse(null);
            }
            if (nbt.contains("biomeRain")) {
                this.biomeRain = BiomeRainDispatcher.getBiomeRain(true, nbt.getInt("biomeRain"));
            }
        }
    }
}

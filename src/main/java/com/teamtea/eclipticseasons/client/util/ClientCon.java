package com.teamtea.eclipticseasons.client.util;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.ClientAgent;
import com.teamtea.eclipticseasons.common.network.message.DataPackEventMessage;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongBooleanImmutablePair;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ClientCon {

    public static final Long2ObjectOpenHashMap<LongBooleanImmutablePair> roomCache = new Long2ObjectOpenHashMap<>();
    public static float humidityModificationLevel;

    private static Level useLevel;
    private static Level nextLevel;

    public static SolarTerm nowSolarTerm = SolarTerm.NONE;
    public static Season nowSeason = Season.NONE;

    public static int nowSolarYear = 0;
    public static int nowGregorianYear = 0;
    public static boolean isDay = false;
    public static boolean isEvening = false;
    public static boolean isNoon = false;

    public static int progress = 0;

    public final static List<HumidityControl> humidityControls = new ArrayList<>();
    public static DataPackEventMessage<BiomesClimateSettings> biomeDataPackCache;
    public static DataPackEventMessage<SnowDefinition> snowDefCache;
    public static DataPackEventMessage<SeasonCycle> seasonCycleCache;
    public static DataPackEventMessage<CustomRainBuilder> biomeRainCache;
    public static DataPackEventMessage<CustomSnowTerm> snowTermCache;
    public static DataPackEventMessage<AgroClimaticZone> aczCache;
    public static DataPackEventMessage<CropGrowControlBuilder> cropCache;
    @Getter
    public static ClientAgent agent = new ClientAgent() {
    };

    // Use for export
    public static String ServerName = "client";

    public static void tick(Level clientLevel) {
        if (MapChecker.isValidDimension(clientLevel)) {
            nowSolarTerm = EclipticUtil.getNowSolarTerm(clientLevel);
            ClientCon.nowSeason = EclipticSeasonsApi.getInstance().getSeasonSignal(clientLevel,
                    agent.getCameraEntity() == null ? BlockPos.ZERO :
                            agent.getCameraEntity().blockPosition());
            isDay = EclipticUtil.isDay(clientLevel);
            isEvening = EclipticUtil.isEvening(clientLevel);
            isNoon = EclipticUtil.isNoon(clientLevel);
            SolarDataManager saveData = SolarHolders.getSaveData(clientLevel);
            if (saveData != null) {
                ClientCon.progress = Mth.clamp(Mth.floor(((saveData.getSolarTermDaysInPeriod() + (Mth.floor((clientLevel.getDayTime() + EclipticUtil.getDayLengthInMinecraft(clientLevel)) % ((long) EclipticUtil.getDayLengthInMinecraft(clientLevel)) / ((float) EclipticUtil.getDayLengthInMinecraft(clientLevel)) * 10)) / 10f) * 100 / saveData.getSolarTermLastingDays())), 0, 100);
            }
            ClientCon.nowGregorianYear = EclipticSeasonsApi.getInstance().getGregorianYear(clientLevel);
        } else {
            ClientCon.nowSolarTerm = SolarTerm.NONE;
            ClientCon.nowSeason = Season.NONE;
            ClientCon.isDay = false;
            ClientCon.isEvening = false;
            ClientCon.isNoon = false;
            ClientCon.progress = 0;
            ClientCon.nowSolarYear = 0;
            ClientCon.nowGregorianYear = 0;
        }

        if (!roomCache.isEmpty()) {
            long gameTime = clientLevel.getGameTime();
            roomCache.entrySet().removeIf(entry ->
                    gameTime > entry.getValue().leftLong() + 100);
        }
    }

    public static Level getUseLevel() {
        return useLevel;
    }

    public static void setUseLevel(Level level) {
        if (level == null) {
            useLevel = null;
            if (nextLevel != null) {
                useLevel = nextLevel;
                nextLevel = null;
            }
        } else {
            if (useLevel == null)
                useLevel = level;
            else nextLevel = level;
        }
    }

    public static void onClientPlayerExit() {
        humidityControls.clear();
        biomeDataPackCache = null;
        snowDefCache = null;
        seasonCycleCache = null;
        biomeRainCache = null;
        snowTermCache = null;
        aczCache = null;
        cropCache = null;

        roomCache.clear();
        humidityModificationLevel = 0;
    }
}

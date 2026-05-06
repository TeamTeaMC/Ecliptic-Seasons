package com.teamtea.eclipticseasons.client.util;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.ClientAgent;
import it.unimi.dsi.fastutil.longs.*;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

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

    // Use for export
    public static String ServerName = "client";

    @Getter
    public static ClientAgent agent = new ClientAgent() {
    };

    public static void tick(Level clientLevel) {
        if (MapChecker.isValidDimension(clientLevel)) {
            ClientCon.nowSolarTerm = EclipticUtil.getNowSolarTerm(clientLevel);
            ClientCon.nowSeason = EclipticSeasonsApi.getInstance().getSeasonSignal(clientLevel,
                    agent.getCameraEntity() == null ? BlockPos.ZERO :
                            agent.getCameraEntity().blockPosition());
            ClientCon.isDay = EclipticUtil.isDay(clientLevel);
            ClientCon.isEvening = EclipticUtil.isEvening(clientLevel);
            ClientCon.isNoon = EclipticUtil.isNoon(clientLevel);
            SolarDataManager saveData = SolarHolders.getSaveData(clientLevel);
            if (saveData != null) {
                ClientCon.progress = Mth.clamp(Mth.floor(((saveData.getSolarTermDaysInPeriod() + (Mth.floor((clientLevel.getDayTime() + EclipticUtil.getDayLengthInMinecraft(clientLevel)) % ((long) EclipticUtil.getDayLengthInMinecraft(clientLevel)) / ((float) EclipticUtil.getDayLengthInMinecraft(clientLevel)) * 10)) / 10f) * 100 / saveData.getSolarTermLastingDays())), 0, 100);
            }
            ClientCon.nowSolarYear = EclipticUtil.getNowSolarYear(clientLevel);
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
        // useLevel=clientLevel;
    }

    public static Level getUseLevel() {
        return useLevel;
    }

    // would load level before unload
    // so if level create and useLevel is none, we should add it if not none means next level
    // if level is none so check if we have received a new level and push it if not means exit
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
        roomCache.clear();
        humidityModificationLevel = 0;
    }


}

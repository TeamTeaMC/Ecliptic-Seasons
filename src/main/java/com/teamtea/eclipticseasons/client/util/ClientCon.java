package com.teamtea.eclipticseasons.client.util;


import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.message.DataPackEventMessage;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongBooleanImmutablePair;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ClientCon {

    public static final Long2ObjectOpenHashMap<LongBooleanImmutablePair> roomCache = new Long2ObjectOpenHashMap<>();
    public static int humidityModificationLevel;

    private static Level useLevel;
    private static Level nextLevel;

    public static SolarTerm nowSolarTerm = SolarTerm.NONE;
    public static boolean isDay = false;
    public static boolean isEvening = false;
    public static boolean isNoon = false;

    public final static List<HumidityControl> humidityControls=new ArrayList<>();
    public static DataPackEventMessage<BiomesClimateSettings> biomeDataPackCache;

    public static void tick(Level clientLevel) {
        if (MapChecker.isValidDimension(clientLevel)) {
            nowSolarTerm = EclipticUtil.getNowSolarTerm(clientLevel);
            isDay = EclipticUtil.isDay(clientLevel);
            isEvening = EclipticUtil.isEvening(clientLevel);
            isNoon = EclipticUtil.isNoon(clientLevel);
        } else {
            nowSolarTerm = SolarTerm.NONE;
            isDay = false;
            isEvening = false;
            isNoon = false;
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
        biomeDataPackCache=null;
        roomCache.clear();
        humidityModificationLevel=0;
    }
}

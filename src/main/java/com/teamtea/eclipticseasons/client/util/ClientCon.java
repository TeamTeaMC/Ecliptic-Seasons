package com.teamtea.eclipticseasons.client.util;


import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.world.level.Level;

public class ClientCon {
    public static Level useLevel;

    public static SolarTerm nowSolarTerm = SolarTerm.NONE;
    public static boolean isDay = false;
    public static boolean isEvening = false;
    public static boolean isNoon = false;

    // Use for export
    public static String ServerName = "client";

    public static void tick(Level clientLevel) {
        if (MapChecker.isValidDimension(clientLevel)) {
            ClientCon.nowSolarTerm = EclipticUtil.getNowSolarTerm(clientLevel);
            ClientCon.isDay = EclipticUtil.isDay(clientLevel);
            ClientCon.isEvening = EclipticUtil.isEvening(clientLevel);
            ClientCon.isNoon = EclipticUtil.isNoon(clientLevel);
        } else {
            ClientCon.nowSolarTerm = SolarTerm.NONE;
            ClientCon.isDay = false;
            ClientCon.isEvening = false;
            ClientCon.isNoon = false;
        }
    }
}

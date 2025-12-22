package com.teamtea.eclipticseasons.common.util;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;


public class time4jUtil {

    public static SolarTerm getCurrent() {
        return SolarTerm.get(net.time4j.calendar.ChineseCalendar.nowInSystemTime().getSolarTerm().ordinal());
    }

    public static int getYear() {
        return net.time4j.calendar.ChineseCalendar.nowInSystemTime().getYear().getNumber();
    }

    public static double getSolarTermProgress() {
        var now = net.time4j.PlainTimestamp.nowInSystemTime();

        double dayProgress =
                (now.getHour() * 3600
                        + now.getMinute() * 60
                        + now.getSecond())
                        / 86400.0;

        double avgTermDays = 365.2422 / 24.0;

        double progress = dayProgress / avgTermDays;

        return Math.min(1.0, Math.max(0.0, progress));
    }

}
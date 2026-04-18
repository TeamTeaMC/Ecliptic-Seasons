package com.teamtea.eclipticseasons.common.core.solar;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;

import org.jspecify.annotations.Nullable;


public class SolarAngelHelper {

    public static float getSeasonCelestialAngle(@Nullable Holder<WorldClock> world, long worldTime) {
        return getCelestialAngle(getSolarAngelTime(world, worldTime, EclipticUtil.getDayLengthInMinecraftStatic()));
    }

    public static int getSolarAngelTime(@Nullable Holder<WorldClock> clock, long worldTime, long loopTicks) {
        if (clock != null
                && SolarHolders.getSaveData(clock) instanceof SolarDataManager data
                && data.isValidDimension()) {
            int dayLength = data.getDayCycleTicks();
            if (dayLength != loopTicks)
                return Math.toIntExact(worldTime % loopTicks);

            int quart = dayLength / 4;
            int half = quart * 2;
            int offset = quart * 3;

            int dayLevelTime = Math.toIntExact((worldTime + offset) % dayLength); // 0 for noon; 6000 for sunset; 18000 for sunrise.

            // 这里必须要等于，因为0时刻还没切换到下一天。
            // Must be equal here, as the time 0 tick has not transitioned to the next day.
            int dayTime =
                    dayLevelTime > half && dayLevelTime <= offset && data.isTodayLastDay() ?
                            data.getNextSolarTerm().getDayTime() :
                            data.getSolarTerm().getDayTime();

            int sunrise = dayLength - dayTime / 2;
            int sunset = dayTime / 2;
            int solarAngelTime;
            if (0 <= dayLevelTime && dayLevelTime <= sunset) {
                solarAngelTime = quart + dayLevelTime * quart / sunset;
            } else if (dayLevelTime > sunset && dayLevelTime <= sunrise) {
                solarAngelTime = half + (dayLevelTime - sunset) * half / (dayLength - dayTime);
            } else {
                solarAngelTime = (dayLevelTime - sunrise) * quart / (dayLength - sunrise);
            }

            return solarAngelTime;
        }
        return Math.toIntExact(worldTime % EclipticUtil.getDayLengthInMinecraftStatic());
    }

    public static int getDayTimeFromSolarAngelTime(@Nullable Holder<WorldClock> clock,
                                                   int solarAngelTime,
                                                   int loopTicks,
                                                   ResourceKey<ClockTimeMarker> timeMarker) {
        if (clock != null
                && SolarHolders.getSaveData(clock) instanceof SolarDataManager data
                && data.isValidDimension()) {

            int dayLength = data.getDayCycleTicks();
            if (dayLength != loopTicks) {
                return solarAngelTime;
            }

            int half = dayLength / 2;

            int dayTime = data.getSolarTerm().getDayTime();
            int dayTimeOffset = -dayTime + half;

            if (timeMarker.equals(ClockTimeMarkers.DAY)) {
                solarAngelTime += dayTimeOffset / 4;
            } else if (timeMarker.equals(ClockTimeMarkers.NIGHT)) {
                solarAngelTime -= dayTimeOffset / 4;
            } else if (timeMarker.equals(ClockTimeMarkers.WAKE_UP_FROM_SLEEP)) {
                solarAngelTime += dayTimeOffset / 4;
            } else if (timeMarker.equals(ClockTimeMarkers.ROLL_VILLAGE_SIEGE)) {
                solarAngelTime += dayTimeOffset / 4;
            } else return solarAngelTime;


            return Math.floorMod(solarAngelTime, dayLength);
        }

        return solarAngelTime;
    }

    public static float getCelestialAngle(long worldTime) {
        double d0 = Mth.frac((double) worldTime / ((double) EclipticUtil.getDayLengthInMinecraftStatic()) - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float) (d0 * 2.0D + d1) / 3.0F;
    }
}

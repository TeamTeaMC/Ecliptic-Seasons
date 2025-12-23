package com.teamtea.eclipticseasons.common.core.solar;

//import com.ibm.icu.impl.CalendarAstronomer;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Calendar;
import java.util.Date;

public class FixedSolarDataManagerLocal extends SolarDataManager {
    protected final CalendarAstronomer astro = new CalendarAstronomer();

    public FixedSolarDataManagerLocal(Level level, CompoundTag nbt) {
        super(level, nbt);
        updateTimeCounter();
    }

    public FixedSolarDataManagerLocal(Level level) {
        super(level);
        updateTimeCounter();
    }

    private void updateTimeCounter() {
        if (!isValidDimension) return;
        //Date nextSolarTermByDay = getNextSolarTermByDay(astro);
        astro.setDate(new Date());
    }


    @Override
    public void tickLevel(Level level) {
        super.tickLevel(level);
        updateTimeCounter();
    }

    @Override
    public SolarTerm getSolarTerm() {
        return SolarTerm.get(getSolarTermIndex());
    }

    @Override
    public int getSolarTermIndex() {
        return getIndex(astro);
    }


    @Override
    public int getSolarYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    public int getSolarTermsDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        //return (getSolarTermIndex() * getSolarTermLastingDays()) + getSolarTermDaysInPeriod();
    }

    @Override
    public int getSolarTermDaysInPeriod() {
        double sunLongitudeDeg = Math.toDegrees(astro.getSunLongitude());
        sunLongitudeDeg = (sunLongitudeDeg + 360) % 360;
        return (int) (sunLongitudeDeg % 15 * getSolarTermLastingDays() / 15);
    }

    @Override
    public boolean isTodayLastDay() {
        return false;
    }


    private static int getIndex(CalendarAstronomer astro) {
        double sunLongitudeDeg = Math.toDegrees(astro.getSunLongitude());
        sunLongitudeDeg = (sunLongitudeDeg + 360) % 360;
        int index = (int) (sunLongitudeDeg / 15 + 3);
        index = Math.floorMod(index, 24);
        return index;
    }

    public Date getNextSolarTermByDay(CalendarAstronomer astro) {
        Date current = new Date();
        astro.setDate(current);

        int currentIndex = getIndex(astro);
        int nextIndex = (currentIndex + 1 + 21) % 24;
        double nextLongitude = nextIndex * 15;

        Calendar cal = Calendar.getInstance();
        cal.setTime(current);

        while (true) {
            astro.setDate(cal.getTime());
            double sunDeg = Math.toDegrees(astro.getSunLongitude());
            sunDeg = (sunDeg + 360) % 360;
            if (sunDeg >= nextLongitude) {
                break;
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return cal.getTime();
    }

}

package com.teamtea.eclipticseasons.api.constant.solar;

import com.teamtea.eclipticseasons.api.misc.ITranslatableWithPlaceholder;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public enum Month implements ITranslatableWithPlaceholder {
    MONTH_1, MONTH_2, MONTH_3, MONTH_4,
    MONTH_5, MONTH_6, MONTH_7, MONTH_8,
    MONTH_9, MONTH_10, MONTH_11, MONTH_12,
    NONE;

    // public static int MONTH_OFFSET = 1;
    // public static int DAY_OFFSET = 2;

    @Override
    public String getName() {
        return "month_" + (ordinal() + 1);
    }

    @Override
    public boolean isValid() {
        return this != NONE;
    }

    @Override
    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.month." + getName());
    }

    // public static Month of(int solarDays, int lastingDaysOfTerm) {
    //     return of(solarDays, lastingDaysOfTerm, DAY_OFFSET, MONTH_OFFSET);
    // }

    public static Month of(int solarDays, int lastingDaysOfTerm, int dayOffset, int monthOffset) {
        if (lastingDaysOfTerm <= 0) return Month.NONE;
        int dayInYear = Math.floorMod(solarDays + dayOffset, lastingDaysOfTerm * 24);
        int monthIndex = dayInYear / (lastingDaysOfTerm * 2);
        monthIndex = Math.floorMod(monthIndex + monthOffset, collectValidValues().length);
        return collectValidValues()[monthIndex];
    }

    public static int ofDay(int solarDays, int lastingDaysOfTerm, int dayOffset) {
        if (lastingDaysOfTerm <= 0) return 0;
        int yearLength = lastingDaysOfTerm * 24;
        int monthLength = lastingDaysOfTerm * 2;
        int dayInYear = Math.floorMod(solarDays + dayOffset, yearLength);
        return dayInYear % monthLength + 1;
    }


    public static int toYear(int solarDays, int lastingDaysOfTerm, int dayOffset, int monthOffset) {
        if (lastingDaysOfTerm <= 0) return 0;
        int yearLength = lastingDaysOfTerm * 24;
        int totalDays = solarDays + monthOffset * (lastingDaysOfTerm * 2) + dayOffset;
        return Math.floorDiv(totalDays, yearLength) + 1;
    }

    private static final Month[] values = Month.values();

    public static Month[] collectValues() {
        return values;
    }

    private static final Month[] validValues = Arrays.stream(values)
            .filter(Month::isValid)
            .toArray(Month[]::new);

    public static Month[] collectValidValues() {
        return validValues;
    }
}

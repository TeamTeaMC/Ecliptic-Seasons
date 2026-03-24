package com.teamtea.eclipticseasons.api.constant.solar;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;

public enum TimePeriod implements ITranslatable {
    DAWN,      // 早晨
    DAY,       // 白天
    DUSK,      // 黄昏
    NIGHT,     // 夜晚
    MIDNIGHT,  // 午夜
    NONE;

    private static final TimePeriod[] timePeriods = TimePeriod.values();

    public static TimePeriod[] collectValues() {
        return timePeriods;
    }

    public static TimePeriod fromTimeOfDay(float timeOfDay) {
        float angle = (timeOfDay * 20f + 5) % 20;
        if (angle > 0.83 && angle < 10) {
            return DAY;
        } else if (angle <= 0.83f) {
            return DAWN;
        } else if (angle < 11.42f) {
            return DUSK;
        } else if (angle < 15) {
            return NIGHT;
        } else if (angle < 19.16f) {
            return MIDNIGHT;
        } else {
            return DAWN;
        }
    }

    @Override
    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.time_period." + getName());
    }
}

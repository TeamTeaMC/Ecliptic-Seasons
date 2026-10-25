package com.teamtea.eclipticseasons.api.constant.simulation;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Defines how snowfall schedules are assigned through built-in datapacks.
 */
public enum SnowfallTiming implements ITranslatable {

    UNIFIED(ChatFormatting.AQUA),
    REGIONAL(ChatFormatting.GREEN),
    CUSTOM(ChatFormatting.YELLOW);

    private final ChatFormatting color;

    SnowfallTiming(ChatFormatting color) {
        this.color = color;
    }

    public static SnowfallTiming getSnowfallTiming() {
        return CommonConfig.Resource.snowfallTiming.get();
    }

    public static void setSnowfallTiming(SnowfallTiming timing) {
        CommonConfig.Resource.snowfallTiming.set(timing);
    }

    @Override
    public Component getTranslation() {
        return Component.translatable(
                "info.eclipticseasons.snowfall_timing." + getName()
        ).withStyle(color);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(
                "info.eclipticseasons.snowfall_timing." + getName() + ".tooltip"
        );
    }
}

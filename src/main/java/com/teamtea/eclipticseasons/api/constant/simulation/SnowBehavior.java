package com.teamtea.eclipticseasons.api.constant.simulation;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Aggregates the three snow/ice configuration values into a single
 * user-facing mode for the recommended settings screen.
 */
public enum SnowBehavior implements ITranslatable {
    DISABLED(ChatFormatting.GRAY),
    RENDER(ChatFormatting.AQUA),
    VANILLA(ChatFormatting.WHITE),
    BOTH(ChatFormatting.GREEN);

    private final ChatFormatting color;

    SnowBehavior(ChatFormatting color) {
        this.color = color;
    }

    public static SnowBehavior getSnowBehavior() {
        boolean render = CommonConfig.Snow.snowyWinter.get();
        boolean vanilla = CommonConfig.Temperature.snowDown.get()
                && CommonConfig.Temperature.iceMelt.get();

        if (render && vanilla) {
            return BOTH;
        }
        if (render) {
            return RENDER;
        }
        if (vanilla) {
            return VANILLA;
        }
        return DISABLED;
    }

    public static void setSnowBehavior(SnowBehavior mode) {
        switch (mode) {
            case DISABLED -> {
                CommonConfig.Snow.snowyWinter.set(false);
                CommonConfig.Temperature.snowDown.set(false);
                CommonConfig.Temperature.iceMelt.set(false);
            }
            case RENDER -> {
                CommonConfig.Snow.snowyWinter.set(true);
                CommonConfig.Temperature.snowDown.set(false);
                CommonConfig.Temperature.iceMelt.set(false);
            }
            case VANILLA -> {
                CommonConfig.Snow.snowyWinter.set(false);
                CommonConfig.Temperature.snowDown.set(true);
                CommonConfig.Temperature.iceMelt.set(true);
            }
            case BOTH -> {
                CommonConfig.Snow.snowyWinter.set(true);
                CommonConfig.Temperature.snowDown.set(true);
                CommonConfig.Temperature.iceMelt.set(true);
            }
        }

        CommonConfig.setVanillaSnowAndIce(
                CommonConfig.Temperature.snowDown.get()
                        && CommonConfig.Temperature.iceMelt.get()
        );
    }

    @Override
    public Component getTranslation() {
        return Component.translatable(
                "info.eclipticseasons.snow_behavior." + getName()
        ).withStyle(color);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(
                "info.eclipticseasons.snow_behavior." + getName() + ".tooltip"
        );
    }
}

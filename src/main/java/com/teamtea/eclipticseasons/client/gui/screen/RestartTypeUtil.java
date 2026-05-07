package com.teamtea.eclipticseasons.client.gui.screen;

import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class RestartTypeUtil {
    public static <T> RestartType get(ForgeConfigSpec.ConfigValue<T> configValue) {
        if (!SpecUtil.getSpec(configValue).needsWorldRestart()) return RestartType.NONE;
        if (configValue == ClientConfig.Renderer.seasonalColorOverrides
                || configValue == CommonConfig.Resource.extraSnow)
            return RestartType.GAME;
        return RestartType.WORLD;
    }

    public enum RestartType {
        NONE,
        WORLD,
        GAME;
    }
}

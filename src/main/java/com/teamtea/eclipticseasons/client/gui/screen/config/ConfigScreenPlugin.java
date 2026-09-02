package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;

@FunctionalInterface
public interface ConfigScreenPlugin {
    void register(ConfigScreenContext context);

    default boolean autoRegisterConfigs() {
        return true;
    }
}

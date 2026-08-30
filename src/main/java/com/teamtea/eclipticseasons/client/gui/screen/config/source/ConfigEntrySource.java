package com.teamtea.eclipticseasons.client.gui.screen.config.source;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
@FunctionalInterface
public interface ConfigEntrySource {
    void load(ConfigScreenContext context);
}

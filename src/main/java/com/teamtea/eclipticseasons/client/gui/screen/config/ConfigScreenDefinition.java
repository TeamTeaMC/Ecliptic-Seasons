package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;

public interface ConfigScreenDefinition {
    String modId();

    ConfigScreenText text();

    void initialize(ConfigScreenContext context);

    ConfigScreenSession createSession(ConfigScreenContext context);
}

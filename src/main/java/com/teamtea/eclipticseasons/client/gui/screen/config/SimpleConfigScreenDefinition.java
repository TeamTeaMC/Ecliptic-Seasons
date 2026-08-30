package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleConfigScreenDefinition implements ConfigScreenDefinition {
    protected String modId;
    protected ConfigScreenText text;
    protected Consumer<ConfigScreenContext> initializer;
    protected Function<ConfigScreenContext, ConfigScreenSession> sessionFactory;

    public SimpleConfigScreenDefinition(
            String modId,
            ConfigScreenText text,
            Consumer<ConfigScreenContext> initializer,
            Function<ConfigScreenContext, ConfigScreenSession> sessionFactory
    ) {
        this.modId = modId;
        this.text = text;
        this.initializer = initializer;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String modId() {
        return modId;
    }

    @Override
    public ConfigScreenText text() {
        return text;
    }

    @Override
    public void initialize(ConfigScreenContext context) {
        initializer.accept(context);
    }

    @Override
    public ConfigScreenSession createSession(ConfigScreenContext context) {
        return sessionFactory.apply(context);
    }
}

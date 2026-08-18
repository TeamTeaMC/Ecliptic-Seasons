package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * The top-level categories shown in the configuration screen.
 */
public enum ConfigCategory implements ITranslatable {

    GENERAL,
    ENVIRONMENT,
    GAMEPLAY,
    VISUAL,
    ADVANCED,
    ALL;

    public Component title() {
        return Component.translatable(
                "eclipticseasons.options."
                        + name().toLowerCase(Locale.ROOT)
        );
    }

    @Override
    public Component getTranslation() {
        return Component.translatable("eclipticseasons.options." + getName());
    }

    @Override
    public Component getDescription() {
        return Component.translatable("eclipticseasons.options." + getName() + ".tooltip");
    }
}

package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * The top-level categories shown in the configuration screen.
 */
public final class ConfigCategory implements ITranslatable {

    public static final ConfigCategory GENERAL = new ConfigCategory("GENERAL", 0);
    public static final ConfigCategory ENVIRONMENT = new ConfigCategory("ENVIRONMENT", 1);
    public static final ConfigCategory GAMEPLAY = new ConfigCategory("GAMEPLAY", 2);
    public static final ConfigCategory VISUAL = new ConfigCategory("VISUAL", 3);
    public static final ConfigCategory ADVANCED = new ConfigCategory("ADVANCED", 4);
    public static final ConfigCategory ALL = new ConfigCategory("ALL", 5);

    private static final ConfigCategory[] VALUES = {GENERAL, ENVIRONMENT, GAMEPLAY, VISUAL, ADVANCED, ALL};

    private final String name;
    private final int ordinal;

    private ConfigCategory(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public static ConfigCategory[] values() {
        return VALUES;
    }

    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    public Component title() {
        return Component.translatable(
                "eclipticseasons.options."
                        + name.toLowerCase(Locale.ROOT)
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

    @Override
    public String getName() {
        return name.toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return name;
    }
}
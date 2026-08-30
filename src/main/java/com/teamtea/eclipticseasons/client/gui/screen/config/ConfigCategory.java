package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Objects;

/** A top-level category represented by one sidebar button. */
public class ConfigCategory implements ITranslatable {
    public static final ConfigCategory GENERAL = create("eclipticseasons", "GENERAL", 0);
    public static final ConfigCategory ENVIRONMENT = create("eclipticseasons", "ENVIRONMENT", 1);
    public static final ConfigCategory GAMEPLAY = create("eclipticseasons", "GAMEPLAY", 2);
    public static final ConfigCategory VISUAL = create("eclipticseasons", "VISUAL", 3);
    public static final ConfigCategory ADVANCED = create("eclipticseasons", "ADVANCED", 4);
    public static final ConfigCategory ALL = create("eclipticseasons", "ALL", 5);

    private static final ConfigCategory[] DEFAULTS = {
            GENERAL, ENVIRONMENT, GAMEPLAY, VISUAL, ADVANCED, ALL
    };

    protected String namespace;
    protected String name;
    protected int order;
    protected Component title;
    protected Component description;

    protected ConfigCategory(
            String namespace,
            String name,
            int order,
            Component title,
            Component description
    ) {
        this.namespace = namespace;
        this.name = name.toUpperCase(Locale.ROOT);
        this.order = order;
        this.title = title;
        this.description = description;
    }

    public static ConfigCategory create(String namespace, String name, int order) {
        String path = name.toLowerCase(Locale.ROOT);
        String key = namespace + ".options." + path;
        return create(namespace, name, order,
                Component.translatable(key),
                Component.translatable(key + ".tooltip"));
    }

    public static ConfigCategory create(
            String namespace,
            String name,
            int order,
            Component title,
            Component description
    ) {
        return new ConfigCategory(namespace, name, order, title, description);
    }

    /** Returns the built-in categories. Custom categories live in ConfigScreenContext. */
    public static ConfigCategory[] values() {
        return DEFAULTS;
    }

    public String namespace() {
        return namespace;
    }

    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return order;
    }

    public int order() {
        return order;
    }

    public Component title() {
        return title;
    }

    @Override
    public Component getTranslation() {
        return title();
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof ConfigCategory other
                && namespace.equals(other.namespace)
                && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }

    @Override
    public String toString() {
        return name;
    }
}

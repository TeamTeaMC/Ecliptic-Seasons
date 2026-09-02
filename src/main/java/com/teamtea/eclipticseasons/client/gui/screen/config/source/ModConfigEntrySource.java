package com.teamtea.eclipticseasons.client.gui.screen.config.source;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModConfigEntrySource implements ConfigEntrySource {
    protected ConfigCategory category;
    protected Component section;
    protected List<ModConfig> configs;
    protected boolean splitSections;

    public ModConfigEntrySource(
            ConfigCategory category,
            Component section,
            Collection<ModConfig> configs
    ) {
        this(category, section, configs, false);
    }

    public ModConfigEntrySource(
            ConfigCategory category,
            Component section,
            Collection<ModConfig> configs,
            boolean splitSections
    ) {
        this.category = category;
        this.section = section;
        this.configs = new ArrayList<>(configs);
        this.splitSections = splitSections;
    }

    @Override
    public void load(ConfigScreenContext context) {
        if (!context.containsCategory(category)) return;

        context.registerConfigs(configs);

        for (ModConfig owner : configs) {
            if (!(owner.getSpec() instanceof ModConfigSpec spec)) continue;

            SyncType syncType = SyncType.of(owner.getType());

            if (splitSections) {
                collectRoot(spec.getValues(), syncType, owner, context);
            } else {
                collect(spec.getValues(), syncType, owner, context);
            }
        }
    }

    protected void collectRoot(
            UnmodifiableConfig config,
            SyncType syncType,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        for (UnmodifiableConfig.Entry rawEntry : config.entrySet()) {
            Object value = rawEntry.getValue();

            if (value instanceof ModConfigSpec.ConfigValue<?> configValue) {
                add(configValue, section, syncType, owner, context);
            } else if (value instanceof UnmodifiableConfig child) {
                collect(
                        child,
                        resolveSection(owner, rawEntry.getKey()),
                        syncType,
                        owner,
                        context
                );
            }
        }
    }

    protected void collect(
            UnmodifiableConfig config,
            SyncType syncType,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        collect(config, section, syncType, owner, context);
    }

    protected void collect(
            UnmodifiableConfig config,
            Component section,
            SyncType syncType,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        for (UnmodifiableConfig.Entry rawEntry : config.entrySet()) {
            Object value = rawEntry.getValue();

            if (value instanceof ModConfigSpec.ConfigValue<?> configValue) {
                add(configValue, section, syncType, owner, context);
            } else if (value instanceof UnmodifiableConfig child) {
                collect(child, section, syncType, owner, context);
            }
        }
    }

    protected void add(
            ModConfigSpec.ConfigValue<?> configValue,
            Component section,
            SyncType syncType,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        SpecEntry<?> entry = SpecEntry.parse(configValue);
        if (entry == null) return;

        entry.setSyncType(syncType);
        context.add(category, section, entry, owner);
    }

    protected Component resolveSection(ModConfig owner, String name) {
        String key = owner.getModId() + ".configuration." + name;
        return Component.translatable(key);
        // return I18n.exists(key)
        //         ? Component.translatable(key)
        //         : Component.literal(Platform.getModName(name, 0));
    }
}
package com.teamtea.eclipticseasons.client.gui.screen.config.source;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class ModConfigEntrySource implements ConfigEntrySource {
    protected ConfigCategory category;
    protected Component section;
    protected List<ModConfig> configs;

    public ModConfigEntrySource(
            ConfigCategory category,
            Component section,
            Collection<ModConfig> configs
    ) {
        this.category = category;
        this.section = section;
        this.configs = new ArrayList<>(configs);
    }

    @Override
    public void load(ConfigScreenContext context) {
        if (!context.containsCategory(category)) return;
        context.registerConfigs(configs);

        for (ModConfig config : configs) {
            if (config.getSpec() instanceof ModConfigSpec spec) {
                collect(spec.getValues(), SyncType.of(config.getType()), config, context);
            }
        }
    }

    protected void collect(
            UnmodifiableConfig config,
            SyncType syncType,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        for (UnmodifiableConfig.Entry rawEntry : config.entrySet()) {
            Object value = rawEntry.getValue();
            if (value instanceof ModConfigSpec.ConfigValue<?> configValue) {
                SpecEntry<?> entry = SpecEntry.parse(configValue);
                if (entry != null) {
                    entry.setSyncType(syncType);
                    context.add(category, section, entry, owner);
                }
            } else if (value instanceof UnmodifiableConfig child) {
                collect(child, syncType, owner, context);
            }
        }
    }

}

package com.teamtea.eclipticseasons.client.gui.screen.config.source;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        for (ModConfig config : configs) {
            if (config.getSpec() instanceof ForgeConfigSpec spec) {
                collectConfigValues(spec.getValues(), config, context);
            }
        }
    }

    protected void collectConfigValues(
            UnmodifiableConfig values,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        for (UnmodifiableConfig.Entry entry : values.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
                addConfigValue(configValue, owner, context);
            } else if (value instanceof UnmodifiableConfig child) {
                collectConfigValues(child, owner, context);
            }
        }
    }

    protected void addConfigValue(
            ForgeConfigSpec.ConfigValue<?> configValue,
            ModConfig owner,
            ConfigScreenContext context
    ) {
        SpecEntry<?> entry = SpecEntry.parse(configValue);
        if (entry == null) {
            return;
        }

        entry.setSyncType(SyncType.of(owner.getType()));
        context.add(category, section, entry, owner);
    }
}
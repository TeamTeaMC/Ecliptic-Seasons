package com.teamtea.eclipticseasons.client.gui.screen;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigChangeSet;
import com.teamtea.eclipticseasons.client.gui.screen.config.source.ConfigEntrySource;
import com.teamtea.eclipticseasons.client.gui.screen.config.tab.Tab;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.config.ModConfig;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.IdentityHashMap;
import java.util.HashSet;
import net.neoforged.fml.config.IConfigSpec;

public class ConfigScreenContext {
    protected Map<ConfigCategory, Tab> tabs = new LinkedHashMap<>();
    protected List<ConfigEntrySource> sources = new ArrayList<>();
    protected Set<ModConfig> configs = new LinkedHashSet<>();
    protected Map<IConfigSpec, ModConfig> configsBySpec = new IdentityHashMap<>();
    protected Map<ConfigEntry, Set<ModConfig>> entryOwners = new IdentityHashMap<>();
    protected Set<ConfigEntry> leadingEntries = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    public void registerCategory(ConfigCategory category) {
        tabs.computeIfAbsent(category, key -> new Tab(key.title(), new LinkedHashMap<>()));
    }

    public void clearCategories() {
        tabs.clear();
    }

    public void removeCategory(ConfigCategory category) {
        tabs.remove(category);
    }

    public boolean containsCategory(ConfigCategory category) {
        return tabs.containsKey(category);
    }

    public void addSource(ConfigEntrySource source) {
        sources.add(source);
    }

    public void loadSources() {
        sources.forEach(source -> source.load(this));
    }

    public void registerConfigs(Collection<ModConfig> configs) {
        for (ModConfig config : configs) {
            this.configs.add(config);
            configsBySpec.put(config.getSpec(), config);
        }
    }

    public Collection<ModConfig> configs() {
        return configs;
    }

    public void add(ConfigCategory category, Component section, ConfigEntry entry) {
        add(category, section, entry, new ModConfig[0]);
    }

    public void add(ConfigCategory category, Component section, ConfigEntry entry, ModConfig... owners) {
        if (entry == null) return;

        Tab tab = tabs.get(category);
        if (tab == null) {
            EclipticSeasons.LOGGER.warn("Unknown configuration category: {}", category);
            return;
        }
        tab.configShown().computeIfAbsent(section, key -> new ArrayList<>()).add(entry);
        if (owners.length > 0) entryOwners.put(entry, new LinkedHashSet<>(List.of(owners)));
    }

    public void addFirst(ConfigCategory category, Component section, ConfigEntry entry) {
        add(category, section, entry);
        if (entry != null) leadingEntries.add(entry);
    }

    public void put(ConfigCategory category, Component section, ModConfigSpec.ConfigValue<?>... values) {
        for (ModConfigSpec.ConfigValue<?> value : values) {
            if (value == null) continue;

            SpecEntry<?> entry = SpecEntry.parse(value);
            if (entry == null) continue;
            ModConfig owner = ownerOf(value);
            if (owner == null) add(category, section, entry);
            else add(category, section, entry, owner);
        }
    }

    public ModConfig ownerOf(IConfigSpec spec) {
        return configsBySpec.get(spec);
    }

    public ModConfig ownerOf(ModConfigSpec.ConfigValue<?> value) {
        for (ModConfig config : configs) {
            if (config.getSpec() instanceof ModConfigSpec spec
                    && spec.getSpec().get(value.getPath()) == value.getSpec()) return config;
        }
        return null;
    }

    public Tab tab(ConfigCategory category) {
        return tabs.get(category);
    }

    public List<ConfigCategory> categories() {
        return tabs.keySet().stream()
                .sorted(java.util.Comparator.comparingInt(ConfigCategory::order))
                .toList();
    }

    public Collection<Map.Entry<ConfigCategory, Tab>> tabEntries() {
        return tabs.entrySet();
    }

    public void sortEntries() {
        for (Tab tab : tabs.values()) {
            for (Map.Entry<Component, List<ConfigEntry>> section : tab.configShown().entrySet()) {
                section.getValue().sort(java.util.Comparator
                        .comparing((ConfigEntry entry) -> !leadingEntries.contains(entry))
                        .thenComparing(ConfigEntry::getPosition));
            }
        }
    }

    public ConfigChangeSet collectChanges(boolean inGame) {
        Set<ModConfig> changedConfigs = new LinkedHashSet<>();
        Set<SyncType> customTypes = new HashSet<>();
        boolean worldRestart = false;
        boolean gameRestart = false;

        for (Map.Entry<ConfigCategory, Tab> tabEntry : tabs.entrySet()) {
            for (List<ConfigEntry> entries : tabEntry.getValue().configShown().values()) {
                for (ConfigEntry entry : entries) {
                    if (!entry.isValueChanged()) continue;
                    worldRestart |= entry.shouldRestart(inGame);
                    gameRestart |= entry.shouldRestart(false);
                    Set<ModConfig> owners = entryOwners.get(entry);
                    if (owners == null || owners.isEmpty()) customTypes.add(entry.getSyncType());
                    else changedConfigs.addAll(owners);
                    if (entry instanceof SpecEntry<?> specEntry) specEntry.getSpec().clearCache();
                }
            }
        }
        customTypes.remove(SyncType.NONE);
        return new ConfigChangeSet(changedConfigs, customTypes, worldRestart, gameRestart);
    }
}

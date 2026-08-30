package com.teamtea.eclipticseasons.client.gui.screen.config.session;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigScreenSession {
    protected List<ModConfig> configs;
    protected Map<String, byte[]> snapshots = new HashMap<>();

    public ConfigScreenSession(Collection<ModConfig> configs) {
        this.configs = new ArrayList<>(configs);
        snapshot();
    }

    protected void snapshot() {
        for (ModConfig config : configs) {
            try {
                snapshots.put(
                        config.getFileName(),
                        Files.readAllBytes(
                                FMLPaths.CONFIGDIR.get().resolve(config.getFileName())
                        )
                );
            } catch (IOException exception) {
                EclipticSeasons.logger(exception);
            }
        }
    }

    public void restore() {
        for (Map.Entry<String, byte[]> snapshot : snapshots.entrySet()) {
            ModConfig config = ConfigTracker.INSTANCE.fileMap().get(snapshot.getKey());
            if (config != null) {
                config.acceptSyncedConfig(snapshot.getValue());
            }
        }
    }

    public ConfigSaveResult save(ConfigScreenContext context, boolean inGame) {
        ConfigChangeSet changes = context.collectChanges(inGame);
        if (changes.changed()) {
            saveConfigs(changes.configs());
            afterSave(changes);
        }
        return changes.result();
    }

    protected void saveConfigs(Collection<ModConfig> changedConfigs) {
        for (ModConfig config : changedConfigs) {
            if (config.getSpec() instanceof ForgeConfigSpec spec) {
                spec.save();
            }
        }
    }

    protected void afterSave(ConfigChangeSet changes) {
    }
}
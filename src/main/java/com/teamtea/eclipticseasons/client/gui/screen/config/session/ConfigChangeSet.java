package com.teamtea.eclipticseasons.client.gui.screen.config.session;

import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Set;

public record ConfigChangeSet(
        Set<ModConfig> configs,
        Set<SyncType> customTypes,
        boolean worldRestartRequired,
        boolean gameRestartRequired
) {
    public boolean changed() {
        return !configs.isEmpty() || !customTypes.isEmpty();
    }

    public ConfigSaveResult result() {
        return new ConfigSaveResult(worldRestartRequired, gameRestartRequired);
    }
}

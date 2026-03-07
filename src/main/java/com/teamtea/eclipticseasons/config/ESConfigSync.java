package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.ESConfigFilePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ESConfigSync {
    public static final ESConfigSync INSTANCE = new ESConfigSync();
    public static Set<IConfigSpec> specShouldSync = new HashSet<>(List.of(CommonConfig.COMMON_CONFIG));

    private ESConfigSync() {
    }


    public List<ESConfigFilePayload> syncConfigs(boolean isLocal) {
        final Map<String, byte[]> configData = new ConcurrentHashMap<>();
        for (ModConfig modConfig : ModConfigs.getConfigSet(ModConfig.Type.COMMON)) {
            for (IConfigSpec iConfigSpec : specShouldSync) {
                if (iConfigSpec == modConfig.getSpec()) {
                    try {
                        configData.put(modConfig.getFileName(), Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName())));
                        break;
                    } catch (IOException e) {
                        EclipticSeasons.logger(e);
                    }
                }
            }
        }

        return configData.entrySet().stream()
                .map(e -> new ESConfigFilePayload(e.getKey(), e.getValue()))
                .toList();
    }

    private final Map<String, byte[]> LOCAL_CONFIG_BACKUP = new ConcurrentHashMap<>();

    public void receiveSyncedConfig(final byte[] contents, final String fileName) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        ModConfig modConfig = ModConfigs.getFileMap().get(fileName);
        if (modConfig == null) {
            return;
        }

        if (!CommonConfig.Debug.forceServerConfig.get()) {
            try {
                byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName()));
                LOCAL_CONFIG_BACKUP.computeIfAbsent(fileName, k -> bytes);
            } catch (IOException e) {
                EclipticSeasons.logger(e);
            }
        }

        ConfigTracker.INSTANCE.acceptSyncedConfig(modConfig, contents);
    }

    public void onClientPlayerExit() {
        if (Minecraft.getInstance().isLocalServer()) {
            LOCAL_CONFIG_BACKUP.clear();
            return;
        }

        for (Map.Entry<String, byte[]> entry : LOCAL_CONFIG_BACKUP.entrySet()) {
            ModConfig modConfig = ModConfigs.getFileMap().get(entry.getKey());
            if (modConfig != null) {
                ConfigTracker.INSTANCE.acceptSyncedConfig(modConfig, entry.getValue());
            }
        }

        LOCAL_CONFIG_BACKUP.clear();
    }
}

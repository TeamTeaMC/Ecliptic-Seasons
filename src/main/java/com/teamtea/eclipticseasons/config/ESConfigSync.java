package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ESConfigSync {
    public static final ESConfigSync INSTANCE = new ESConfigSync(ConfigTracker.INSTANCE);
    private final ConfigTracker tracker;
    public static Set<IConfigSpec<?>> specShouldSync = new HashSet<>(List.of(CommonConfig.COMMON_CONFIG));

    private ESConfigSync(final ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public List<Pair<String, SimpleNetworkHandler.S2CConfigData>> syncConfigs(boolean isLocal) {
        final Map<String, byte[]> configData = new ConcurrentHashMap<>();
        for (ModConfig modConfig : tracker.configSets().get(ModConfig.Type.COMMON)) {
            for (IConfigSpec<?> iConfigSpec : specShouldSync) {
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
        return configData.entrySet().stream().map(e -> Pair.of("Config " + e.getKey(), new SimpleNetworkHandler.S2CConfigData(e.getKey(), e.getValue()))).collect(Collectors.toList());
    }


    //public void receiveSyncedConfig(final SimpleNetworkHandler.S2CConfigData s2CConfigData, final Supplier<NetworkEvent.Context> contextSupplier) {
    //    if (!Minecraft.getInstance().isLocalServer()) {
    //        Optional.ofNullable(tracker.fileMap().get(s2CConfigData.getFileName())).ifPresent(mc -> mc.acceptSyncedConfig(s2CConfigData.getBytes()));
    //    }
    //}
    private final Map<String, byte[]> LOCAL_CONFIG_BACKUP = new ConcurrentHashMap<>();

    public void receiveSyncedConfig(final SimpleNetworkHandler.S2CConfigData msg,
                                    final Supplier<NetworkEvent.Context> contextSupplier) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        ModConfig modConfig = tracker.fileMap().get(msg.getFileName());
        if (modConfig == null) {
            return;
        }

        if (!CommonConfig.Debug.forceServerConfig.get()) {
            try {
                byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName()));
                LOCAL_CONFIG_BACKUP.computeIfAbsent(msg.getFileName(), k -> bytes);
            } catch (IOException e) {
                EclipticSeasons.logger(e);
            }
        }

        modConfig.acceptSyncedConfig(msg.getBytes());
    }

    public void onClientPlayerExit() {
        if (Minecraft.getInstance().isLocalServer()) {
            LOCAL_CONFIG_BACKUP.clear();
            return;
        }

        for (Map.Entry<String, byte[]> entry : LOCAL_CONFIG_BACKUP.entrySet()) {
            ModConfig modConfig = tracker.fileMap().get(entry.getKey());
            if (modConfig != null) {
                modConfig.acceptSyncedConfig(entry.getValue());
            }
        }

        LOCAL_CONFIG_BACKUP.clear();
    }
}

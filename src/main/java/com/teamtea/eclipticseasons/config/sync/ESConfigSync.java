package com.teamtea.eclipticseasons.config.sync;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.ByteArrayInputStream;
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
        if (ServerLifecycleHooks.getCurrentServer() == null
                || ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
            return;
        }

        // if (fileName.equals(SyncType.MIXINS.configName(EclipticSeasonsApi.MODID))) {
        //     receiveSyncedMixinsConfig(contents);
        //     return;
        // }

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

    protected static void receiveSyncedMixinsConfig(byte[] contents) {
        CommentedFileConfig config = EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig();
        config.bulkCommentedUpdate(view -> {
            TomlFormat.instance().createParser().parse(new ByteArrayInputStream(contents), view, ParsingMode.REPLACE);
        });
        config.save();
    }

    public void onClientPlayerExit() {
        if (ServerLifecycleHooks.getCurrentServer() == null
                || ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
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

    public void notBackup(ModConfig modConfig) {
        LOCAL_CONFIG_BACKUP.remove(modConfig.getFileName());
    }


    public void syncToSever(ESConfigToServerPayload configFilePayload, ServerPlayer serverPlayer) {
        if (!serverPlayer.permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
            return;
        }

        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer != null && currentServer.isSingleplayer()) {
            return;
        }
        if (currentServer != null) {
            currentServer.getPlayerList().broadcastSystemMessage(Component.translatable(
                    configFilePayload.restart() ? "eclipticseasons.configuration.server_restart.hint" : "eclipticseasons.configuration.server_update.hint",
                    serverPlayer.getDisplayName(), configFilePayload.syncType().extension()), false);
            // currentServer.getPlayerList().broadcastChatMessage(PlayerChatMessage.unsigned(serverPlayer.getUUID(),
            //         "eclipticseasons.configuration.server_restart.hint"), serverPlayer, ChatType.bind(ChatType.CHAT, serverPlayer));
        }
        if (!configFilePayload.syncType().custom()) {
            ConfigTracker.INSTANCE.acceptSyncedConfig(ModConfigs.getFileMap().get(configFilePayload.fileName()),
                    configFilePayload.contents());
        } else if (configFilePayload.syncType() == SyncType.MIXINS) {
            receiveSyncedMixinsConfig(configFilePayload.contents());
        }
        if (currentServer != null) {
            for (ServerPlayer player : currentServer.getPlayerList().getPlayers()) {
                if (player == serverPlayer) continue;
                SimpleNetworkHandler.send(player, new ESConfigFilePayload(configFilePayload.fileName(), configFilePayload.contents()));
            }
        }
    }
}

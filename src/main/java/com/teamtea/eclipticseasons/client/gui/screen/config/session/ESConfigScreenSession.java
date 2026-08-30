package com.teamtea.eclipticseasons.client.gui.screen.config.session;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import com.teamtea.eclipticseasons.config.sync.ESConfigToServerPayload;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

public class ESConfigScreenSession extends ConfigScreenSession {
    protected CommentedFileConfig mixinConfig;
    protected String mixinFileName;

    public ESConfigScreenSession(Collection<ModConfig> configs) {
        super(configs);
    }

    public ESConfigScreenSession(
            Collection<ModConfig> configs,
            CommentedFileConfig mixinConfig,
            String mixinFileName
    ) {
        super(configs);
        this.mixinConfig = mixinConfig;
        this.mixinFileName = mixinFileName;
    }

    @Override
    protected void afterSave(ConfigChangeSet changes) {
        for (ModConfig config : changes.configs()) {
            if (config.getType() != ModConfig.Type.CLIENT) {
                ESConfigSync.INSTANCE.notBackup(config);
            }
        }

        if (mixinConfig != null && changes.customTypes().contains(SyncType.MIXINS)) {
            mixinConfig.save();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null
                || minecraft.isLocalServer()
                || !minecraft.player.hasPermissions(Commands.LEVEL_ADMINS)) {
            return;
        }

        try {
            for (ModConfig config : changes.configs()) {
                if (config.getType() == ModConfig.Type.CLIENT) {
                    continue;
                }

                byte[] contents = Files.readAllBytes(
                        FMLPaths.CONFIGDIR.get().resolve(config.getFileName())
                );

                sendToServer(new ESConfigToServerPayload(
                        config.getFileName(),
                        changes.worldRestartRequired(),
                        SyncType.of(config.getType()),
                        contents
                ));
            }

            if (mixinConfig != null
                    && changes.customTypes().contains(SyncType.MIXINS)) {
                byte[] contents = Files.readAllBytes(
                        FMLPaths.CONFIGDIR.get().resolve(mixinFileName)
                );

                sendToServer(new ESConfigToServerPayload(
                        mixinFileName,
                        true,
                        SyncType.MIXINS,
                        contents
                ));
            }
        } catch (IOException exception) {
            EclipticSeasons.logger(exception);
        }
    }

    protected void sendToServer(ESConfigToServerPayload payload) {
        SimpleNetworkHandler.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                payload
        );
    }
}
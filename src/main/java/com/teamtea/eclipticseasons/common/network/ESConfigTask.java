package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.config.ESConfigSync;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

class ESConfigTask implements ICustomConfigurationTask {

    private final Type configTask = new Type(EclipticSeasons.rl("config_task"));
    private final ServerConfigurationPacketListener event;

    public ESConfigTask(ServerConfigurationPacketListener event) {
        this.event = event;
    }

    @Override
    public void run(@NonNull Consumer<CustomPacketPayload> sender) {
        for (ESConfigFilePayload syncConfig : ESConfigSync.INSTANCE.syncConfigs(false)) {
            sender.accept(syncConfig);
        }
        event.finishCurrentTask(type());
    }

    @Override
    public @NonNull Type type() {
        return this.configTask;
    }
}

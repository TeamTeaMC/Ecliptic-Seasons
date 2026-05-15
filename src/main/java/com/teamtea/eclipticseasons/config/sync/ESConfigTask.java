package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ESConfigTask implements ICustomConfigurationTask {

    private final Type configTask = new Type(EclipticSeasons.rl("config_task"));
    private final RegisterConfigurationTasksEvent event;

    public ESConfigTask(RegisterConfigurationTasksEvent event) {
        this.event = event;
    }

    @Override
    public void run(@NotNull Consumer<CustomPacketPayload> sender) {
        for (ESConfigFilePayload syncConfig : ESConfigSync.INSTANCE.syncConfigs(false)) {
            sender.accept(syncConfig);
        }
        event.getListener().finishCurrentTask(type());
    }

    @Override
    public @NotNull Type type() {
        return this.configTask;
    }
}

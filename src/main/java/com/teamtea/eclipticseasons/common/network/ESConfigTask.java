package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.ESConfigSync;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class ESConfigTask implements ICustomConfigurationTask {

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

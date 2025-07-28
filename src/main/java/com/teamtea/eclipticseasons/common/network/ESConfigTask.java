package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.message.ConfigMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
        sender.accept(new ConfigMessage(
                CommonConfig.Season.validDimensions.get().stream()
                        .map(s -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(s)))
                        .toList()
        ));
        event.getListener().finishCurrentTask(type());
    }

    @Override
    public @NotNull Type type() {
        return this.configTask;
    }
}

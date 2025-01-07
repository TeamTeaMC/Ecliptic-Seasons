package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SimpleNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(EclipticSeasons.NETWORK_VERSION);

        registrar
                .configurationToClient(
                        ConfigMessage.TYPE,
                        ConfigMessage.STREAM_CODEC,
                        NetworkUtil::handleConfigMessage);

        registrar.playToClient(
                SolarTermsMessage.TYPE,
                SolarTermsMessage.STREAM_CODEC,
                NetworkUtil::processSolarTermsMessage2
        );


        registrar.playToClient(
                EmptyMessage.TYPE,
                EmptyMessage.STREAM_CODEC,
                NetworkUtil::processEmptyMessage
        );

        registrar.playToClient(
                BiomeWeatherMessage.TYPE,
                BiomeWeatherMessage.STREAM_CODEC,
                NetworkUtil::processBiomeWeatherMessage
        );

        registrar.playToClient(
                ChunkUpdateMessage.TYPE,
                ChunkUpdateMessage.STREAM_CODEC,
                NetworkUtil::processChunkUpdateMessage
        );

        registrar.playToClient(
                ChunkBiomeUpdateMessage.TYPE,
                ChunkBiomeUpdateMessage.STREAM_CODEC,
                NetworkUtil::processChunkBiomeUpdateMessage
        );

        registrar.playToClient(
                MapFixerMessage.TYPE,
                MapFixerMessage.STREAM_CODEC,
                NetworkUtil::processMapFixerMessage
        );
    }

    @SubscribeEvent
    public static void onRegisterConfigurationTasksEvent(RegisterConfigurationTasksEvent event){
        event.register(new ICustomConfigurationTask() {

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
                return new ConfigurationTask.Type(EclipticSeasons.rl("config_task"));
            }
        });
    }


    public static <MSG extends CustomPacketPayload> void send(ServerPlayer player, MSG msg) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static <MSG extends CustomPacketPayload> void send(List<ServerPlayer> players, MSG msg) {
        players.forEach(player -> {
            // if (player instanceof ServerPlayer serverPlayer)
            {
                send(player, msg);
            }
        });
    }
}

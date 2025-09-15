package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.network.message.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@SuppressWarnings("removal")
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
                HumidModifyMessage.TYPE,
                HumidModifyMessage.STREAM_CODEC,
                NetworkUtil::processHumidModifyMessage
        );

        registrar.playToClient(
                UpdateTempChangeMessage.TYPE,
                UpdateTempChangeMessage.STREAM_CODEC,
                NetworkUtil::processUpdateTempChangeMessage
        );
    }

    @SubscribeEvent
    public static void onRegisterConfigurationTasksEvent(RegisterConfigurationTasksEvent event) {
        event.register(new ESConfigTask(event));
    }


    public static <MSG extends CustomPacketPayload> void send(ServerPlayer player, MSG msg) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static <MSG extends CustomPacketPayload> void send(List<ServerPlayer> players, MSG msg) {
        players.forEach(player -> send(player, msg));
    }

}

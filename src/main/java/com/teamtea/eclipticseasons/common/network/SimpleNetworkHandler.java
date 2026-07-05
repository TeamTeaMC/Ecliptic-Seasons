package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfoResolver;
import com.teamtea.eclipticseasons.common.network.clientmesage.GrowthInfoQuery;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.config.sync.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Collection;
import java.util.Optional;

@EventBusSubscriber
public final class SimpleNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(EclipticSeasons.NETWORK_VERSION);


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
        registrar.configurationToClient(
                ESConfigFilePayload.TYPE,
                ESConfigFilePayload.STREAM_CODEC,
                NetworkUtil::handleConfig);

        registrar.playToClient(
                ESConfigToClientPayload.TYPE,
                ESConfigToClientPayload.STREAM_CODEC,
                NetworkUtil::handleConfig
        );

        registrar.playToServer(
                ESConfigToServerPayload.TYPE,
                ESConfigToServerPayload.STREAM_CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    if (player instanceof ServerPlayer serverPlayer)
                        ESConfigSync.INSTANCE.syncToSever(payload, serverPlayer);
                }
        );

        registrar.playToClient(
                GrowthInfoMessage.TYPE,
                GrowthInfoMessage.STREAM_CODEC,
                NetworkUtil::handleGrowthInfoQuery
        );

        registrar.playToServer(
                GrowthInfoQuery.TYPE,
                GrowthInfoQuery.STREAM_CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    context.enqueueWork(() -> {
                        if (player instanceof ServerPlayer serverPlayer
                                && serverPlayer.level() instanceof ServerLevel level
                                && MapChecker.isLoadedOnlyServer(level, payload.getPos()))
                            send(serverPlayer, new GrowthInfoMessage(Optional.ofNullable(GrowthInfoResolver.resolve(
                                    level, payload.getPos(), level.getBlockState(payload.getPos())
                            ))));
                    });
                }
        );

    }

    @SubscribeEvent
    public static void onRegisterConfigurationTasksEvent(RegisterConfigurationTasksEvent event) {
        ServerConfigurationPacketListener listener = event.getListener();
        if (listener.hasChannel(ESConfigFilePayload.TYPE)) {
            event.register(new ESConfigTask(listener));
        }
    }


    public static <MSG extends CustomPacketPayload> void send(ServerPlayer player, MSG msg) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static <MSG extends CustomPacketPayload> void send(Collection<ServerPlayer> players, MSG msg) {
        players.forEach(player -> send(player, msg));
    }

}

package com.teamtea.eclipticseasons.compat.teacon;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class TeaconNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(EclipticSeasons.NETWORK_VERSION);
        registrar.playToClient(
                TeaconMessage.TYPE,
                TeaconMessage.STREAM_CODEC,
                (teaconMessage, iPayloadContext) -> {
                    TeaconCheckTool.teacon = teaconMessage.teacon;
                }
        );

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

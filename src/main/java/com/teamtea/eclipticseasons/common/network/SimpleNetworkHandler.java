package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.network.message.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public final class SimpleNetworkHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(EclipticSeasons.rl("main"))
            .networkProtocolVersion(() -> EclipticSeasons.NETWORK_VERSION)
            .serverAcceptedVersions(EclipticSeasons.NETWORK_VERSION::equals)
            .clientAcceptedVersions(EclipticSeasons.NETWORK_VERSION::equals)
            .simpleChannel();

    public static void init() {
        int id = 0;
        // registerMessage(id++, SolarTermsMessage.class, SolarTermsMessage::new);
        // registerMessage(id++, BiomeWeatherMessage.class, BiomeWeatherMessage::new);
        var a = CHANNEL.messageBuilder(SolarTermsMessage.class, id++)
                .encoder(SolarTermsMessage::toBytes)
                .decoder(SolarTermsMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            a.consumerNetworkThread(NetworkUtil::processSolarTermsMessage);
        a.add();
        
        var c = CHANNEL.messageBuilder(BiomeWeatherMessage.class, id++)
                .encoder(BiomeWeatherMessage::toBytes)
                .decoder(BiomeWeatherMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            c.consumerNetworkThread(NetworkUtil::processBiomeWeatherMessage);
        c.add();


        var d = CHANNEL.messageBuilder(EmptyMessage.class, id++)
                .encoder(EmptyMessage::toBytes)
                .decoder(EmptyMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            d.consumerNetworkThread(NetworkUtil::processEmptyMessage);
        d.add();

        var e = CHANNEL.messageBuilder(BroomUseMessage.class, id++)
                .encoder(BroomUseMessage::toBytes)
                .decoder(BroomUseMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            e.consumerNetworkThread(NetworkUtil::processBroomUseMessage);
        e.add();

        var f = CHANNEL.messageBuilder(DataPackEventMessage.class, id++)
                .encoder(DataPackEventMessage::toBytes)
                .decoder(DataPackEventMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            f.consumerNetworkThread(NetworkUtil::processDataPackEvent);
        f.add();

        var g = CHANNEL.messageBuilder(HumidModifyMessage.class, id++)
                .encoder(HumidModifyMessage::toBytes)
                .decoder(HumidModifyMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            g.consumerNetworkThread(NetworkUtil::processHumidModifyMessage);
        g.add();
    }

    private static void registerMessage(int i, Class<BiomeWeatherMessage> biomeWeatherMessageClass, Object o) {
    }

    // private static <T extends INormalMessage> void registerMessage(int index, Class<T> messageType, Function<FriendlyByteBuf, T> decoder) {
    //     CHANNEL.registerMessage(index, messageType, INormalMessage::toBytes, decoder, (message, context) ->
    //     {
    //
    //
    //         message.process(context);
    //         context.get().setPacketHandled(true);
    //     });
    // }


    public static <MSG> void send(ServerPlayer player, MSG msg) {
        SimpleNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void send(List<ServerPlayer> players, MSG msg) {
        players.forEach(player -> {
            // if (player instanceof ServerPlayer serverPlayer)
            {
                send(player, msg);
            }
        });
    }
}

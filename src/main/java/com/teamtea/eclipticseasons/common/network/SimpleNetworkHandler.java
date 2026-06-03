package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusHandler;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfoResolver;
import com.teamtea.eclipticseasons.common.network.clientmesage.GrowthInfoQuery;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.config.sync.ESConfigFilePayload;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import com.teamtea.eclipticseasons.config.sync.ESConfigToServerPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;

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

        var e = CHANNEL.messageBuilder(SnowyStatusHandler.class, id++)
                .encoder(SnowyStatusHandler::write)
                .decoder(SnowyStatusHandler::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            e.consumerNetworkThread(SnowyStatusHandler::processSnowyStatusMessage);
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

        // var h = CHANNEL.messageBuilder(MapFixerMessage.class, id++)
        //         .encoder(MapFixerMessage::toBytes)
        //         .decoder(MapFixerMessage::new);
        // if (FMLLoader.getDist() == Dist.CLIENT)
        //     h.consumerNetworkThread(NetworkUtil::processMapFixerMessage);
        // h.add();

        var i = CHANNEL.messageBuilder(UpdateTempChangeMessage.class, id++)
                .encoder(UpdateTempChangeMessage::toBytes)
                .decoder(UpdateTempChangeMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            i.consumerNetworkThread(NetworkUtil::processUpdateTempChangeMessage);
        i.add();

        var j = CHANNEL.messageBuilder(ChunkBiomeUpdateMessage.class, id++)
                .encoder(ChunkBiomeUpdateMessage::toBytes)
                .decoder(ChunkBiomeUpdateMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            j.consumerNetworkThread(NetworkUtil::processChunkBiomeUpdateMessage);
        j.add();

        var k = CHANNEL.messageBuilder(ESConfigFilePayload.class, id++)
                .encoder(ESConfigFilePayload::encode)
                .decoder(ESConfigFilePayload::decodeExtend);
        if (FMLLoader.getDist() == Dist.CLIENT)
            k.consumerNetworkThread(NetworkUtil::processConfigInGame);
        k.add();


        var l = CHANNEL.messageBuilder(GrowthInfoMessage.class, id++)
                .encoder(GrowthInfoMessage::toBytes)
                .decoder(GrowthInfoMessage::new);
        if (FMLLoader.getDist() == Dist.CLIENT)
            l.consumerNetworkThread(NetworkUtil::handleGrowthInfoQuery);
        l.add();

        CHANNEL.messageBuilder(GrowthInfoQuery.class, id++)
                .encoder(GrowthInfoQuery::toBytes)
                .decoder(GrowthInfoQuery::new)
                .consumerNetworkThread((payload, xy) -> {
                    Player player = xy.get().getSender();
                    if (player instanceof ServerPlayer serverPlayer
                            && serverPlayer.level() instanceof ServerLevel level
                            && MapChecker.isLoadedOnlyServer(level, payload.getPos()))
                        send(serverPlayer, new GrowthInfoMessage(Optional.ofNullable(GrowthInfoResolver.resolve(
                                level, payload.getPos(), level.getBlockState(payload.getPos())
                        ))));
                })
                .add();

        CHANNEL.messageBuilder(ESConfigToServerPayload.class, id++)
                .encoder(ESConfigToServerPayload::encode)
                .decoder(ESConfigToServerPayload::decode)
                .consumerNetworkThread((x, xy) -> {
                    Player player = xy.get().getSender();
                    if (player instanceof ServerPlayer serverPlayer)
                        ESConfigSync.INSTANCE.syncToSever(x, serverPlayer);
                })
                .add();

        CHANNEL.messageBuilder(S2CConfigData.class, id++, NetworkDirection.LOGIN_TO_CLIENT).
                loginIndex(LoginIndexedMessage::getLoginIndex, LoginIndexedMessage::setLoginIndex).
                decoder(S2CConfigData::decode).
                encoder(S2CConfigData::encode).
                buildLoginPacketList(ESConfigSync.INSTANCE::syncConfigs).
                consumerNetworkThread(NetworkUtil::processConfigSync).
                add();

        CHANNEL.messageBuilder(C2SAcknowledge.class, 99, NetworkDirection.LOGIN_TO_SERVER).
                loginIndex(LoginIndexedMessage::getLoginIndex, LoginIndexedMessage::setLoginIndex).
                decoder(C2SAcknowledge::decode).
                encoder(C2SAcknowledge::encode).
                consumerNetworkThread(HandshakeHandler.indexFirst(NetworkUtil::handleClientAck)).
                add();
    }

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


    @Data
    public static class LoginIndexedMessage implements IntSupplier {
        protected int loginIndex;

        @Override
        public int getAsInt() {
            return getLoginIndex();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class S2CConfigData extends LoginIndexedMessage {
        protected final String fileName;
        protected final byte[] bytes;

        public void encode(final FriendlyByteBuf buffer) {
            buffer.writeUtf(this.fileName);
            buffer.writeByteArray(this.bytes);
        }

        public static S2CConfigData decode(final FriendlyByteBuf buffer) {
            return new S2CConfigData(buffer.readUtf(32767), buffer.readByteArray());
        }
    }

    public static class C2SAcknowledge extends LoginIndexedMessage {
        public void encode(FriendlyByteBuf buf) {

        }

        public static C2SAcknowledge decode(FriendlyByteBuf buf) {
            return new C2SAcknowledge();
        }
    }

}

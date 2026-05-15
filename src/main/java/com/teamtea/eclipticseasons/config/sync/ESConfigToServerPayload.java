package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ESConfigToServerPayload(String fileName, boolean restart,
                                      SyncType syncType,
                                      byte[] contents) implements CustomPacketPayload, IESConfigMessage {
    public static final Type<ESConfigToServerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EclipticSeasonsApi.MODID, "config_to_server"));


    public static final StreamCodec<FriendlyByteBuf, ESConfigToServerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ESConfigToServerPayload::fileName,
            ByteBufCodecs.BOOL,
            ESConfigToServerPayload::restart,
            SyncType.STREAM_CODEC,
            ESConfigToServerPayload::syncType,
            NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY,
            ESConfigToServerPayload::contents,
            ESConfigToServerPayload::new);

    @Override
    public  Type<ESConfigToServerPayload> type() {
        return TYPE;
    }
}

package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ESConfigToClientPayload(String fileName,
                                      byte[] contents) implements CustomPacketPayload, IESConfigMessage {
    public static final Type<ESConfigToClientPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EclipticSeasonsApi.MODID, "config_to_client"));

    public static final StreamCodec<FriendlyByteBuf, ESConfigToClientPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ESConfigToClientPayload::fileName,
            NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY,
            ESConfigToClientPayload::contents,
            ESConfigToClientPayload::new);

    @Override
    public Type<ESConfigToClientPayload> type() {
        return TYPE;
    }
}

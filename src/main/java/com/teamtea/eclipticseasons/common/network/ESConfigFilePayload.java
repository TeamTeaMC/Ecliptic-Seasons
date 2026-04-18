package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jspecify.annotations.NonNull;

public record ESConfigFilePayload(String fileName, byte[] contents) implements CustomPacketPayload {
    public static final Type<ESConfigFilePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(EclipticSeasonsApi.MODID, "config_file"));
    public static final StreamCodec<FriendlyByteBuf, ESConfigFilePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ESConfigFilePayload::fileName,
            NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY,
            ESConfigFilePayload::contents,
            ESConfigFilePayload::new);

    @Override
    public @NonNull Type<ESConfigFilePayload> type() {
        return TYPE;
    }
}

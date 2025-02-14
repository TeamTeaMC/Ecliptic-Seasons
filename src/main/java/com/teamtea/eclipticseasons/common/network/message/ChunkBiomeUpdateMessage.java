package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ChunkBiomeUpdateMessage implements CustomPacketPayload {
    public final int[] biomes;
    public final int x;
    public final int z;
    public final int version;

    public ChunkBiomeUpdateMessage(int[] biomes, int x, int z, int version) {
        this.biomes = biomes;
        this.x = x;
        this.z = z;
        this.version = version;
    }

    public static final Type<ChunkBiomeUpdateMessage> TYPE = new Type<>(EclipticSeasons.rl("chunk_biome"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, ChunkBiomeUpdateMessage> STREAM_CODEC = StreamCodec.composite(
            MessageCodec.intArrayStreamCodec,
            chunkUpdateMessage -> chunkUpdateMessage.biomes,
            ByteBufCodecs.VAR_INT,
            chunkUpdateMessage -> chunkUpdateMessage.x,
            ByteBufCodecs.VAR_INT,
            chunkUpdateMessage -> chunkUpdateMessage.z,
            ByteBufCodecs.VAR_INT,
            chunkUpdateMessage -> chunkUpdateMessage.version,
            ChunkBiomeUpdateMessage::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

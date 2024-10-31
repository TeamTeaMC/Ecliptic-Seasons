package com.teamtea.eclipticseasons.common.network;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public class ChunkUpdateMessage implements CustomPacketPayload {
    public final byte[] snowyArea;
    public final int x;
    public final int z;
    public final List<Integer> y;
    public final List<BlockPos> blockPosList;

    public ChunkUpdateMessage(byte[] snowyArea, int x, int z, List<Integer> y, List<BlockPos> blockPosList) {
        this.snowyArea = snowyArea;
        this.x = x;
        this.z = z;
        this.y = y;
        this.blockPosList = blockPosList;
    }

    public static final Type<ChunkUpdateMessage> TYPE = new Type<>(EclipticSeasons.rl("chunk_snow"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, ChunkUpdateMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.byteArray(256),
            solarTermsMessage -> solarTermsMessage.snowyArea,
            ByteBufCodecs.VAR_INT,
            solarTermsMessage -> solarTermsMessage.x,
            ByteBufCodecs.VAR_INT,
            solarTermsMessage -> solarTermsMessage.z,
            new StreamCodec<ByteBuf, List<Integer>>() {
                @Override
                public void encode(ByteBuf pBuffer, List<Integer> pValue) {
                    pBuffer.writeInt(pValue.size());
                    for (Integer i : pValue) {
                        pBuffer.writeInt(i);
                    }
                }

                @Override
                public List<Integer> decode(ByteBuf pBuffer) {
                    int size = pBuffer.readInt();
                    ArrayList<Integer> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(pBuffer.readInt());
                    }
                    return list;
                }
            },
            solarTermsMessage -> solarTermsMessage.y,
            new StreamCodec<ByteBuf, List<BlockPos>>() {
                @Override
                public void encode(ByteBuf pBuffer, List<BlockPos> pValue) {
                    pBuffer.writeInt(pValue.size());
                    for (BlockPos i : pValue) {
                        BlockPos.STREAM_CODEC.encode(pBuffer, i);
                    }
                }

                @Override
                public List<BlockPos> decode(ByteBuf pBuffer) {
                    int size = pBuffer.readInt();
                    ArrayList<BlockPos> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(BlockPos.STREAM_CODEC.decode(pBuffer));
                    }
                    return list;
                }
            },
            solarTermsMessage -> solarTermsMessage.blockPosList,
            ChunkUpdateMessage::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

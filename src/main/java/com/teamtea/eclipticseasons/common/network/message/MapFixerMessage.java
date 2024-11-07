package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// y 表示起点y
public class MapFixerMessage implements CustomPacketPayload {
    public final List<Integer> startYList;
    public final List<BlockPos> blockPosList;

    public MapFixerMessage(List<BlockPos> blockPosList, List<Integer> startYList) {
        this.blockPosList = blockPosList;
        this.startYList = startYList;
    }

    public static final Type<MapFixerMessage> TYPE = new Type<>(EclipticSeasons.rl("map_fixer"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, MapFixerMessage> STREAM_CODEC = StreamCodec.composite(
            MessageCodec.poslistStreamCodec,
            solarTermsMessage -> solarTermsMessage.blockPosList,
            MessageCodec.intlistStreamCodec,
            chunkUpdateMessage -> chunkUpdateMessage.startYList,
            MapFixerMessage::new
    );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

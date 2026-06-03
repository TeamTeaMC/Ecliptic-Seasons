package com.teamtea.eclipticseasons.common.network.clientmesage;

import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Data
public class GrowthInfoQuery implements CustomPacketPayload {

    private final BlockPos pos;

    public static final Type<GrowthInfoQuery> TYPE = new Type<>(EclipticSeasons.rl("growth_info_query"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GrowthInfoQuery> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            GrowthInfoQuery::getPos,
            GrowthInfoQuery::new
    );

    @Override
    public  Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


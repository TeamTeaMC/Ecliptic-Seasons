package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class EmptyMessage implements CustomPacketPayload {

    public boolean use = true;

    public EmptyMessage(boolean use) {
        this.use = use;
    }

    public EmptyMessage() {

    }

    public static final Type<EmptyMessage> TYPE = new Type<>(EclipticSeasons.rl("empty"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, EmptyMessage> STREAM_CODEC =
            StreamCodec.unit(new EmptyMessage());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

}

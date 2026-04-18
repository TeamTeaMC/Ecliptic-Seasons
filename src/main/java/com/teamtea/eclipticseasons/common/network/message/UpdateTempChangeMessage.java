package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public  class UpdateTempChangeMessage implements CustomPacketPayload {
    public final float change;

    public static final Type<UpdateTempChangeMessage> TYPE = new Type<>(EclipticSeasons.rl("temp_change_update"));

    public static final StreamCodec<ByteBuf, UpdateTempChangeMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            broomUseMessage -> broomUseMessage.change,
            UpdateTempChangeMessage::new
    );

    public UpdateTempChangeMessage(float change) {
        this.change = change;
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}

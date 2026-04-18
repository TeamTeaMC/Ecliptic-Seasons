package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class HumidModifyMessage implements CustomPacketPayload {
    public final BlockPos blockPos;
    public final float value;

    public static final Type<HumidModifyMessage> TYPE = new Type<>(EclipticSeasons.rl("humidity"));

    public static final StreamCodec<ByteBuf, HumidModifyMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            message -> message.blockPos,
            ByteBufCodecs.FLOAT,
            message -> message.value,
            HumidModifyMessage::new
    );

    public HumidModifyMessage(BlockPos blockPos, float time) {
        this.blockPos = blockPos;
        this.value = time;
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}

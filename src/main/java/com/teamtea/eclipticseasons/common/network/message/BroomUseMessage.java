package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class BroomUseMessage implements CustomPacketPayload {
    public final BlockPos blockPos;
    public final long time;

    public static final Type<BroomUseMessage> TYPE = new Type<>(EclipticSeasons.rl("broom_use"));

    public static final StreamCodec<ByteBuf, BroomUseMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            broomUseMessage -> broomUseMessage.blockPos,
            ByteBufCodecs.VAR_LONG,
            message -> message.time,
            BroomUseMessage::new
    );

    public BroomUseMessage(BlockPos blockPos, long time) {
        this.blockPos = blockPos;
        this.time = time;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}

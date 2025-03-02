package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public final class BroomUseMessage {
    public final BlockPos blockPos;
    public final long time;


    public BroomUseMessage(BlockPos blockPos, long time) {
        this.blockPos = blockPos;
        this.time = time;
    }

    public BroomUseMessage(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        time = buf.readVarLong();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeVarLong(time);
    }
}

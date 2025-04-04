package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public final class HumidModifyMessage {
    public final BlockPos blockPos;
    public final int value;


    public HumidModifyMessage(BlockPos blockPos, int time) {
        this.blockPos = blockPos;
        this.value = time;
    }

    public HumidModifyMessage(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        value = buf.readVarInt();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeVarInt(value);
    }
}

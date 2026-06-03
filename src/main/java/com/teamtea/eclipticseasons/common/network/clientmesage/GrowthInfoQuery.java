package com.teamtea.eclipticseasons.common.network.clientmesage;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class GrowthInfoQuery {

    @Getter
    private final BlockPos pos;

    public GrowthInfoQuery(BlockPos pos) {
        this.pos = pos;
    }

    public GrowthInfoQuery(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}


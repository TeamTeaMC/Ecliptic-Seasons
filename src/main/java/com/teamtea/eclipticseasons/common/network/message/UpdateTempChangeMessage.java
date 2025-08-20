package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.network.FriendlyByteBuf;

public class UpdateTempChangeMessage {
    public float change;

    public UpdateTempChangeMessage(float change) {
        this.change = change;
    }

    public UpdateTempChangeMessage(FriendlyByteBuf buf) {
        change = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(change);
    }

}

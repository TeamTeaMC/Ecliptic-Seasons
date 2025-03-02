package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.network.FriendlyByteBuf;

public class SolarTermsMessage {
    public int solarDay;

    public SolarTermsMessage(int solarDay) {
        this.solarDay = solarDay;
    }

    public SolarTermsMessage(FriendlyByteBuf buf) {
        solarDay = buf.readInt();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(solarDay);
    }


}

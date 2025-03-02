package com.teamtea.eclipticseasons.common.network.message;



import net.minecraft.network.FriendlyByteBuf;

public class BiomeWeatherMessage {
    public final byte[] rain;
    public final byte[] thuder;
    public final byte[] clear;
    public final byte[] snowDepth;

    public BiomeWeatherMessage(FriendlyByteBuf buf) {
        rain = buf.readByteArray();
        thuder = buf.readByteArray();
        clear = buf.readByteArray();
        snowDepth = buf.readByteArray();
    }

    public BiomeWeatherMessage(byte[] rain, byte[] thuder, byte[] clear, byte[] snowDepth) {
        this.rain = rain;
        this.thuder = thuder;
        this.clear = clear;
        this.snowDepth = snowDepth;
    }



    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByteArray(rain);
        buf.writeByteArray(thuder);
        buf.writeByteArray(clear);
        buf.writeByteArray(snowDepth);
    }


}

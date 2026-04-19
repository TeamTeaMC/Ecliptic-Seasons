package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.network.FriendlyByteBuf;

public class BiomeWeatherMessage {
    public final byte[] snowDepth;
    public final int[] special;
    public final int[] weather;

    public BiomeWeatherMessage(FriendlyByteBuf buf) {
        snowDepth = buf.readByteArray();
        special = buf.readVarIntArray();
        weather = buf.readVarIntArray();
    }

    public BiomeWeatherMessage( byte[] snowDepth, int[] special, int[] weather) {
        this.snowDepth = snowDepth;
        this.special = special;
        this.weather = weather;
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByteArray(snowDepth);
        buf.writeVarIntArray(special);
        buf.writeVarIntArray(weather);
    }

}

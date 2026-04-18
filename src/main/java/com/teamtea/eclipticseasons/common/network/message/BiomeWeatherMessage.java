package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public class BiomeWeatherMessage implements CustomPacketPayload {
    public final byte[] rain;
    public final byte[] thuder;
    public final byte[] clear;
    public final byte[] snowDepth;
    public final int[] special;
    public final int[] weather;

    public BiomeWeatherMessage(byte[] rain, byte[] thuder, byte[] clear, byte[] snowDepth, int[] special, int[] weather) {
        this.rain = rain;
        this.thuder = thuder;
        this.clear = clear;
        this.snowDepth = snowDepth;
        this.special = special;
        this.weather = weather;
    }

    public static final Type<BiomeWeatherMessage> TYPE = new Type<>(EclipticSeasons.rl("biomes_weather"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, BiomeWeatherMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.byteArray(1024),
            solarTermsMessage -> solarTermsMessage.rain,
            ByteBufCodecs.byteArray(1024),
            solarTermsMessage -> solarTermsMessage.thuder,
            ByteBufCodecs.byteArray(1024),
            solarTermsMessage -> solarTermsMessage.clear,
            ByteBufCodecs.byteArray(1024),
            solarTermsMessage -> solarTermsMessage.snowDepth,
            MessageCodec.intArrayStreamCodec,
            solarTermsMessage -> solarTermsMessage.special,
            MessageCodec.intArrayStreamCodec,
            solarTermsMessage -> solarTermsMessage.weather,
            BiomeWeatherMessage::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

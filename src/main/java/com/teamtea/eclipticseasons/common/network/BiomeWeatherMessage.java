package com.teamtea.eclipticseasons.common.network;



import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class BiomeWeatherMessage  implements CustomPacketPayload {
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

    public static final CustomPacketPayload.Type<BiomeWeatherMessage> TYPE = new CustomPacketPayload.Type<>(EclipticSeasons.rl("biomes_weather"));

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
            BiomeWeatherMessage::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

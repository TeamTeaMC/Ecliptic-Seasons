package com.teamtea.eclipticseasons.common.network.message;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import lombok.Data;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

@Data
public class GrowthInfoMessage implements CustomPacketPayload {
    private final Optional<GrowthInfo> info;

    public static final Type<GrowthInfoMessage> TYPE = new Type<>(EclipticSeasons.rl("growth_info_result"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<RegistryFriendlyByteBuf, GrowthInfoMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(GrowthInfo.STREAM_CODEC),
            GrowthInfoMessage::getInfo,
            GrowthInfoMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

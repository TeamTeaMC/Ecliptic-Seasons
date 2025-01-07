package com.teamtea.eclipticseasons.common.network.message;

import com.teamtea.eclipticseasons.EclipticSeasons;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ConfigMessage(List<ResourceKey<Level>> SeasonalDimensions) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigMessage> TYPE = new CustomPacketPayload.Type<>(EclipticSeasons.rl("config"));
    public static final StreamCodec<ByteBuf, ConfigMessage> STREAM_CODEC =
            StreamCodec.composite(
                    MessageCodec.dimensionKeysStreamCodec,
                    ConfigMessage::SeasonalDimensions,
                    ConfigMessage::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}

package com.teamtea.eclipticseasons.common.item.info;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

@Data
@Accessors(fluent = true)
public class GrowthInfo {
    @NonNull
    final BlockPos pos;

    @NonNull
    final Component cropName;

    final int greenhouseLevel;
    final float growChance;
    final boolean needsSeasonCore;
    final boolean humidityMismatch;

    boolean waitingForServer = false;

    public GrowthInfo(
            @NonNull Component cropName,
            @NonNull BlockPos pos
    ) {
        this(pos, cropName, 0, 0.0F, false, false);
        this.waitingForServer = true;
    }

    @Builder
    public GrowthInfo(
            @NonNull BlockPos pos,
            @NonNull Component cropName,
            int greenhouseLevel,
            float growChance,
            boolean needsSeasonCore,
            boolean humidityMismatch
    ) {
        this.pos = pos;
        this.cropName = cropName;
        this.greenhouseLevel = greenhouseLevel;
        this.growChance = growChance;
        this.needsSeasonCore = needsSeasonCore;
        this.humidityMismatch = humidityMismatch;
        this.waitingForServer = false;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, GrowthInfo> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    GrowthInfo::pos,
                    ComponentSerialization.STREAM_CODEC,
                    GrowthInfo::cropName,
                    ByteBufCodecs.VAR_INT,
                    GrowthInfo::greenhouseLevel,
                    ByteBufCodecs.FLOAT,
                    GrowthInfo::growChance,
                    ByteBufCodecs.BOOL,
                    GrowthInfo::needsSeasonCore,
                    ByteBufCodecs.BOOL,
                    GrowthInfo::humidityMismatch,
                    GrowthInfo::new
            );
}
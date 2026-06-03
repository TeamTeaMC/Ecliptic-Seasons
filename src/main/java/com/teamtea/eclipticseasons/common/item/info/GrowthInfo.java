package com.teamtea.eclipticseasons.common.item.info;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GrowthInfo(
        BlockPos pos,
        // BlockState state,
        Component cropName,

        // @Nullable Holder<AgroClimaticZone> agroClimaticZone,

        // boolean greenhouse,
        int greenhouseLevel,

        float growChance,
        // int growChanceLevel,

        boolean needsSeasonCore,
        boolean humidityMismatch

        // float humidity,
        // @Nullable List<Season> likedSeasons,
        // @Nullable List<Humidity> likedHumidity
) {


    public static final StreamCodec<RegistryFriendlyByteBuf, GrowthInfo> STREAM_CODEC = StreamCodec.composite(
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
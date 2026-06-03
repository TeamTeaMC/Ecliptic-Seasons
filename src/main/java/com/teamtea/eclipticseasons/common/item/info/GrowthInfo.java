package com.teamtea.eclipticseasons.common.item.info;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(Component.Serializer.toJson(cropName));
        buf.writeInt(greenhouseLevel);
        buf.writeFloat(growChance);
        buf.writeBoolean(needsSeasonCore);
        buf.writeBoolean(humidityMismatch);
    }

    public static GrowthInfo fromBytes(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Component cropName = Component.Serializer.fromJson(buf.readUtf());
        int greenhouseLevel = buf.readInt();
        float growChance = buf.readFloat();
        boolean needsSeasonCore = buf.readBoolean();
        boolean humidityMismatch = buf.readBoolean();

        return new GrowthInfo(
                pos,
                cropName,
                greenhouseLevel,
                growChance,
                needsSeasonCore,
                humidityMismatch
        );
    }
}
package com.teamtea.eclipticseasons.common.item.info;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Data
@Accessors(fluent = true)
public class GrowthInfo {
    @NotNull
    final BlockPos pos;

    @NotNull
    final Component cropName;

    final int greenhouseLevel;
    final float growChance;
    final boolean needsSeasonCore;
    final boolean humidityMismatch;

    boolean waitingForServer = false;

    public GrowthInfo(
            @NotNull Component cropName,
            @NotNull BlockPos pos
    ) {
        this(pos, cropName, 0, 0.0F, false, false);
        this.waitingForServer = true;
    }

    @Builder
    public GrowthInfo(
            @NotNull BlockPos pos,
            @NotNull Component cropName,
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
                cropName == null ? Component.empty() : cropName,
                greenhouseLevel,
                growChance,
                needsSeasonCore,
                humidityMismatch
        );
    }
}
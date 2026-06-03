package com.teamtea.eclipticseasons.common.network.message;

import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public class GrowthInfoMessage {
    public final Optional<GrowthInfo> info;

    public GrowthInfoMessage(FriendlyByteBuf buf) {
        info = buf.readOptional(GrowthInfo::fromBytes);
    }

    public GrowthInfoMessage(Optional<GrowthInfo> info) {
        this.info = info;
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeOptional(this.info, (friendlyByteBuf, info1) -> info1.toBytes(friendlyByteBuf));
    }
}

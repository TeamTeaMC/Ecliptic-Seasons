package com.teamtea.eclipticseasons.config.sync;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;

@Data
@Accessors(fluent = true)
public class ESConfigToServerPayload{
    final String fileName;
    final boolean restart;
    final SyncType syncType;
    final byte[] contents;


    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeUtf(this.fileName);
        buffer.writeBoolean(restart);
        buffer.writeEnum(syncType);
        buffer.writeByteArray(this.contents);
    }

    public static ESConfigToServerPayload decode(final FriendlyByteBuf buffer) {
        return new ESConfigToServerPayload(
                buffer.readUtf(32767),
                buffer.readBoolean(),
                buffer.readEnum(SyncType.class),
                buffer.readByteArray());
    }
}

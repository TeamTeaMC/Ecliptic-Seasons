package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.FriendlyByteBuf;

@EqualsAndHashCode(callSuper = true)
@Data
public class ESConfigFilePayload extends SimpleNetworkHandler.S2CConfigData {
    public ESConfigFilePayload(String fileName, byte[] contents) {
        super(fileName, contents);
    }

    public static ESConfigFilePayload decodeExtend(final FriendlyByteBuf buffer) {
        return new ESConfigFilePayload(buffer.readUtf(32767), buffer.readByteArray());
    }
}

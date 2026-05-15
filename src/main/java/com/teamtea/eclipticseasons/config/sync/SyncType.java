package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.util.SpecUtil;
import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.StringUtils;

import java.util.Locale;

public enum SyncType {

    COMMON,
    CLIENT(false),
    // SERVER,
    // STARTUP,
    MIXINS,
    NONE(false);

    // public static final StreamCodec<ByteBuf, SyncType> STREAM_CODEC = new StreamCodec<>() {
    //     public SyncType decode(ByteBuf input) {
    //         return SyncType.values()[input.readByte()];
    //     }
    //
    //     public void encode(ByteBuf output, SyncType value) {
    //         output.writeByte(value.ordinal());
    //     }
    // };

    @Getter
    final boolean shouldSync;

    SyncType(boolean shouldSync) {
        this.shouldSync = shouldSync;
    }

    SyncType() {
        this(true);
    }


    boolean custom() {
        return this == MIXINS || this == NONE;
    }

    public String extension() {
        return StringUtils.toLowerCase(name());
    }

    public String configName(String modId) {
        return String.format(Locale.ROOT, "%s-%s.toml", modId, extension());
    }

    public static SyncType getTypeFrom(ForgeConfigSpec.ConfigValue<?> configValue) {
        ForgeConfigSpec.ValueSpec spec = SpecUtil.getSpec(configValue);
        if (CommonConfig.COMMON_CONFIG.getSpec().get(configValue.getPath()) == spec) {
            return COMMON;
        } else if (ClientConfig.CLIENT_CONFIG.getSpec().get(configValue.getPath()) == spec) {
            return CLIENT;
        }
        return NONE;
    }

    public static SyncType of(ModConfig.Type type) {
        return valueOf(type.toString());
    }
}

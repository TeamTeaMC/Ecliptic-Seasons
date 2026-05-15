package com.teamtea.eclipticseasons.config.sync;

import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.StartConfig;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.StringUtils;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SyncType {

    COMMON,
    CLIENT(false),
    // SERVER,
    STARTUP,
    MIXINS,
    NONE(false);

    public static final StreamCodec<ByteBuf, SyncType> STREAM_CODEC = new StreamCodec<>() {
        public @NotNull SyncType decode(ByteBuf input) {
            return SyncType.values()[input.readByte()];
        }

        public void encode(ByteBuf output, SyncType value) {
            output.writeByte(value.ordinal());
        }
    };

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

    public static SyncType getTypeFrom(ModConfigSpec.ConfigValue<?> configValue) {
        if (CommonConfig.COMMON_CONFIG.getSpec().get(configValue.getPath()) == configValue.getSpec()) {
            return COMMON;
        } else if (StartConfig.START_CONFIG.getSpec().get(configValue.getPath()) == configValue.getSpec()) {
            return STARTUP;
        } else if (ClientConfig.CLIENT_CONFIG.getSpec().get(configValue.getPath()) == configValue.getSpec()) {
            return CLIENT;
        }
        return NONE;
    }

    public static SyncType of(ModConfig.Type type) {
        return valueOf(type.toString());
    }
}

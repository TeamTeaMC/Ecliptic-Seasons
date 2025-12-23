package com.teamtea.eclipticseasons.api.util.serializer;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface SerializerHolder {
    @Nullable
    CompoundTag getCacheTag();

    void setCacheTag(@Nullable CompoundTag cacheTag);

    default void setChanged() {
        setCacheTag(null);
    }
}

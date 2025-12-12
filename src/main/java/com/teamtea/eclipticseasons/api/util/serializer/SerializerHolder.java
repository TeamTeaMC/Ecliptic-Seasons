package com.teamtea.eclipticseasons.api.util.serializer;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public interface SerializerHolder<T extends SerializerHolder<T>> extends ICapabilityProvider, INBTSerializable<CompoundTag> {
    @Nullable
    CompoundTag getCacheTag();

    void setCacheTag(@Nullable CompoundTag cacheTag);

    default void setChanged() {
        setCacheTag(null);
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    Codec<T> codec();

    default boolean isEnabled(){return true;};

    @Override
    default CompoundTag serializeNBT() {
        if (isEnabled()) {
            if (getCacheTag() != null) return getCacheTag();
            Level level = WeatherManager.fetchLevelIfNull(null);
            if (level != null) {
                RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
                Optional<Tag> result = codec().encodeStart(registryOps, self()).result();
                if (result.orElse(null) instanceof CompoundTag compoundTag) {
                    setCacheTag(compoundTag);
                    return compoundTag;
                }
            }
        }
        return new CompoundTag();
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (!isEnabled()) return;
        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null) {
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            Optional<T> result = codec().parse(registryOps, nbt).result();
            result.ifPresent(this::copyFrom);
        }
    }

    void copyFrom(T newHolder);

    Capability<T> getCapabilityName();

    LazyOptional<T> getCast();

    void setCast(LazyOptional<T> cast);

    @Override
    default @NotNull <CT> LazyOptional<CT> getCapability(@NotNull Capability<CT> cap, Direction side) {
        if (cap == getCapabilityName()) {
            if (getCast() == null) setCast(LazyOptional.of(this::self));
            return getCast().cast();
        }
        return LazyOptional.empty();
    }
}

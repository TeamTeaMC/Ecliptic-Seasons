package com.teamtea.eclipticseasons.api.util.serializer;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public record ESCommonSerializer<T extends SerializerHolder>
        (Codec<T> codec, Supplier<T> empty) implements IAttachmentSerializer<Tag, T> {

    @Override
    public @NotNull T read(@NotNull IAttachmentHolder holder, @NotNull Tag tag, HolderLookup.@NotNull Provider provider) {
        Optional<T> result = codec.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
        return result.orElseGet(empty);
    }

    @Override
    public Tag write(@NotNull T attachment, HolderLookup.@NotNull Provider provider) {
        if (attachment.getCacheTag() != null) {
            return attachment.getCacheTag();
        }
        Optional<Tag> result = codec.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment).result();
        if (result.orElse(null) instanceof CompoundTag compoundTag) {
            attachment.setCacheTag(compoundTag);
            return compoundTag;
        }
        return new CompoundTag();
    }
}
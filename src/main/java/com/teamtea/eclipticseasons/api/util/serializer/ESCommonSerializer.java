package com.teamtea.eclipticseasons.api.util.serializer;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.Supplier;

public record ESCommonSerializer<T extends SerializerHolder>
        (String id, Codec<T> codec, Supplier<T> empty) implements IAttachmentSerializer<@NonNull T> {


    @Override
    public T read(@NonNull IAttachmentHolder holder, ValueInput input) {
        Optional<T> snowyStatus = input.read(id, codec);
        return snowyStatus.orElseGet(empty);
    }

    @Override
    public boolean write(T attachment, ValueOutput output) {
        output.storeNullable(id, codec, attachment);
        return true;
    }

    //@Override
    //public @NonNull T read(@NonNull IAttachmentHolder holder, @NonNull Tag tag, HolderLookup.@NonNull Provider provider) {
    //    Optional<T> result = codec.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
    //    return result.orElseGet(empty);
    //}
    //
    //@Override
    //public Tag write(@NonNull T attachment, HolderLookup.@NonNull Provider provider) {
    //    if (attachment.getCacheTag() != null) {
    //        return attachment.getCacheTag();
    //    }
    //    Optional<Tag> result = codec.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment).result();
    //    if (result.orElse(null) instanceof CompoundTag compoundTag) {
    //        attachment.setCacheTag(compoundTag);
    //        return compoundTag;
    //    }
    //    return new CompoundTag();
    //}

}
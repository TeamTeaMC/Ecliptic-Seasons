package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record RecursiveObject(List<RecursiveObject> inner) { /* ... */

    public static final Codec<RecursiveObject> RECURSIVE_CODEC = Codec.recursive(
            RecursiveObject.class.getSimpleName(), // This is for the toString method
            recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
                    recursedCodec.listOf().fieldOf("inner").forGetter(RecursiveObject::inner)
            ).apply(instance, RecursiveObject::new))
    );
}



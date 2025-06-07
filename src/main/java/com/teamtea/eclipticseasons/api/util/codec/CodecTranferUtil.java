package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.HolderLookup;

import java.util.function.Consumer;

public class CodecTranferUtil {

    public static <T, R>  void decode(HolderLookup.Provider registries,
                              DynamicOps<R> ops,
                              Codec<T> codec,
                              R orange,
                              Consumer<T> consumer) {
        DynamicOps<R> dynamicops = registries.createSerializationContext(ops);
        codec.parse(dynamicops, orange)
                .resultOrPartial(EclipticSeasons::logger)
                .ifPresent(consumer)
        ;
    }

    public static<T, R> void encode(HolderLookup.Provider registries,
                              DynamicOps<R> ops,
                              Codec<T> codec,
                              T orange,
                              Consumer<R> consumer) {
        DynamicOps<R> dynamicops = registries.createSerializationContext(ops);
        codec
                .encodeStart(dynamicops, orange)
                .resultOrPartial(EclipticSeasons::logger)
                .ifPresent(consumer);
    }
}

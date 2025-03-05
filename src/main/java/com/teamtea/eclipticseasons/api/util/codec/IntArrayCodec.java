package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

public class IntArrayCodec implements Codec<int[]> {

    private IntArrayCodec() {
    }

    @Override
    public <T> DataResult<Pair<int[], T>> decode(DynamicOps<T> ops, T input) {
        return ops.getIntStream(input).setLifecycle(Lifecycle.stable()).flatMap(
                intStream -> DataResult.success(Pair.of(intStream.toArray(), input), Lifecycle.stable())
        );
    }

    @Override
    public <T> DataResult<T> encode(int[] input, DynamicOps<T> ops, T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();
        for (final int e : input) {
            builder.add(Codec.INT.encodeStart(ops, e));
        }
        return builder.build(prefix);
    }

    public static final Codec<int[]> CODEC = new IntArrayCodec();


}

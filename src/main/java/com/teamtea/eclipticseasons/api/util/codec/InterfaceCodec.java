package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public record InterfaceCodec<I, F extends I, S extends I>(
        Codec<F> first,
        Class<F> a
        , Codec<S> second, Class<S> b) implements Codec<I> {
    @Override
    public <T> DataResult<Pair<I, T>> decode(final DynamicOps<T> ops, final T input) {
        final DataResult<Pair<I, T>> firstRead = first.decode(ops, input).map(vo -> Pair.of(vo.getFirst(), vo.getSecond()));
        if (firstRead.isSuccess()) {
            return firstRead;
        }
        final DataResult<Pair<I, T>> secondRead = second.decode(ops, input).map(vo -> Pair.of(vo.getFirst(), vo.getSecond()));
        if (secondRead.isSuccess()) {
            return secondRead;
        }
        if (firstRead.hasResultOrPartial()) {
            return firstRead;
        }
        if (secondRead.hasResultOrPartial()) {
            return secondRead;
        }
        return DataResult.error(() -> "Failed to parse either. First: " + firstRead.error().orElseThrow().message() + "; Second: " + secondRead.error().orElseThrow().message());
    }

    @Override
    public <T> DataResult<T> encode(final I input, final DynamicOps<T> ops, final T prefix) {
        if (a.isInstance(input)) {
            F f = a.cast(input);
            return first.encode(f, ops, prefix);
        } else if (b.isInstance(input)) {
            S s = b.cast(input);
            return second.encode(s, ops, prefix);
        } else {
            return DataResult.error(() -> "Unsupported type: " + input.getClass().getSimpleName());
        }
    }

}

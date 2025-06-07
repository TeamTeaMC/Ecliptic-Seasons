package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.BaseMapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.stream.Stream;

public record IdentityHashCodec<K, V>(
        Codec<K> keyCodec,
        Codec<V> elementCodec
) implements BaseMapCodec<K, V>, Codec<IdentityHashMap<K, V>> {

    @Override
    public <T> DataResult<Pair<IdentityHashMap<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input)
                .setLifecycle(Lifecycle.stable())
                .flatMap(map -> decode2(ops, map))
                .map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(final IdentityHashMap<K, V> input, final DynamicOps<T> ops, final T prefix) {
        return encode(input, ops, new IdentityMapBuilder<>(ops)).build(prefix);
    }

    @Override
    public String toString() {
        return "IdentityHashCodec[" + keyCodec + " -> " + elementCodec + ']';
    }

    public <T> DataResult<IdentityHashMap<K, V>> decode2(final DynamicOps<T> ops, final MapLike<T> input) {
        final Object2ObjectMap<K, V> read = new Object2ObjectArrayMap<>();
        final Stream.Builder<Pair<T, T>> failed = Stream.builder();

        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (r, pair) -> {
                    final DataResult<K> key = keyCodec().parse(ops, pair.getFirst());
                    final DataResult<V> value = elementCodec().parse(ops, pair.getSecond());

                    final DataResult<Pair<K, V>> entryResult = key.apply2stable(Pair::of, value);
                    final Optional<Pair<K, V>> entry = entryResult.resultOrPartial();
                    if (entry.isPresent()) {
                        final V existingValue = read.putIfAbsent(entry.get().getFirst(), entry.get().getSecond());
                        if (existingValue != null) {
                            failed.add(pair);
                            return r.apply2stable((u, p) -> u, DataResult.error(() -> "Duplicate entry for key: '" + entry.get().getFirst() + "'"));
                        }
                    }
                    if (entryResult.isError()) {
                        failed.add(pair);
                    }

                    return r.apply2stable((u, p) -> u, entryResult);
                },
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        // We need identity map to speed the query
        final IdentityHashMap<K, V> elements = new IdentityHashMap<>(read);
        final T errors = ops.createMap(failed.build());

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
    }

    static final class IdentityMapBuilder<T> extends RecordBuilder.AbstractUniversalBuilder<T, IdentityHashMap<T, T>> {
        public IdentityMapBuilder(final DynamicOps<T> ops) {
            super(ops);
        }

        @Override
        protected IdentityHashMap<T, T> initBuilder() {
            return new IdentityHashMap<>();
        }

        @Override
        protected DataResult<T> build(IdentityHashMap<T, T> builder, T prefix) {
            return ops().mergeToMap(prefix, builder);
        }

        @Override
        protected IdentityHashMap<T, T> append(T key, T value, IdentityHashMap<T, T> builder) {
            builder.put(key, value);
            return builder;
        }


    }
}

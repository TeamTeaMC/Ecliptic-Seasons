package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@ApiStatus.Internal
public class CodecUtil {

    /**
     * By xiaogu
     **/
    public static <K, V> Codec<Pair<K, V>> pairCodec(String nkey, Codec<K> key, String nval, Codec<V> val) {
        return RecordCodecBuilder.create(t -> t.group(
                key.fieldOf(nkey).forGetter(Pair::getFirst),
                val.fieldOf(nval).forGetter(Pair::getSecond)
        ).apply(t, Pair::of));
    }

    public static <K, V> Codec<Pair<K, V>> pairCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.compoundList(keyCodec, valueCodec).xmap(
                List::getFirst,
                Collections::singletonList
        );
    }

    public static <K, V> Codec<Pair<K, V>> pairCodec(Function<String, K> keyCodec, Function<String, V> valueCodec) {
        return Codec.STRING.listOf(2, 2).xmap(
                c -> Pair.of(keyCodec.apply(c.get(0)), valueCodec.apply(c.get(1))),
                p -> List.of(p.getFirst().toString(), p.getSecond().toString())
        );
    }

    /**
     * By xiaogu
     **/
    public static <K, V> Codec<Map<K, V>> mapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.compoundList(keyCodec, valueCodec).xmap(
                pl -> {
                    Map<K, V> map = new LinkedHashMap<>();
                    for (Pair<K, V> kvPair : pl) {
                        map.put(kvPair.getFirst(), kvPair.getSecond());
                    }
                    return map;
                },
                map -> map.entrySet().stream()
                        .map(ent -> Pair.of(ent.getKey(), ent.getValue()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * By xiaogu
     **/
    public static <K, V> Codec<Map<K, V>> mapCodec(String nkey, Codec<K> keyCodec, String nval, Codec<V> valueCodec) {
        return Codec.list(pairCodec(nkey, keyCodec, nval, valueCodec)).xmap(
                pl -> pl.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
                map -> map.entrySet().stream()
                        .map(ent -> Pair.of(ent.getKey(), ent.getValue()))
                        .collect(Collectors.toList())
        );
    }

    public static <K extends Enum<K> & StringRepresentable, V> Codec<EnumMap<K, V>> enumMapCodec(StringRepresentable.EnumCodec<K> keyCodec, Codec<V> valueCodec, Class<K> kClass) {
        return Codec.compoundList(keyCodec, valueCodec).xmap(
                pl -> {
                    EnumMap<K, V> collect = new EnumMap<>(kClass);
                    for (int i = 0, plSize = pl.size(); i < plSize; i++) {
                        Pair<K, V> kvPair = pl.get(i);
                        collect.put(kvPair.getFirst(), kvPair.getSecond());
                    }
                    return collect;
                },
                map -> map.entrySet().stream()
                        .map(ent -> Pair.of(ent.getKey(), ent.getValue()))
                        .collect(Collectors.toList())
        );
    }

    public static <K extends Enum<K> & StringRepresentable, V> Codec<Enum2ObjectMap<K, V>> enum2ObjectMapCodec(StringRepresentable.EnumCodec<K> keyCodec, Codec<V> valueCodec, Class<K> kClass) {
        return Codec.compoundList(keyCodec, valueCodec).xmap(
                pl -> {
                    Enum2ObjectMap<K, V> collect = new Enum2ObjectMap<>(kClass);
                    for (int i = 0, plSize = pl.size(); i < plSize; i++) {
                        Pair<K, V> kvPair = pl.get(i);
                        collect.put(kvPair.getFirst(), kvPair.getSecond());
                    }
                    return collect;
                },
                map -> map.entrySet().stream()
                        .map(ent -> Pair.of(ent.getKey(), ent.getValue()))
                        .collect(Collectors.toList())
        );
    }

    public static <E> RegistryFixedCodec<E> holderCodec(ResourceKey<? extends Registry<E>> registryKey) {
        return RegistryFixedCodec.create(registryKey);
    }


    public static <E> Codec<HolderSet<E>> holderSetCodec(ResourceKey<? extends Registry<E>> registryKey) {
        return RegistryCodecs.homogeneousList(registryKey, false);
    }

    //public static <E> Codec<E> empty(E e) {
    //    return Codec.unit(e);
    //}


    public static <I, F extends I, S extends I> Codec<I> either(final Codec<F> first, final Codec<S> second, Class<F> fClass, Class<S> sClass) {
        return new InterfaceCodec<>(first, fClass, second, sClass);
    }

    public static <E> Codec<List<E>> listFrom(Codec<E> singleCodec) {
        return Codec.either(singleCodec,singleCodec.listOf())
                .flatXmap(
                        either -> DataResult.success(either.left().isPresent() ?
                                List.of(either.left().get()) :
                                either.right().isPresent() ? either.right().get() : List.of()),
                        cond -> {
                            if (cond.size() == 1) return DataResult.success(Either.left(cond.getFirst()));
                            return DataResult.success(Either.right(cond));
                        });
    }

    // static {
    //     Codec<Item> dispatch = Identifier.CODEC.dispatch("type",
    //             BuiltInRegistries.ITEM::getKey,
    //             Identifier ->
    //                     MapCodec.unit(BuiltInRegistries.ITEM.get(Identifier))
    //     );
    // }
}

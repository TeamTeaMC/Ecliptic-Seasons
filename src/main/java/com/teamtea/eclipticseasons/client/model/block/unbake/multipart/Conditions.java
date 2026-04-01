package com.teamtea.eclipticseasons.client.model.block.unbake.multipart;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Conditions {
    public static class ConditionCodecs {

        public static final Codec<ConditionLike> CONDITION_CODEC = Codec.either(
                Codec.either(AND_CODEC, OR_CODEC),
                KEY_VALUE_CODEC
        ).flatXmap(
                either -> DataResult.success(either.map(c ->
                        c.left().isPresent() ? c.left().get() : c.right().get(), Function.identity())),
                cond -> {
                    if (cond instanceof AndConditionLike and) return DataResult.success(Either.left(Either.left(and)));
                    if (cond instanceof OrConditionLike or) return DataResult.success(Either.left(Either.right(or)));
                    if (cond instanceof KeyValueConditionLike kv) return DataResult.success(Either.right(kv));
                    return DataResult.error(() -> "Unknown ConditionLike type: " + cond);
                }
        );
    }

    // 为了递归引用
    public static final Codec<ConditionLike> CODEC = Codec.lazyInitialized(() -> ConditionCodecs.CONDITION_CODEC);

    // 普通键值对：直接通过 Map<String, String>
    private static final Codec<ConditionLike> KEY_VALUE_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .flatXmap(map -> {
                        if (map.size() == 1) {
                            Map.Entry<String, String> entry = map.entrySet().iterator().next();
                            return DataResult.success(new KeyValueConditionLike(entry.getKey(), entry.getValue()));
                        } else {
                            return DataResult.error(() -> "Simple KeyValueConditionLike must have exactly one entry");
                        }
                    }, cond -> {
                        if (cond instanceof KeyValueConditionLike kv) {
                            return DataResult.success(Map.of(kv.key, kv.value));
                        }
                        return DataResult.error(() -> "Not a KeyValueCondition");
                    });

    // OR 组合
    private static final Codec<OrConditionLike> OR_CODEC =
            CODEC.listOf()
                    .fieldOf("OR")
                    .xmap(OrConditionLike::new, OrConditionLike::getConditions).codec();

    // AND 组合
    private static final Codec<AndConditionLike> AND_CODEC_BASE =
            CODEC.listOf()
                    .fieldOf("AND")
                    .xmap(AndConditionLike::new, AndConditionLike::getConditions).codec();

    private static final Codec<AndConditionLike> AND_CODEC_FLAT =
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .flatXmap(map -> {
                        if (map.size() != 1) {
                            List<KeyValueConditionLike> list = map.entrySet().stream().map(entry -> new KeyValueConditionLike(entry.getKey(), entry.getValue())).toList();
                            return DataResult.success(new AndConditionLike(list));
                        } else {
                            return DataResult.error(() -> "Flat AndConditionLike must have more than one entry or none");
                        }
                    }, cond -> {
                        if (cond instanceof AndConditionLike andConditionLike) {
                            if (andConditionLike.getConditions().isEmpty()
                                    || andConditionLike.getConditions().stream().allMatch(c -> c instanceof KeyValueConditionLike)) {
                                return DataResult.success(andConditionLike.getConditions().stream()
                                        .collect(Collectors.toMap(e -> ((KeyValueConditionLike) e).getKey(), e -> ((KeyValueConditionLike) e).getValue())));
                            }
                        }
                        return DataResult.error(() -> "Not a Flat AndConditionLike");
                    });

    private static final Codec<AndConditionLike> AND_CODEC =
            Codec.either(AND_CODEC_FLAT, AND_CODEC_BASE).flatXmap(
                    x -> {
                        if (x.left().isPresent()) {
                            return DataResult.success(x.left().get());
                        }
                        if (x.right().isPresent()) {
                            return DataResult.success(x.right().get());
                        }
                        return DataResult.error(() -> "Fail to parse");
                    },
                    x -> {
                        if (x instanceof AndConditionLike andConditionLike) {
                            if (andConditionLike.getConditions()
                                    .stream().allMatch(c -> c instanceof KeyValueConditionLike)) {
                                return DataResult.success(Either.left(x));
                            }
                            return DataResult.success(Either.right(x));
                        }
                        return DataResult.error(() -> "Not a AndConditionLike");
                    }
            );

    // 将多种结构组合在一起


}

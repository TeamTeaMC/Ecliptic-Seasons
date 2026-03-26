package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public record ESSortInfo(
        Identifier registry,
        List<Identifier> target,
        int priority,
        boolean remove
) {

    public static final Codec<ESSortInfo> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Identifier.CODEC.fieldOf("registry").forGetter(ESSortInfo::registry),
            CodecUtil.listFrom(Identifier.CODEC).fieldOf("target").forGetter(ESSortInfo::target),
            Codec.INT.optionalFieldOf("priority", 1000).forGetter(ESSortInfo::priority),
            Codec.BOOL.optionalFieldOf("remove", false).forGetter(ESSortInfo::remove)
    ).apply(ins, ESSortInfo::new));

    private static final Map<Identifier, List<Identifier>> REMOVE_MAP = new HashMap<>();
    private static final Map<Identifier, Map<Identifier, Integer>> PRIORITY_MAP = new HashMap<>();
    public static boolean hasUpdated = false;

    private static final int INTERNAL_PRIORITY_MUL = 1000;

    public static void resetUpdate(HolderLookup.Provider registryAccess, boolean isServer) {
        if (hasUpdated) return;
        var registry1 = registryAccess.lookup(ESRegistries.EXTRA_INFO);
        if (registry1.isEmpty()) return;

        for (ESSortInfo esDataSorted : registry1.get().listElements().map(Holder::value).toList()) {
            if (esDataSorted.remove()) {
                List<Identifier> Identifiers = REMOVE_MAP.computeIfAbsent(esDataSorted.registry, (cc) -> new ArrayList<>());
                Identifiers.addAll(esDataSorted.target());
            } else {
                var Identifiers = PRIORITY_MAP.computeIfAbsent(esDataSorted.registry, (cc) -> new LinkedHashMap<>());
                List<Identifier> targeted = esDataSorted.target();
                for (int i = 0, targetedSize = targeted.size(); i < targetedSize; i++) {
                    Identifier holder = targeted.get(i);
                    Identifiers.put(holder, esDataSorted.priority() * INTERNAL_PRIORITY_MUL + i);
                }
            }
        }
        hasUpdated = true;
    }

    public static void clearOnClientExitOrServerClose() {
        REMOVE_MAP.clear();
        PRIORITY_MAP.clear();
        hasUpdated = false;
    }

    public static <T> Set<Map.Entry<ResourceKey<T>, T>> sorted(Set<Map.Entry<ResourceKey<T>, T>> original) {
        if (original.isEmpty()) return original;
        Map.Entry<ResourceKey<T>, T> resourceKeyTEntry = original.stream().findFirst().get();
        Identifier registry1 = resourceKeyTEntry.getKey().registry();
        Map<Identifier, Integer> priorityMap = PRIORITY_MAP.getOrDefault(registry1, Map.of());
        List<Identifier> removemap2 = REMOVE_MAP.getOrDefault(registry1, List.of());
        if (priorityMap.isEmpty() && removemap2.isEmpty()) return original;

        Map<Identifier, Map.Entry<ResourceKey<T>, T>> oldMap = new HashMap<>();
        for (Map.Entry<ResourceKey<T>, T> e : original) {
            if (!removemap2.contains(e.getKey().identifier()))
                oldMap.put(e.getKey().identifier(), e);
        }

        return oldMap.entrySet().stream().sorted(
                        Comparator.comparingInt(a ->
                                priorityMap.getOrDefault(a.getKey(), 1000 * INTERNAL_PRIORITY_MUL)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    public static <T> List<Holder.Reference<T>> sorted(List<Holder.Reference<T>> original) {
        if (original.isEmpty()) return original;
        var resourceKeyTEntry = original.get(0);
        Identifier registry1 = resourceKeyTEntry.key().registry();
        Map<Identifier, Integer> priorityMap = PRIORITY_MAP.getOrDefault(registry1, Map.of());
        List<Identifier> removemap2 = REMOVE_MAP.getOrDefault(registry1, List.of());
        if (priorityMap.isEmpty() && removemap2.isEmpty()) return original;
        original = new ArrayList<>(original);
        original.removeIf(e -> removemap2.contains(e.key().identifier()));
        original.sort(Comparator.comparingInt(a ->
                priorityMap.getOrDefault(a.key().identifier(), 1000 * INTERNAL_PRIORITY_MUL)));
        return original;
    }

    // public static <T> List<T> sorted2(Registry<T> registry) {
    //     List<Holder.Reference<T>> list = registry.listElements().toList();
    //     return sorted(list).stream().map(Holder::value).toList();
    // }

    public static <T> List<T> sorted2(HolderLookup.RegistryLookup<T> registry) {
        List<Holder.Reference<T>> list = registry.listElements().toList();
        return sorted(list).stream().map(Holder::value).toList();
    }
}

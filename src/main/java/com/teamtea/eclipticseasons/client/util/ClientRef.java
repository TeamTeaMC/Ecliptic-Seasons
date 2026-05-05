package com.teamtea.eclipticseasons.client.util;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBackgroundMusic;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.misc.util.Mergable;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.stream.Collectors;

public class ClientRef {

    public static final Map<Biome, BiomeColor.Instance> biomeColors = new IdentityHashMap<>();

    public static final Map<Block, List<Pair<LeafColor.InstanceHolder, LeafColor.Instance>>> leaveColors = new IdentityHashMap<>();

    public static final List<SeasonalBiomeAmbient> sounds = new ArrayList<>();
    public static final Map<Block, List<SeasonBlockDefinition>> seasonDef = new IdentityHashMap<>();
    public static final Map<Block, List<SnowDefinition>> snowClientDef = new IdentityHashMap<>();

    public static final List<SeasonalBackgroundMusic> musics = new ArrayList<>();

    public static void updateClientSide(RegistryAccess registryAccess) {
        biomeColors.clear();
        leaveColors.clear();
        sounds.clear();
        seasonDef.clear();
        snowClientDef.clear();
        musics.clear();
        buildBiomeColors(registryAccess);
        buildLeafColors(registryAccess);
        buildSeasonalSounds(registryAccess);
        buildSeasonalModels(registryAccess);
        buildOverrideSnowModels(registryAccess);
        buildSeasonalMusics(registryAccess);
    }

    private static void buildSeasonalMusics(RegistryAccess registryAccess) {
        musics.addAll(ClientJsonCacheListener.backgroundMusicCache
                .build(SeasonalBackgroundMusic.CODEC, registryAccess).values());
    }

    private static void buildSeasonalSounds(RegistryAccess registryAccess) {
        sounds.addAll(ClientJsonCacheListener.ambientCache
                .build(SeasonalBiomeAmbient.CODEC, registryAccess).values());
    }

    private static void buildOverrideSnowModels(RegistryAccess registryAccess) {
        ArrayList<Pair<HolderSet<Block>, SnowDefinition>> collect = ClientJsonCacheListener.snowDefOverrideCache
                .build(SnowDefinition.CODEC, registryAccess).values()
                .stream()
                .map(HolderMappable::asHolderMapping)
                .collect(Collectors.toCollection(ArrayList::new));

        Map<Block, List<SnowDefinition>> biomeListMap = buildFromHolders(collect, getHolders(registryAccess, Registries.BLOCK));
        snowClientDef.putAll(biomeListMap);
    }

    private static void buildSeasonalModels(RegistryAccess registryAccess) {
        ArrayList<Pair<HolderSet<Block>, SeasonBlockDefinition>> collect = ClientJsonCacheListener.seasonDefCache
                .build(SeasonBlockDefinition.CODEC, registryAccess)
                .entrySet()
                .stream().filter(r ->
                        ClientConfig.Renderer.flowerOnGrass.get() || !r.getKey().equals(SeasonBlockDefinition.GRASS_BLOCK))
                .map(Map.Entry::getValue)
                .map(HolderMappable::asHolderMapping)
                .collect(Collectors.toCollection(ArrayList::new));
        Map<Block, List<SeasonBlockDefinition>> biomeListMap = buildFromHolders(collect, getHolders(registryAccess, Registries.BLOCK));
        seasonDef.putAll(biomeListMap);
    }

    private static void buildLeafColors(RegistryAccess registryAccess) {
        ArrayList<Pair<HolderSet<Block>, Pair<LeafColor.InstanceHolder, LeafColor.Instance>>> collect = ClientJsonCacheListener.leafCache
                .build(LeafColor.CODEC, registryAccess).values()
                .stream().map(HolderMappable::asHolderMapping)
                .collect(Collectors.toCollection(ArrayList::new));
        Map<Block, List<Pair<LeafColor.InstanceHolder, LeafColor.Instance>>> biomeListMap = buildFromHolders(collect, getHolders(registryAccess, Registries.BLOCK));
        biomeListMap.forEach(
                (pairs, instances) -> {
                    List<Pair<LeafColor.InstanceHolder, LeafColor.Instance>> instance = mergePairList(instances);
                    leaveColors.put(pairs, instance);
                }
        );
    }

    private static void buildBiomeColors(RegistryAccess registryAccess) {
        ArrayList<Pair<HolderSet<Biome>, BiomeColor.Instance>> collect = ClientJsonCacheListener.biomeCache
                .build(BiomeColor.CODEC, registryAccess).values()
                .stream().map(HolderMappable::asHolderMapping)
                .collect(Collectors.toCollection(ArrayList::new));
        Map<Biome, List<BiomeColor.Instance>> biomeListMap = buildFromHolders(collect, getHolders(registryAccess, Registries.BIOME));
        biomeListMap.forEach(
                (biome, instances) -> {
                    BiomeColor.Instance instance = mergeList(instances);
                    biomeColors.put(biome, instance);
                }
        );
    }


    public static <T, V> Map<T, List<V>> buildFromHolders(List<Pair<HolderSet<T>, V>> pairs, List<Holder<T>> holders) {
        Map<T, List<V>> resultMap = new HashMap<>();
        for (Pair<HolderSet<T>, V> pair : pairs) {
            HolderSet<T> holderSet = pair.getFirst();
            V value = pair.getSecond();
            for (Holder<T> th : holderSet) {
                T t = th.value();
                resultMap.putIfAbsent(t, new ArrayList<>());
                if (holderSet.contains(th)) {
                    resultMap.get(t).add(value);
                }
            }
        }
        return resultMap;
    }

    public static <E> ArrayList<Holder<E>> getHolders(RegistryAccess registryAccess, ResourceKey<? extends Registry<? extends E>> registryKey) {
        Optional<Registry<E>> registry = registryAccess.registry(registryKey);
        if (registry.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(registryKey);
            return new ArrayList<>();
        }
        return registry.get().holders().collect(Collectors.toCollection(ArrayList::new));
    }

    private static <S, T extends Mergable<T>> List<Pair<S, T>> mergePairList(List<Pair<S, T>> instances) {
        if (instances == null || instances.size() <= 1) {
            return instances;
        }
        Map<S, T> mergedMap = new HashMap<>();
        for (Pair<S, T> pair : instances) {
            S key = pair.getFirst();
            T value = pair.getSecond();
            mergedMap.merge(key, value, Mergable::merge);
        }

        return mergedMap.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static <T extends Mergable<T>> T mergeList(List<T> instances) {
        if (instances.isEmpty()) {
            return null;
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return instances.stream().reduce(Mergable::merge).orElse(null);
    }

    public static void onClientPlayerExit() {
        biomeColors.clear();
        leaveColors.clear();
        sounds.clear();
        seasonDef.clear();
        snowClientDef.clear();
        musics.clear();
    }
}

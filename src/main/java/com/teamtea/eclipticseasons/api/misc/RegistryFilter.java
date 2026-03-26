package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface RegistryFilter<T> {
    List<? extends Holder<T>> toHolders(HolderLookup.RegistryLookup<T> registry);

    public record DirectHolder<T>(ResourceKey<T> key) implements RegistryFilter<T> {
        @Override
        public List<? extends Holder<T>> toHolders(HolderLookup.RegistryLookup<T> biomes) {
            return biomes.get(key).map(List::of).orElse(List.of());
        }
    }

    public record TagHolder<T>(TagKey<T> tag) implements RegistryFilter<T> {
        @Override
        public List<Holder<T>> toHolders(HolderLookup.RegistryLookup<T> biomes) {
            return biomes.get(tag).map(b -> b.stream().toList()).orElse(List.of());
        }
    }

    public record And<T>(RegistryFilter<T>... filters) implements RegistryFilter<T> {
        @SafeVarargs
        public And {
        }

        @Override
        public List<Holder<T>> toHolders(HolderLookup.RegistryLookup<T> biomes) {
            if (filters.length == 0) return List.of();

            Set<Holder<T>> result = new HashSet<>(filters[0].toHolders(biomes));
            for (int i = 1; i < filters.length; i++) {
                result.retainAll(filters[i].toHolders(biomes));
            }
            return List.copyOf(result);
        }
    }

    public record Or<T>(RegistryFilter<T>... filters) implements RegistryFilter<T> {
        @SafeVarargs
        public Or {
        }

        @SafeVarargs
        public Or(TagKey<T>... tags) {
            this(Stream.of(tags).map(TagHolder<T>::new).toArray(RegistryFilter[]::new));
        }

        @Override
        public List<Holder<T>> toHolders(HolderLookup.RegistryLookup<T> biomes) {
            Set<Holder<T>> result = new HashSet<>();
            for (RegistryFilter<T> f : filters) {
                result.addAll(f.toHolders(biomes));
            }
            return List.copyOf(result);
        }
    }

    public record Not<T>(RegistryFilter<T>... filters) implements RegistryFilter<T> {
        @SafeVarargs
        public Not {
        }

        @Override
        public List<? extends Holder<T>> toHolders(HolderLookup.RegistryLookup<T> biomes) {
            Set<Holder<T>> excluded = new HashSet<>();
            for (RegistryFilter<T> f : filters) {
                excluded.addAll(f.toHolders(biomes));
            }

            return biomes.listElements()
                    .filter(holder -> !excluded.contains(holder))
                    .toList();
        }
    }
}

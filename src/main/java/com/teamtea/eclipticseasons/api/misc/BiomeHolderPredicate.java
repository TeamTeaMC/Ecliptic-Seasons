package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

@FunctionalInterface
public interface BiomeHolderPredicate extends Predicate<Holder<Biome>> {
    public static BiomeHolderPredicate of(HolderSet<Biome> holders) {
        return new Impl(holders);
    }

    public record Impl(HolderSet<Biome> holders) implements BiomeHolderPredicate {

        @Override
        public boolean test(Holder<Biome> biomeHolder) {
            return holders.contains(biomeHolder);
        }
    }
}

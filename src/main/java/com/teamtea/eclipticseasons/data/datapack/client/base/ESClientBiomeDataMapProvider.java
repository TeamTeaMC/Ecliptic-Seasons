package com.teamtea.eclipticseasons.data.datapack.client.base;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.concurrent.CompletableFuture;

public abstract class ESClientBiomeDataMapProvider<C> extends ESClientDataMapProvider<C> {
    public ESClientBiomeDataMapProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries, String type, Codec<C> codec) {
        super(output, modid, helper, registries, type, codec);
    }

    @SafeVarargs
    protected final <T> HolderSet<T> and(HolderSet<T>... values) {
        return new AndHolderSet<>(values);
    }

    @SafeVarargs
    protected final <T> HolderSet<T> or(HolderSet<T>... values) {
        return new OrHolderSet<>(values);
    }

    protected final HolderSet<Biome> not(HolderSet<Biome> value) {
        return new NotHolderSet<>(BIOME_REGISTRY_LOOKUP, value);
    }

    protected final HolderSet<Biome> get(TagKey<Biome> tagKey) {
        return BIOME_HOLDER_GETTER.getOrThrow(tagKey);
    }

    protected final HolderSet<Biome> get(ResourceKey<Biome> tagKey) {
        return HolderSet.direct(BIOME_HOLDER_GETTER.getOrThrow(tagKey));
    }


    private HolderLookup.RegistryLookup<Biome> BIOME_REGISTRY_LOOKUP = null;
    private HolderGetter<Biome> BIOME_HOLDER_GETTER = null;

    @Override
    protected void gather(HolderLookup.Provider provider) {
        BIOME_HOLDER_GETTER = provider.lookupOrThrow(Registries.BIOME);
        BIOME_REGISTRY_LOOKUP = new AgroClimateRegistry.BiomeRegistryLookup(BIOME_HOLDER_GETTER);
        gather(provider,BIOME_HOLDER_GETTER);
        BIOME_HOLDER_GETTER = null;
        BIOME_REGISTRY_LOOKUP = null;
    }

    protected abstract void gather(HolderLookup.Provider provider, HolderGetter<Biome> biomeHolderGetter);
}

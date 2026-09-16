package com.teamtea.eclipticseasons.data.api.provider.base;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.neoforged.neoforge.common.util.ConcatenatedListView;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ESSubPackDataMapProvider<T> extends ESClientDataMapProvider<T> {

    private final ResourceKey<? extends Registry<T>> entryKey;
    private final RegistryDataLoader.RegistryData<T> registryData;
    private final Set<String> allowedNamespaces = new HashSet<>();

    public ESSubPackDataMapProvider(
            PackOutput output,
            String modid,
            CompletableFuture<HolderLookup.Provider> registries,
            ResourceKey<? extends Registry<T>> entryKey,
            RegistryDataLoader.RegistryData<T> registryData,
            String... allowedNamespaces
    ) {
        super(output, modid, registries, FileToIdConverter.registry(entryKey).prefix(), registryData.elementCodec());
        this.entryKey = entryKey;
        this.registryData = registryData;
        if (allowedNamespaces.length > 0) {
            Collections.addAll(this.allowedNamespaces, allowedNamespaces);
        } else {
            this.allowedNamespaces.add(modid);
        }
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<T> lookup = provider.lookupOrThrow(entryKey);

        List<Identifier> unbound = new ArrayList<>();
        List<Identifier> skipped = new ArrayList<>();

        for (Holder.Reference<T> reference : lookup.listElements().toList()) {
            Identifier id = reference.key().identifier();

            // Skip and record references outside this mod's namespace
            if (!allowedNamespaces.contains(id.getNamespace())) {
                skipped.add(id);
                continue;
            }

            if (reference.isBound()) {
                add(id, reference.value());
            } else {
                unbound.add(id);
            }
        }

        if (!skipped.isEmpty()) {
            EclipticSeasons.logger(
                    "Skipped non-mod references in registry "
                            + entryKey.identifier()
                            + ": "
                            + skipped
            );
        }

        if (!unbound.isEmpty()) {
            EclipticSeasons.logger(
                    "Unbound references in registry "
                            + entryKey.identifier()
                            + ": "
                            + unbound
            );
        }
    }

    @Override
    protected Path resolvePath(Identifier id) {
        return this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(id.getNamespace())
                .resolve(type)
                .resolve(id.getPath() + ".json");
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        gather(provider);
        Codec<T> codec = registryData.elementCodec();

        return CompletableFuture.allOf(
                outMap.entrySet().stream()
                        .map(e -> saveStable(output, provider, codec, e.getValue().get(), resolvePath(e.getKey())))
                        .toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public @NonNull String getName() {
        return "ESSubPackDataMapProvider[" + entryKey.identifier() + "]" + hashCode();
    }

    public static void addAll(
            GatherDataEvent event,
            PackOutput output,
            RegistrySetBuilder builder,
            String... namespaces
    ) {
        RegistryAccess.Frozen registryAccess =
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

        HolderLookup.Provider modProvider = builder.build(registryAccess);

        CompletableFuture<HolderLookup.Provider> modFuture =
                CompletableFuture.supplyAsync(() -> modProvider, Util.backgroundExecutor());

        List<RegistryDataLoader.RegistryData<?>> registryDataList = ConcatenatedListView.of(
                DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().toList(),
                RegistryDataLoader.RELOADABLE_REGISTRIES
        );

        for (ResourceKey<? extends Registry<?>> entryKey : builder.getEntryKeys()) {
            RegistryDataLoader.RegistryData<?> registryData = registryDataList.stream()
                    .filter(data -> data.key().equals(entryKey))
                    .findFirst()
                    .orElse(null);

            if (registryData == null) {
                EclipticSeasons.logger("No RegistryData found for registry: " + entryKey.identifier());
                continue;
            }

            addProvider(
                    event,
                    output,
                    event.getModContainer().getNamespace(),
                    modFuture,
                    entryKey,
                    registryData,
                    namespaces
            );
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void addProvider(
            GatherDataEvent event,
            PackOutput output,
            String modid,
            CompletableFuture<HolderLookup.Provider> provider,
            ResourceKey<? extends Registry<?>> entryKey,
            RegistryDataLoader.RegistryData<?> registryData,
            String... namespaces
    ) {
        ResourceKey<? extends Registry<T>> typedEntryKey = (ResourceKey<? extends Registry<T>>) entryKey;
        RegistryDataLoader.RegistryData<T> typedRegistryData = (RegistryDataLoader.RegistryData<T>) registryData;

        event.addProvider(new ESSubPackDataMapProvider<>(
                output,
                modid,
                provider,
                typedEntryKey,
                typedRegistryData,
                namespaces
        ));
    }
}
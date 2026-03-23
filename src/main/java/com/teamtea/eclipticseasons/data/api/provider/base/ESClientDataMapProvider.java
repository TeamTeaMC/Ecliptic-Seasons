package com.teamtea.eclipticseasons.data.api.provider.base;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class ESClientDataMapProvider<T> implements DataProvider {

    private final PackOutput output;
    protected final String modid;
    public final ExistingFileHelper helper;

    protected final Map<ResourceLocation, Supplier<T>> outMap;
    private final String type;
    private final Codec<T> codec;
    protected   CompletableFuture<HolderLookup.Provider> registries;

    public ESClientDataMapProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries, String type, Codec<T> codec) {
        this.output = output;
        this.modid = modid;
        this.helper = helper;
        outMap = new HashMap<>();
        this.type = type;
        this.codec = codec;
        this.registries = registries;
    }

    protected abstract void gather(HolderLookup.Provider provider);

    protected void add(String path, T t) {
        this.outMap.put(ResourceLocation.fromNamespaceAndPath(modid, path), () -> t);
    }

    protected void add(ResourceLocation path, T t) {
        this.outMap.put(path, () -> t);
    }

    protected void add(String path, Supplier<T> t) {
        this.outMap.put(ResourceLocation.fromNamespaceAndPath(modid, path), t);
    }

    protected void add(ResourceLocation path, Supplier<T> t) {
        this.outMap.put(path, t);
    }


    protected Path resolvePath(ResourceLocation id) {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(id.getNamespace())
                .resolve(type)
                .resolve(id.getPath() + ".json");
    }


    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> this.run(output, provider));
    }

    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        gather(provider);
        return CompletableFuture.allOf(outMap.entrySet().stream()
                .map(e -> saveStable(output, provider, codec, e.getValue().get(), resolvePath(e.getKey()))
                ).toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Client Provider for %s: %s".formatted(modid, type);
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput output, HolderLookup.Provider registries, Codec<T> codec, T value, Path path) {
        RegistryOps<JsonElement> registryops = registries.createSerializationContext(JsonOps.INSTANCE);
        JsonElement jsonelement = codec.encodeStart(registryops, value).getOrThrow();
        return saveStable(output, jsonelement, path);
    }

    static CompletableFuture<?> saveStable(CachedOutput output, JsonElement json, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent(" ".repeat(Math.max(0, INDENT_WIDTH.get()))); // Neo: Allow changing the indent width without needing to mixin this lambda.
                    Gson gson = new GsonBuilder()
                            .disableHtmlEscaping()
                            .create();
                    gson.toJson(json, jsonwriter);
                }


                output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", path, ioexception);
            }
        }, Util.backgroundExecutor());
    }
}

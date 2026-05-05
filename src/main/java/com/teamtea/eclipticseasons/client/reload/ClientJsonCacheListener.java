package com.teamtea.eclipticseasons.client.reload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBackgroundMusic;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.data.client.ui.UIParser;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ClientJsonCacheListener<T> extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    public static final Map<String, Supplier<Set<Identifier>>> ALL_MAP = new HashMap<>();

    private final Map<Identifier, JsonElement> elementMap;
    public static final Gson GSON = new GsonBuilder().setLenient()
            // .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
            .create();

    public static final String DIRECTORY_TEST = EclipticSeasonsApi.MODID + "/test";

    public static final String DIRECTORY_BIOME = EclipticSeasonsApi.MODID + "/biome_colors";
    public static final String DIRECTORY_LEAF = EclipticSeasonsApi.MODID + "/particles/fallen_leaves";
    public static final String DIRECTORY_SNOW_DEFINITION = EclipticSeasonsApi.MODID + "/snow_definitions";
    public static final String DIRECTORY_AMBIENT = EclipticSeasonsApi.MODID + "/ambient";
    public static final String DIRECTORY_BACKGROUND_MUSIC = EclipticSeasonsApi.MODID + "/background_music";
    public static final String DIRECTORY_MODEL_DEFINITION = EclipticSeasonsApi.MODID + "/model_definitions";
    public static final String DIRECTORY_SEASON_DEFINITION = EclipticSeasonsApi.MODID + "/season_definitions";

    public static final String DIRECTORY_SEASON_TEXTURES = EclipticSeasonsApi.MODID + "/season_textures";

    public static final String DIRECTORY_UI_PARSER = EclipticSeasonsApi.MODID + "/ui_parser";

    // Async
    public static final ClientJsonCacheListener<ESModelLoadedJson> modelDefCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_MODEL_DEFINITION, true);
    public static final ClientJsonCacheListener<SeasonalTexture> textureReMappingsCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_SEASON_TEXTURES, true);

    // normal
    public static final ClientJsonCacheListener<BiomeColor> biomeCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_BIOME);
    public static final ClientJsonCacheListener<LeafColor> leafCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_LEAF);
    public static final ClientJsonCacheListener<SnowDefinition> snowDefOverrideCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_SNOW_DEFINITION);
    public static final ClientJsonCacheListener<SeasonalBiomeAmbient> ambientCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_AMBIENT);
    public static final ClientJsonCacheListener<SeasonBlockDefinition> seasonDefCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_SEASON_DEFINITION);
    public static final ClientJsonCacheListener<UIParser> uiParserCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_UI_PARSER);
    public static final ClientJsonCacheListener<SeasonalBackgroundMusic> backgroundMusicCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_BACKGROUND_MUSIC);


    private final String directory;
    private final boolean async;

    public ClientJsonCacheListener(Gson gson, String directory) {
        this(gson, directory, false);
    }

    public ClientJsonCacheListener(Gson gson, String directory, boolean async) {
        this.directory = directory;
        ALL_MAP.put(directory, () -> getElementMap().keySet());
        this.async = async;
        this.elementMap = async ? new ConcurrentHashMap<>() : new HashMap<>();
    }


    @Override
    public Map<Identifier, JsonElement> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        if (!async) {
            Map<Identifier, JsonElement> prepared = scanDirectorySync(manager, this.directory, GSON);
            this.elementMap.clear();
            this.elementMap.putAll(prepared);
        }
        return elementMap;
    }

    // we need it since standalone models not load after model loads
    public void prepareAsync(ResourceManager manager) {
        Map<Identifier, JsonElement> prepared = scanDirectorySync(manager, this.directory, GSON);
        this.elementMap.clear();
        this.elementMap.putAll(prepared);
    }

    public static Map<Identifier, JsonElement> scanDirectorySync(ResourceManager resourceManager, String name, Gson gson) {
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(name);
        Map<Identifier, Resource> matching = fileToIdConverter.listMatchingResources(resourceManager);
        Map<Identifier, JsonElement> output = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : matching.entrySet()) {
            Identifier file = entry.getKey();
            Identifier id = fileToIdConverter.fileToId(file);
            Resource resource = entry.getValue();

            try (Reader reader = resource.openAsReader()) {
                JsonElement element = GsonHelper.fromJson(gson, reader, JsonElement.class);
                JsonElement previous = output.putIfAbsent(id, element);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException e) {
                EclipticSeasons.LOGGER.error("Couldn't parse data file {} from {}", id, file, e);
            }
        }

        return output;
    }


    @Override
    protected void apply(Map<Identifier, JsonElement> preparations, ResourceManager manager, ProfilerFiller profiler) {

    }

    public CompletableFuture<Map<Identifier, JsonElement>> prepareAsync(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            ConcurrentMap<Identifier, JsonElement> prepare = new ConcurrentHashMap<>();
            scanDirectoryAsync(resourceManager, this.directory, GSON, prepare, executor).join();
            this.elementMap.clear();
            this.elementMap.putAll(prepare);
            EclipticSeasons.logger("ssss", elementMap.size(), directory);
            return prepare;
        }, executor);
    }

    public static CompletableFuture<Void> scanDirectoryAsync(ResourceManager resourceManager, String name, Gson gson, ConcurrentMap<Identifier, JsonElement> output, Executor executor) {
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(name);
        Map<Identifier, Resource> matching = fileToIdConverter.listMatchingResources(resourceManager);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Identifier, Resource> entry : matching.entrySet()) {
            Identifier file = entry.getKey();
            Identifier id = fileToIdConverter.fileToId(file);
            Resource resource = entry.getValue();

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(gson, reader, JsonElement.class);
                    JsonElement previous = output.putIfAbsent(id, element);
                    if (previous != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                    }
                } catch (IllegalArgumentException | IOException | JsonParseException e) {
                    EclipticSeasons.LOGGER.error("Couldn't parse data file {} from {}", id, file, e);
                }
            }, executor);

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    // @Override
    // protected void apply(@NonNull Map<Identifier, JsonElement> object, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
    //     this.elementMap.clear();
    //     this.elementMap.putAll(object);
    // }

    public Map<Identifier, JsonElement> getElementMap() {
        return elementMap;
    }


    public Map<Identifier, T> build(Codec<T> codec, HolderLookup.Provider registryAccess) {
        return getIdentifierTMap(codec, registryAccess.createSerializationContext(JsonOps.INSTANCE));
    }

    public Map<Identifier, T> build(Codec<T> codec) {
        DynamicOps<JsonElement> dynamicops = JsonOps.INSTANCE;
        return getIdentifierTMap(codec, dynamicops);
    }

    private @NonNull Map<Identifier, T> getIdentifierTMap(Codec<T> codec, DynamicOps<JsonElement> dynamicops) {
        Map<Identifier, T> map = new HashMap<>();
        this.elementMap.forEach(
                (Identifier, jsonElement) -> {
                    try {
                        codec
                                .parse(dynamicops, jsonElement)
                                .resultOrPartial(x ->
                                        {
                                            String formatted = "Unable to load %s: '%s' due to: %s".formatted(getName().replace(EclipticSeasonsApi.MODID + "/", ""), Identifier, x);
                                            EclipticSeasons.LOGGER.warn(formatted);
                                        }
                                )
                                .ifPresent(t -> {
                                    map.put(Identifier, t);
                                });
                    } catch (Exception e) {
                        // EclipticSeasons.logger(e);
                        String formatted = "Unable to load %s with exception: '%s' due to: %s".formatted(getName().replace(EclipticSeasonsApi.MODID + "/", ""), Identifier, e);
                        EclipticSeasons.LOGGER.warn(formatted);
                    }
                }
        );
        return map;
    }

    @Override
    public @NonNull String getName() {
        return directory;
    }
}

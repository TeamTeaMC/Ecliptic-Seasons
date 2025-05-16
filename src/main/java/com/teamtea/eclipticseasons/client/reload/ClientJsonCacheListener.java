package com.teamtea.eclipticseasons.client.reload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClientJsonCacheListener<T> extends SimpleJsonResourceReloadListener {
    private final Map<ResourceLocation, JsonElement> elementMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setLenient()
            // .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
            .create();

    public static final String DIRECTORY_TEST = EclipticSeasonsApi.MODID + "/test";

    public static final String DIRECTORY_BIOME = EclipticSeasonsApi.MODID + "/biome_colors";
    public static final String DIRECTORY_LEAF = EclipticSeasonsApi.MODID + "/particles/fallen_leaves";
    public static final String DIRECTORY_SNOW_DEFINITION = EclipticSeasonsApi.MODID + "/snow_definitions";
    public static final String DIRECTORY_AMBIENT = EclipticSeasonsApi.MODID + "/ambient";
    public static final String DIRECTORY_MODEL_DEFINITION = EclipticSeasonsApi.MODID + "/model_definitions";
    public static final String DIRECTORY_SEASON_DEFINITION = EclipticSeasonsApi.MODID + "/season_definitions";

    public static final ClientJsonCacheListener<LeafColor> leafCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_LEAF);
    public static final ClientJsonCacheListener<SnowDefinition> snowDefOverrideCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_SNOW_DEFINITION);
    public static final ClientJsonCacheListener<SeasonalBiomeAmbient> ambientCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_AMBIENT);
    public static final ClientJsonCacheListener<ESModelLoadedJson> modelDefCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_MODEL_DEFINITION);
    public static final ClientJsonCacheListener<SeasonBlockDefinition> seasonDefCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_SEASON_DEFINITION);
    private final String directory;

    public ClientJsonCacheListener(Gson gson, String directory) {
        super(gson, directory);
        this.directory = directory;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> prepare = super.prepare(resourceManager, profiler);
        this.elementMap.clear();
        this.elementMap.putAll(prepare);
        return prepare;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
    }

    // @Override
    // protected void apply(@NotNull Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
    //     this.elementMap.clear();
    //     this.elementMap.putAll(object);
    // }

    public Map<ResourceLocation, JsonElement> getElementMap() {
        return elementMap;
    }


    public Map<ResourceLocation, T> build(Codec<T> codec, RegistryAccess registryAccess) {
        return getResourceLocationTMap(codec, RegistryOps.create(JsonOps.INSTANCE, registryAccess));
    }

    public Map<ResourceLocation, T> build(Codec<T> codec) {
        DynamicOps<JsonElement> dynamicops = JsonOps.INSTANCE;
        return getResourceLocationTMap(codec, dynamicops);
    }

    private @NotNull Map<ResourceLocation, T> getResourceLocationTMap(Codec<T> codec, DynamicOps<JsonElement> dynamicops) {
        Map<ResourceLocation, T> map = new HashMap<>();
        this.elementMap.forEach(
                (resourceLocation, jsonElement) -> {
                    try {
                        codec
                                .parse(dynamicops, jsonElement)
                                .resultOrPartial(x ->
                                        {
                                            String formatted = "Unable to load %s: '%s' due to: %s".formatted(getName().replace(EclipticSeasonsApi.MODID+"/",""), resourceLocation, x);
                                            EclipticSeasons.LOGGER.warn(formatted);
                                        }
                                )
                                .ifPresent(t -> {
                                    map.put(resourceLocation, t);
                                });
                    } catch (Exception e) {
                        // EclipticSeasons.logger(e);
                        String formatted = "Unable to load %s with exception: '%s' due to: %s".formatted(getName().replace(EclipticSeasonsApi.MODID+"/",""), resourceLocation, e);
                        EclipticSeasons.LOGGER.warn(formatted);
                    }
                }
        );
        return map;
    }

    @Override
    public @NotNull String getName() {
        return directory;
    }
}

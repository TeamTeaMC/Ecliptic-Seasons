package com.teamtea.eclipticseasons.client.reload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class ClientJsonCacheListener<T> extends SimpleJsonResourceReloadListener {
    private final Map<ResourceLocation, JsonElement> elementMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setLenient()
            // .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
            .create();

    public static final String DIRECTORY_BIOME = EclipticSeasonsApi.MODID + "/biome_colors";
    public static final String DIRECTORY_LEAF = EclipticSeasonsApi.MODID + "/particles/fallen_leaves";
    public static final String DIRECTORY_MODEL = EclipticSeasonsApi.MODID + "/models";
    public static final String DIRECTORY_AMBIENT = EclipticSeasonsApi.MODID + "/ambient";

    public static final ClientJsonCacheListener<SeasonalBiomeAmbient> ambientCache = new ClientJsonCacheListener<>(GSON, DIRECTORY_AMBIENT);

    public ClientJsonCacheListener(Gson gson, String directory) {
        super(gson, directory);
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
        Map<ResourceLocation, T> map = new HashMap<>();
        DynamicOps<JsonElement> dynamicops =RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        this.elementMap.forEach(
                (resourceLocation, jsonElement) -> {
                    codec
                            .parse(dynamicops, jsonElement)
                            .resultOrPartial(EclipticSeasons::logger)
                            .ifPresent(t -> {
                                map.put(resourceLocation, t);
                            });
                }
        );
        return map;
    }
}

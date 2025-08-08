package com.teamtea.eclipticseasons.common.registry;


import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class AgroClimateRegistry {

    /**
     * Temperate Climate is the standard Crop.
     **/
    public static final ResourceKey<AgroClimaticZone> TEMPERATE = createKey("temperate");
    public static final ResourceKey<AgroClimaticZone> COLD = createKey("cold");
    public static final ResourceKey<AgroClimaticZone> HOT = createKey("hot");
    public static final ResourceKey<AgroClimaticZone> DESERT = createKey("desert");
    public static final ResourceKey<AgroClimaticZone> NETHER = createKey("nether");
    public static final ResourceKey<AgroClimaticZone> END = createKey("end");


    private static ResourceKey<AgroClimaticZone> createKey(String name) {
        return ResourceKey.create(ESRegistries.AGRO_CLIMATE, EclipticSeasons.rl(name));
    }

    @SafeVarargs
    private static <T> HolderSet<T> and(HolderSet<T>... values) {
        return new AndHolderSet<>(Arrays.stream(values).toList());
    }

    @SafeVarargs
    private static <T> HolderSet<T> or(HolderSet<T>... values) {
        return new OrHolderSet<>(Arrays.stream(values).toList());
    }

    private static HolderSet<Biome> not(HolderSet<Biome> value) {
        return new NotHolderSet<>(BIOME_REGISTRY_LOOKUP, value);
    }

    private static HolderSet<Biome> get(TagKey<Biome> tagKey) {
        return BIOME_HOLDER_GETTER.getOrThrow(tagKey);
    }

    private static HolderLookup.RegistryLookup<Biome> BIOME_REGISTRY_LOOKUP = null;
    private static HolderGetter<Biome> BIOME_HOLDER_GETTER = null;

    public static void bootstrap(BootstapContext<AgroClimaticZone> context) {
        BIOME_HOLDER_GETTER = context.lookup(Registries.BIOME);
        BIOME_REGISTRY_LOOKUP = new BiomeRegistryLookup(BIOME_HOLDER_GETTER);

        context.register(TEMPERATE, AgroClimaticZone.builder((
                        and(or(get(BiomeTags.IS_OVERWORLD), get(Tags.Biomes.IS_VOID)),
                                not(or(get(Tags.Biomes.IS_PEAK),
                                        get(Tags.Biomes.IS_SNOWY),
                                        get(Tags.Biomes.IS_HOT_OVERWORLD)))
                        )))
                .add(Season.SPRING, 6).add(Season.SUMMER, 6).add(Season.AUTUMN, 6).add(Season.WINTER, 6)
                .end());

        Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> mapCold = of(
                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 0.8f)),
                Either.<Season, SolarTerm>right(SolarTerm.RAIN_WATER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.95f)),
                Either.<Season, SolarTerm>right(SolarTerm.INSECTS_AWAKENING), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.FRESH_GREEN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), 0.7f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.3f)),
                Either.<Season, SolarTerm>right(SolarTerm.GRAIN_RAIN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), 0.5f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.5f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_FULLNESS), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.GRAIN_IN_EAR), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.6f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), 0.4f)),
                Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 0.6f)),
                Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 1f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.END_OF_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 0.6f)),
                Either.<Season, SolarTerm>right(SolarTerm.WHITE_DEW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.COLD_DEW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.6f)),
                Either.<Season, SolarTerm>right(SolarTerm.FIRST_FROST), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 0.2f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.LIGHT_SNOW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.6f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 0.4f)),
                Either.<Season, SolarTerm>right(SolarTerm.HEAVY_SNOW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 0.6f)),
                Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_COLD), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), .8f)),
                Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), 0.55f))
        );

        context.register(COLD, AgroClimaticZone.builder(
                        and(get(BiomeTags.IS_OVERWORLD),
                                or(get(Tags.Biomes.IS_PEAK),
                                        get(Tags.Biomes.IS_SNOWY)))
                )
                .mapping(mapCold)
                .add(Season.WINTER, 3).add(Season.SPRING, 4).add(Season.SUMMER, 3).add(Season.AUTUMN, 4).add(Season.WINTER, 10)
                .end());

        Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> mapHot = of(
                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.RAIN_WATER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SPRING), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.INSECTS_AWAKENING), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SPRING_EQUINOX), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.FRESH_GREEN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), 0.7f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.LESSER_FULLNESS), 0.3f)),
                Either.<Season, SolarTerm>right(SolarTerm.GRAIN_RAIN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.LESSER_FULLNESS), 0.5f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GRAIN_IN_EAR), 0.5f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_SUMMER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GRAIN_IN_EAR), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_FULLNESS), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GRAIN_IN_EAR), 0.3f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.7f)),
                Either.<Season, SolarTerm>right(SolarTerm.GRAIN_IN_EAR), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.LESSER_HEAT), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), 0.8f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), .7f)),
                Either.<Season, SolarTerm>right(SolarTerm.END_OF_HEAT), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.GREATER_HEAT), 0.95f)),
                Either.<Season, SolarTerm>right(SolarTerm.WHITE_DEW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.97f)),
                Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.COLD_DEW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.8f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 0.2f)),
                Either.<Season, SolarTerm>right(SolarTerm.FIRST_FROST), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.SUMMER_SOLSTICE), 0.6f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 0.4f)),

                Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 1f)),
                Either.<Season, SolarTerm>right(SolarTerm.LIGHT_SNOW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_AUTUMN), 0.6f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), 0.4f)),
                Either.<Season, SolarTerm>right(SolarTerm.HEAVY_SNOW), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), 0.4f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 0.6f)),
                Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.AUTUMNAL_EQUINOX), 0.2f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), 0.8f)),
                Either.<Season, SolarTerm>right(SolarTerm.LESSER_COLD), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.BEGINNING_OF_WINTER), .5f), Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 0.5f)),
                Either.<Season, SolarTerm>right(SolarTerm.GREATER_COLD), List.of(Pair.of(Either.<Season, SolarTerm>right(SolarTerm.WINTER_SOLSTICE), 1f))
        );

        context.register(HOT, AgroClimaticZone.builder(and(get(Tags.Biomes.IS_HOT_OVERWORLD)))
                .mapping(mapHot)
                .add(Season.SPRING, 4).add(Season.SUMMER, 14).add(Season.AUTUMN, 3).add(Season.WINTER, 3)
                .end());


        context.register(NETHER, AgroClimaticZone.builder(get(BiomeTags.IS_NETHER))
                .defaultMapping(Pair.of(Either.<Season, SolarTerm>left(Season.SUMMER), .25f))
                .end());

        context.register(END, AgroClimaticZone.builder(get(BiomeTags.IS_END))
                .growParameter(GrowParameter.builder().growChance(0.35f).fertileChance(0.5f).deathChance(0.01f).end())
                .end());


        BIOME_REGISTRY_LOOKUP = null;
        BIOME_HOLDER_GETTER = null;
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
            K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15,
            K k16, V v16, K k17, V v17, K k18, V v18, K k19, V v19, K k20, V v20,
            K k21, V v21, K k22, V v22, K k23, V v23, K k24, V v24) {
        if (k1 == null) {
            throw new IllegalArgumentException("First key cannot be null");
        }
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        map.put(k13, v13);
        map.put(k14, v14);
        map.put(k15, v15);
        map.put(k16, v16);
        map.put(k17, v17);
        map.put(k18, v18);
        map.put(k19, v19);
        map.put(k20, v20);
        map.put(k21, v21);
        map.put(k22, v22);
        map.put(k23, v23);
        map.put(k24, v24);
        return map;
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        if (k1 == null) {
            throw new IllegalArgumentException("First key cannot be null");
        }
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public record BiomeRegistryLookup(
            HolderGetter<Biome> biomeHolderGetter) implements HolderLookup.RegistryLookup<Biome> {

        @Override
        public @NotNull Optional<Holder.Reference<Biome>> get(@NotNull ResourceKey<Biome> pResourceKey) {
            return biomeHolderGetter.get(pResourceKey);
        }

        @Override
        public @NotNull Optional<HolderSet.Named<Biome>> get(@NotNull TagKey<Biome> pTagKey) {
            return biomeHolderGetter.get(pTagKey);
        }

        @Override
        public @NotNull Stream<Holder.Reference<Biome>> listElements() {
            return Stream.empty();
        }

        @Override
        public @NotNull Stream<HolderSet.Named<Biome>> listTags() {
            return Stream.empty();
        }

        @Override
        public @NotNull ResourceKey<? extends Registry<? extends Biome>> key() {
            return Registries.BIOME;
        }

        @Override
        public boolean canSerializeIn(@NotNull HolderOwner<Biome> pOwner) {
            return true;
        }

        @Override
        public @NotNull Lifecycle registryLifecycle() {
            return Lifecycle.stable();
        }
    }
}

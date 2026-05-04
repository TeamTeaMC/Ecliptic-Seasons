package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.List;

public class SeasonCycleRegistry {

    public static final ResourceKey<SeasonCycle> MONSOON = createKey("monsoon");
    // public static final ResourceKey<SeasonCycle> RAINY = createKey("rainy");
    // public static final ResourceKey<SeasonCycle> DESERT = createKey("desert");
    public static final ResourceKey<SeasonCycle> COLD = createKey("cold");
    public static final ResourceKey<SeasonCycle> HOT = createKey("hot");

    private static ResourceKey<SeasonCycle> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_CYCLE, EclipticSeasons.rl(name));
    }

    @SafeVarargs
    private static <T> HolderSet<T> and(HolderSet<T>... values) {
        return new AndHolderSet<>(values);
    }

    @SafeVarargs
    private static <T> HolderSet<T> or(HolderSet<T>... values) {
        return new OrHolderSet<>(values);
    }

    private static HolderSet<Biome> not(AgroClimateRegistry.BiomeRegistryLookup<Biome> biomeRegistryLookup, HolderSet<Biome> value) {
        return new NotHolderSet<>(biomeRegistryLookup, value);
    }

    public static void bootstrap(BootstrapContext<SeasonCycle> context) {
        HolderGetter<Biome> getter = context.lookup(Registries.BIOME);
        HolderGetter<SeasonPhase> lookuped = context.lookup(ESRegistries.SEASON_PHASE);
        AgroClimateRegistry.BiomeRegistryLookup<Biome> lazyLookup = new AgroClimateRegistry.BiomeRegistryLookup<>(getter,Registries.BIOME);

        context.register(MONSOON, new SeasonCycle(
                // and( getter.getOrThrow(Tags.Biomes.IS_HOT_OVERWORLD),
                //         not(lazyLookup,getter.getOrThrow(Tags.Biomes.IS_DESERT))),
                getter.getOrThrow(ClimateTypeBiomeTags.MONSOONAL),
                SolarTermValueMap.<Holder<SeasonPhase>>builder()
                        // 冬季（旱季）
                        .putSolarTerm(SolarTerm.BEGINNING_OF_WINTER, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_START))
                        .putSolarTerm(SolarTerm.LIGHT_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_START))
                        .putSolarTerm(SolarTerm.HEAVY_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_START))
                        .putSolarTerm(SolarTerm.WINTER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_START))
                        .putSolarTerm(SolarTerm.LESSER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_MIDDLE))
                        .putSolarTerm(SolarTerm.GREATER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_MIDDLE))

                        // 春季（旱季）
                        .putSolarTerm(SolarTerm.BEGINNING_OF_SPRING, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_MIDDLE))
                        .putSolarTerm(SolarTerm.RAIN_WATER, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_MIDDLE))
                        .putSolarTerm(SolarTerm.INSECTS_AWAKENING, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_END))
                        .putSolarTerm(SolarTerm.SPRING_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_END))
                        .putSolarTerm(SolarTerm.FRESH_GREEN, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_END))
                        .putSolarTerm(SolarTerm.GRAIN_RAIN, lookuped.getOrThrow(SeasonPhaseRegistry.DRY_END))

                        // 夏季（雨季）
                        .putSolarTerm(SolarTerm.BEGINNING_OF_SUMMER, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_START))
                        .putSolarTerm(SolarTerm.LESSER_FULLNESS, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_MIDDLE))
                        .putSolarTerm(SolarTerm.GRAIN_IN_EAR, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_MIDDLE))
                        .putSolarTerm(SolarTerm.SUMMER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_END))
                        .putSolarTerm(SolarTerm.LESSER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_END))
                        .putSolarTerm(SolarTerm.GREATER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_END))

                        // 秋季（雨季）
                        .putSolarTerm(SolarTerm.BEGINNING_OF_AUTUMN, lookuped.getOrThrow(SeasonPhaseRegistry.RAIN_END))
                        .putSolarTerm(SolarTerm.END_OF_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.WET_START))
                        .putSolarTerm(SolarTerm.WHITE_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.WET_START))
                        .putSolarTerm(SolarTerm.AUTUMNAL_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.WET_MIDDLE))
                        .putSolarTerm(SolarTerm.COLD_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.WET_MIDDLE))
                        .putSolarTerm(SolarTerm.FIRST_FROST, lookuped.getOrThrow(SeasonPhaseRegistry.WET_END))
                        .build()
        ));

        // context.register(RAINY, new SeasonCycle(
        //         and(getter.getOrThrow(Tags.Biomes.IS_HOT_OVERWORLD),
        //                 getter.getOrThrow(Tags.Biomes.IS_WET_OVERWORLD),
        //                 not(lazyLookup, getter.getOrThrow(ClimateTypeBiomeTags.MONSOONAL)))
        //         ,
        //         SolarTermValueMap.
        //                 <Holder<SeasonPhase>>builder().defaultValue(lookuped.getOrThrow(SeasonPhaseRegistry.RAIN)).build()
        // ));
        //
        // context.register(DESERT, new SeasonCycle(
        //         getter.getOrThrow(Tags.Biomes.IS_DESERT),
        //         SolarTermValueMap.
        //                 <Holder<SeasonPhase>>builder().defaultValue(lookuped.getOrThrow(SeasonPhaseRegistry.DRY)).build()
        // ));

        context.register(COLD, new SeasonCycle(
                getter.getOrThrow(ClimateTypeBiomeTags.COLD_REGION),
                SolarTermValueMap.
                        <Holder<SeasonPhase>>builder()
                        .putSolarTerm(SolarTerm.BEGINNING_OF_SPRING, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_BEGINNING_OF_SPRING))
                        .putSolarTerm(SolarTerm.RAIN_WATER, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_RAIN_WATER))
                        .putSolarTerm(SolarTerm.INSECTS_AWAKENING, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_INSECTS_AWAKENING))
                        .putSolarTerm(SolarTerm.SPRING_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_SPRING_EQUINOX))
                        .putSolarTerm(SolarTerm.FRESH_GREEN, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_FRESH_GREEN))
                        .putSolarTerm(SolarTerm.GRAIN_RAIN, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_GRAIN_RAIN))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_SUMMER, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_BEGINNING_OF_SUMMER))
                        .putSolarTerm(SolarTerm.LESSER_FULLNESS, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_LESSER_FULLNESS))
                        .putSolarTerm(SolarTerm.GRAIN_IN_EAR, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_GRAIN_IN_EAR))
                        .putSolarTerm(SolarTerm.SUMMER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_SUMMER_SOLSTICE))
                        .putSolarTerm(SolarTerm.LESSER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_LESSER_HEAT))
                        .putSolarTerm(SolarTerm.GREATER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_GREATER_HEAT))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_AUTUMN, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_BEGINNING_OF_AUTUMN))
                        .putSolarTerm(SolarTerm.END_OF_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_END_OF_HEAT))
                        .putSolarTerm(SolarTerm.WHITE_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_WHITE_DEW))
                        .putSolarTerm(SolarTerm.AUTUMNAL_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_AUTUMNAL_EQUINOX))
                        .putSolarTerm(SolarTerm.COLD_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_COLD_DEW))
                        .putSolarTerm(SolarTerm.FIRST_FROST, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_FIRST_FROST))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_WINTER, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_BEGINNING_OF_WINTER))
                        .putSolarTerm(SolarTerm.LIGHT_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_LIGHT_SNOW))
                        .putSolarTerm(SolarTerm.HEAVY_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_HEAVY_SNOW))
                        .putSolarTerm(SolarTerm.WINTER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_WINTER_SOLSTICE))
                        .putSolarTerm(SolarTerm.LESSER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_LESSER_COLD))
                        .putSolarTerm(SolarTerm.GREATER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.COLD_GREATER_COLD))
                        .build()
        ));

        context.register(HOT, new SeasonCycle(
                and(getter.getOrThrow(ClimateTypeBiomeTags.HOT_REGION),
                        not(lazyLookup, getter.getOrThrow(ClimateTypeBiomeTags.MONSOONAL))),
                SolarTermValueMap.
                        <Holder<SeasonPhase>>builder()
                        .putSolarTerm(SolarTerm.BEGINNING_OF_SPRING, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_BEGINNING_OF_SPRING))
                        .putSolarTerm(SolarTerm.RAIN_WATER, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_RAIN_WATER))
                        .putSolarTerm(SolarTerm.INSECTS_AWAKENING, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_INSECTS_AWAKENING))
                        .putSolarTerm(SolarTerm.SPRING_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_SPRING_EQUINOX))
                        .putSolarTerm(SolarTerm.FRESH_GREEN, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_FRESH_GREEN))
                        .putSolarTerm(SolarTerm.GRAIN_RAIN, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_GRAIN_RAIN))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_SUMMER, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_BEGINNING_OF_SUMMER))
                        .putSolarTerm(SolarTerm.LESSER_FULLNESS, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_LESSER_FULLNESS))
                        .putSolarTerm(SolarTerm.GRAIN_IN_EAR, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_GRAIN_IN_EAR))
                        .putSolarTerm(SolarTerm.SUMMER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_SUMMER_SOLSTICE))
                        .putSolarTerm(SolarTerm.LESSER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_LESSER_HEAT))
                        .putSolarTerm(SolarTerm.GREATER_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_GREATER_HEAT))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_AUTUMN, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_BEGINNING_OF_AUTUMN))
                        .putSolarTerm(SolarTerm.END_OF_HEAT, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_END_OF_HEAT))
                        .putSolarTerm(SolarTerm.WHITE_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_WHITE_DEW))
                        .putSolarTerm(SolarTerm.AUTUMNAL_EQUINOX, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_AUTUMNAL_EQUINOX))
                        .putSolarTerm(SolarTerm.COLD_DEW, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_COLD_DEW))
                        .putSolarTerm(SolarTerm.FIRST_FROST, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_FIRST_FROST))

                        .putSolarTerm(SolarTerm.BEGINNING_OF_WINTER, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_BEGINNING_OF_WINTER))
                        .putSolarTerm(SolarTerm.LIGHT_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_LIGHT_SNOW))
                        .putSolarTerm(SolarTerm.HEAVY_SNOW, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_HEAVY_SNOW))
                        .putSolarTerm(SolarTerm.WINTER_SOLSTICE, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_WINTER_SOLSTICE))
                        .putSolarTerm(SolarTerm.LESSER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_LESSER_COLD))
                        .putSolarTerm(SolarTerm.GREATER_COLD, lookuped.getOrThrow(SeasonPhaseRegistry.HOT_GREATER_COLD))
                        .build()
        ));
    }
}

package com.teamtea.eclipticseasons.common.registry;


import com.google.common.collect.ImmutableMap;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.CropClimateTags;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.*;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CropRegistry {


    public static final ResourceKey<CropGrowControlBuilder> SPRING = createKey(CropSeasonType.SPRING);
    public static final ResourceKey<CropGrowControlBuilder> SUMMER = createKey(CropSeasonType.SUMMER);
    public static final ResourceKey<CropGrowControlBuilder> AUTUMN = createKey(CropSeasonType.AUTUMN);
    public static final ResourceKey<CropGrowControlBuilder> WINTER = createKey(CropSeasonType.WINTER);
    public static final ResourceKey<CropGrowControlBuilder> SP_SU = createKey(CropSeasonType.SP_SU);
    public static final ResourceKey<CropGrowControlBuilder> SP_AU = createKey(CropSeasonType.SP_AU);
    public static final ResourceKey<CropGrowControlBuilder> SP_WI = createKey(CropSeasonType.SP_WI);
    public static final ResourceKey<CropGrowControlBuilder> SU_AU = createKey(CropSeasonType.SU_AU);
    public static final ResourceKey<CropGrowControlBuilder> SU_WI = createKey(CropSeasonType.SU_WI);
    public static final ResourceKey<CropGrowControlBuilder> AU_WI = createKey(CropSeasonType.AU_WI);
    public static final ResourceKey<CropGrowControlBuilder> SP_SU_AU = createKey(CropSeasonType.SP_SU_AU);
    public static final ResourceKey<CropGrowControlBuilder> SP_SU_WI = createKey(CropSeasonType.SP_SU_WI);
    public static final ResourceKey<CropGrowControlBuilder> SP_AU_WI = createKey(CropSeasonType.SP_AU_WI);
    public static final ResourceKey<CropGrowControlBuilder> SU_AU_WI = createKey(CropSeasonType.SU_AU_WI);
    public static final ResourceKey<CropGrowControlBuilder> ALL = createKey(CropSeasonType.ALL);


    public static final ResourceKey<CropGrowControlBuilder> ARID = createKey(CropHumidityType.ARID);
    public static final ResourceKey<CropGrowControlBuilder> ARID_DRY = createKey(CropHumidityType.ARID_DRY);
    public static final ResourceKey<CropGrowControlBuilder> ARID_AVERAGE = createKey(CropHumidityType.ARID_AVERAGE);
    public static final ResourceKey<CropGrowControlBuilder> ARID_MOIST = createKey(CropHumidityType.ARID_MOIST);
    public static final ResourceKey<CropGrowControlBuilder> ARID_HUMID = createKey(CropHumidityType.ARID_HUMID);
    public static final ResourceKey<CropGrowControlBuilder> DRY = createKey(CropHumidityType.DRY);
    public static final ResourceKey<CropGrowControlBuilder> DRY_AVERAGE = createKey(CropHumidityType.DRY_AVERAGE);
    public static final ResourceKey<CropGrowControlBuilder> DRY_MOIST = createKey(CropHumidityType.DRY_MOIST);
    public static final ResourceKey<CropGrowControlBuilder> DRY_HUMID = createKey(CropHumidityType.DRY_HUMID);
    public static final ResourceKey<CropGrowControlBuilder> AVERAGE = createKey(CropHumidityType.AVERAGE);
    public static final ResourceKey<CropGrowControlBuilder> AVERAGE_MOIST = createKey(CropHumidityType.AVERAGE_MOIST);
    public static final ResourceKey<CropGrowControlBuilder> AVERAGE_HUMID = createKey(CropHumidityType.AVERAGE_HUMID);
    public static final ResourceKey<CropGrowControlBuilder> MOIST = createKey(CropHumidityType.MOIST);
    public static final ResourceKey<CropGrowControlBuilder> MOIST_HUMID = createKey(CropHumidityType.MOIST_HUMID);
    public static final ResourceKey<CropGrowControlBuilder> HUMID = createKey(CropHumidityType.HUMID);

    public static <K extends Enum<K>, V> Enum2ObjectMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
            K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15,
            K k16, V v16, K k17, V v17, K k18, V v18, K k19, V v19, K k20, V v20,
            K k21, V v21, K k22, V v22, K k23, V v23, K k24, V v24) {
        if (k1 == null) {
            throw new IllegalArgumentException("First key cannot be null");
        }
        Class<K> enumType = k1.getDeclaringClass();
        Enum2ObjectMap<K, V> map = new Enum2ObjectMap<>(enumType);
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

    private static ResourceKey<CropGrowControlBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.CROP, EclipticSeasons.rl(name));
    }

    public static ResourceKey<CropGrowControlBuilder> createKey(CropSeasonType name) {
        return ResourceKey.create(ESRegistries.CROP, EclipticSeasons.rl("seasons/" + name.getRes().getPath().split("/")[1]));
    }

    public static ResourceKey<CropGrowControlBuilder> createKey(CropHumidityType name) {
        return ResourceKey.create(ESRegistries.CROP, EclipticSeasons.rl("humidity/" + name.getRes().getPath().split("/")[1]));
    }

    private static TagKey<Block> createTagKey(ResourceKey<CropGrowControlBuilder> templateName) {
        return TagKey.create(Registries.BLOCK, EclipticSeasons.rl("crops/" + templateName.identifier().getPath().split("/")[1]));
    }

    private static BlockPredicate createTagPredicate(HolderGetter<Block> blockHolderGetter, ResourceKey<CropGrowControlBuilder> templateName) {
        return BlockPredicate.Builder.block().of(blockHolderGetter,
                        createTagKey(templateName))
                // .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.AGE_3,1))
                .build();
    }


    public static void bootstrap(BootstrapContext<CropGrowControlBuilder> context) {
        final Enum2ObjectMap<SolarTerm, GrowParameter> solarTermListEmpty = new Enum2ObjectMap<>(SolarTerm.class);
        final Enum2ObjectMap<Season, GrowParameter> seasonListEmpty = new Enum2ObjectMap<>(Season.class);
        final Enum2ObjectMap<Humidity, GrowParameter> humidListEmpty = new Enum2ObjectMap<>(Humidity.class);

        Optional<BlockState> empty = Optional.empty();
        Optional<GrowParameter> emptyGP = Optional.empty();
        Optional<GrowParameter> emptyGP2 = Optional.empty();

        Optional<BlockPredicate> emptyBP = Optional.empty();

        var blockHolderGetter = context.lookup(Registries.BLOCK);
        var biomeHolderGetter = context.lookup(Registries.BIOME);
        var cropGetter = context.lookup(ESRegistries.CROP);
        var cropClimateTypeHolderGetter = context.lookup(ESRegistries.AGRO_CLIMATE);

        HolderSet.Direct<AgroClimaticZone> temperate = HolderSet.direct(cropClimateTypeHolderGetter.getOrThrow(AgroClimateRegistry.TEMPERATE));

        // OrHolderSet<Block> snowAndIce = new OrHolderSet<>(blockHolderGetter.getOrThrow(BlockTags.ICE), blockHolderGetter.getOrThrow(BlockTags.SNOW));
        Optional<BlockPredicate> icePredicate = Optional.of(new BlockPredicate(Optional.of(blockHolderGetter.getOrThrow(BlockTags.ICE)), Optional.empty(), Optional.empty(), DataComponentMatchers.ANY));

        context.register(SPRING, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SPRING),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1.25f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.9f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(SUMMER, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SUMMER),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.1f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.7f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(AUTUMN, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, AUTUMN),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.97f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.75f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(WINTER, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter,WINTER),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.2f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.7f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(SP_SU, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_SU),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.85f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.7f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.05f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.05f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.05f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.05f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(SP_AU, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_AU),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.64f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.85f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.5f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(SP_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.64f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.95f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.12f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.25f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.6f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(SU_AU, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SU_AU),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.25f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.3f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.7f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.6f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(SU_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SU_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.01f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.02f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.2f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.85f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.05f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.2f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.6f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(AU_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, AU_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.15f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.1f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.05f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.64f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.75f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.9f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.75f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(SP_SU_AU, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_SU_AU),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.35f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.64f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.95f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.8f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.95f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.5f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.0f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                icePredicate
        ));

        context.register(SP_SU_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_SU_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(1.0f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.8f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.1f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.55f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(SP_AU_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SP_AU_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(1.025f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1.01f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.8f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.25f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.0f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.45f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.7f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.65f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.55f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.45f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(SU_AU_WI, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, SU_AU_WI),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.0f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.1f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.2f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(0.3f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.3f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.9f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.85f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.6f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.4f).end()
                )),

                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        context.register(ALL, new CropGrowControlBuilder(
                temperate,
                createTagPredicate(blockHolderGetter, ALL),
                HolderSet.empty(), emptyGP, emptyGP2,
                new Enum2ObjectMap<>(of(
                        SolarTerm.BEGINNING_OF_SPRING, GrowParameter.builder().growChance(0.45f).end(),
                        SolarTerm.RAIN_WATER, GrowParameter.builder().growChance(0.55f).end(),
                        SolarTerm.INSECTS_AWAKENING, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.SPRING_EQUINOX, GrowParameter.builder().growChance(0.85f).end(),
                        SolarTerm.FRESH_GREEN, GrowParameter.builder().growChance(1f).end(),
                        SolarTerm.GRAIN_RAIN, GrowParameter.builder().growChance(0.95f).end(),

                        SolarTerm.BEGINNING_OF_SUMMER, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.LESSER_FULLNESS, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GRAIN_IN_EAR, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.SUMMER_SOLSTICE, GrowParameter.builder().growChance(1.0f).end(),
                        SolarTerm.LESSER_HEAT, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.GREATER_HEAT, GrowParameter.builder().growChance(0.8f).end(),

                        SolarTerm.BEGINNING_OF_AUTUMN, GrowParameter.builder().growChance(0.9f).end(),
                        SolarTerm.END_OF_HEAT, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.WHITE_DEW, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.AUTUMNAL_EQUINOX, GrowParameter.builder().growChance(0.8f).end(),
                        SolarTerm.COLD_DEW, GrowParameter.builder().growChance(0.7f).end(),
                        SolarTerm.FIRST_FROST, GrowParameter.builder().growChance(0.6f).end(),

                        SolarTerm.BEGINNING_OF_WINTER, GrowParameter.builder().growChance(0.5f).end(),
                        SolarTerm.LIGHT_SNOW, GrowParameter.builder().growChance(0.48f).end(),
                        SolarTerm.HEAVY_SNOW, GrowParameter.builder().growChance(0.45f).end(),
                        SolarTerm.WINTER_SOLSTICE, GrowParameter.builder().growChance(0.4f).end(),
                        SolarTerm.LESSER_COLD, GrowParameter.builder().growChance(0.37f).end(),
                        SolarTerm.GREATER_COLD, GrowParameter.builder().growChance(0.35f).end()
                )),
                seasonListEmpty,
                humidListEmpty,
                emptyBP
        ));

        HolderSet.Named<AgroClimaticZone> allHolderSet = cropClimateTypeHolderGetter.get(CropClimateTags.ALL).get();
        context.register(ARID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, ARID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(1.01f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.5f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.15f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0f).fertileChance(0.8f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0f).fertileChance(0.5f).end()
                )),
                emptyBP
        ));

        context.register(ARID_DRY, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, ARID_DRY),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.99f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(1f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.1f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0f).end()
                )),
                emptyBP
        ));

        context.register(ARID_AVERAGE, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, ARID_AVERAGE),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(1.01f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.1f).end()
                )),
                emptyBP
        ));

        context.register(ARID_MOIST, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, ARID_MOIST),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.9f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.9f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.35f).end()
                )),
                emptyBP
        ));

        context.register(ARID_HUMID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, ARID_HUMID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.8f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.85f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(1f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.95f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.8f).end()
                )),
                emptyBP
        ));

        context.register(DRY, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, DRY),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.4f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.45f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.15f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0f).end()
                )),
                emptyBP
        ));

        context.register(DRY_AVERAGE, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, DRY_AVERAGE),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.3f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.4f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.1f).end()
                )),
                emptyBP
        ));

        context.register(DRY_MOIST, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, DRY_MOIST),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.95f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.95f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.35f).end()
                )),
                emptyBP
        ));

        context.register(DRY_HUMID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, DRY_HUMID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.25f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.9f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(1f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(1f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.9f).end()
                )),
                emptyBP
        ));

        context.register(AVERAGE, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, AVERAGE),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.07f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.45f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.45f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.07f).end()
                )),
                emptyBP
        ));

        context.register(AVERAGE_MOIST, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, AVERAGE_MOIST),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.1f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.97f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.4f).end()
                )),
                emptyBP
        ));

        context.register(AVERAGE_HUMID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, AVERAGE_HUMID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0.05f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.95f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.95f).end()
                )),
                emptyBP
        ));

        context.register(MOIST, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, MOIST),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0).fertileChance(0.8f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.15f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.45f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(1.025f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(0.5f).end()
                )),
                emptyBP
        ));

        context.register(MOIST_HUMID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, MOIST_HUMID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0).fertileChance(0.8f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0.1f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.35f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(1f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(1f).end()
                )),
                emptyBP
        ));

        context.register(HUMID, new CropGrowControlBuilder(
                allHolderSet,
                createTagPredicate(blockHolderGetter, HUMID),
                HolderSet.empty(), emptyGP, emptyGP2,
                solarTermListEmpty,
                seasonListEmpty,
                new Enum2ObjectMap<>(ImmutableMap.of(
                        Humidity.ARID, GrowParameter.builder().growChance(0f).fertileChance(0.5f).end(),
                        Humidity.DRY, GrowParameter.builder().growChance(0f).fertileChance(0.8f).end(),
                        Humidity.AVERAGE, GrowParameter.builder().growChance(0.3f).end(),
                        Humidity.MOIST, GrowParameter.builder().growChance(0.6f).end(),
                        Humidity.HUMID, GrowParameter.builder().growChance(1.025f).end()
                )),
                emptyBP
        ));


        // context.register(createKey("wheat_test"), new CropGrowControlBuilder(
        //         temperate,
        //         BlockPredicate.Builder.block().of(Blocks.SUNFLOWER)
        //                 .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
        //                 .build(),
        //         HolderSet.direct(cropGetter.getOrThrow(SP_SU)
        //         ,cropGetter.getOrThrow(AVERAGE_MOIST)
        //         ), emptyGP, emptyGP2,
        //         new Enum2ObjectMap<>(SolarTerm.class),
        //         seasonListEmpty,
        //         humidListEmpty,
        //         Optional.empty()
        // ));

    }
}

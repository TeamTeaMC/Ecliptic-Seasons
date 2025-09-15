package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.api.misc.RegistryFilter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClimateTypeFilters {

    // agro
    public static final RegistryFilter<Biome> WARM_REGION = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_OVERWORLD);
    public static final RegistryFilter<Biome> HOT_REGION = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_HOT_OVERWORLD);
    public static final RegistryFilter<Biome> COLD_REGION =
            new RegistryFilter.And<>(new RegistryFilter.Or<>(Tags.Biomes.IS_MOUNTAIN_PEAK, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY),
                    new RegistryFilter.Or<>(Tags.Biomes.IS_OVERWORLD)
            );

    // biome rain
    public static final RegistryFilter<Biome> SEASONAL = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_OVERWORLD);
    public static final RegistryFilter<Biome> SEASONAL_HOT = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_HOT_OVERWORLD);
    public static final RegistryFilter<Biome> SEASONAL_COLD =
            new RegistryFilter.And<>(new RegistryFilter.Or<>(Tags.Biomes.IS_MOUNTAIN_PEAK, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY),
                    new RegistryFilter.Or<>(Tags.Biomes.IS_OVERWORLD)
            );
    public static final RegistryFilter<Biome> MONSOONAL = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_SAVANNA);
    public static final RegistryFilter<Biome> RAINLESS = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_CAVE);
    public static final RegistryFilter<Biome> ARID = new RegistryFilter.Or<>(Tags.Biomes.IS_BADLANDS, Tags.Biomes.IS_DESERT);
    public static final RegistryFilter<Biome> DROUGHTY = biomes -> List.of();
    public static final RegistryFilter<Biome> SOFT = new RegistryFilter.Or<>(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OCEAN);
    public static final RegistryFilter<Biome> RAINY = new RegistryFilter.DirectHolder<>(Biomes.JUNGLE);

    public static final RegistryFilter<Biome> IS_SMALL = new RegistryFilter.TagHolder<>(Tags.Biomes.IS_RIVER);

    // biome color
    public static final RegistryFilter<Biome> SEASONAL_COLOR_CHANGE = SEASONAL;
    public static final RegistryFilter<Biome> SEASONAL_HOT_COLOR_CHANGE = SEASONAL_HOT;
    public static final RegistryFilter<Biome> SEASONAL_COLD_COLOR_CHANGE = SEASONAL_COLD;

    public static final RegistryFilter<Biome> MONSOONAL_COLOR_CHANGE = MONSOONAL;
    public static final RegistryFilter<Biome> NONE_COLOR_CHANGE =
            new RegistryFilter.Or<>(Tags.Biomes.IS_CAVE, Tags.Biomes.IS_BADLANDS,
                    Tags.Biomes.IS_DESERT, Tags.Biomes.IS_VOID
            );

    public static final RegistryFilter<Biome> SLIGHTLY_COLOR_CHANGE =
            new RegistryFilter.Or<>(new RegistryFilter.TagHolder<>(Tags.Biomes.IS_BEACH),
                    new RegistryFilter.TagHolder<>(Tags.Biomes.IS_OCEAN),
                    new RegistryFilter.DirectHolder<>(Biomes.JUNGLE));


    // ====================================================
    // map use
    // ====================================================


    public static final Map<TagKey<Biome>, RegistryFilter<Biome>> OVERWORLD_AGRO_BIOME_PRESENT = new LinkedHashMap<>() {{
        put(ClimateTypeBiomeTags.HOT_REGION, ClimateTypeFilters.HOT_REGION);
        put(ClimateTypeBiomeTags.COLD_REGION, ClimateTypeFilters.COLD_REGION);
        put(ClimateTypeBiomeTags.WARM_REGION, ClimateTypeFilters.WARM_REGION);
    }};

    public static final Map<TagKey<Biome>, RegistryFilter<Biome>> BIOME_PRESENT = new LinkedHashMap<>() {{
        put(ClimateTypeBiomeTags.RAINLESS, ClimateTypeFilters.RAINLESS);
        put(ClimateTypeBiomeTags.ARID, ClimateTypeFilters.ARID);
        put(ClimateTypeBiomeTags.DROUGHTY, ClimateTypeFilters.DROUGHTY);
        put(ClimateTypeBiomeTags.SOFT, ClimateTypeFilters.SOFT);
        put(ClimateTypeBiomeTags.RAINY, ClimateTypeFilters.RAINY);
        put(ClimateTypeBiomeTags.MONSOONAL, ClimateTypeFilters.MONSOONAL);
        put(ClimateTypeBiomeTags.SEASONAL_HOT, ClimateTypeFilters.SEASONAL_HOT);
        put(ClimateTypeBiomeTags.SEASONAL_COLD, ClimateTypeFilters.SEASONAL_COLD);
        put(ClimateTypeBiomeTags.SEASONAL, ClimateTypeFilters.SEASONAL);

    }};


    @Deprecated(forRemoval = true)
    public static final Map<TagKey<Biome>, RegistryFilter<Biome>> SMALL_BIOME_PRESENT =
            new LinkedHashMap<>(Map.of(
                    ClimateTypeBiomeTags.IS_SMALL, ClimateTypeFilters.IS_SMALL
            ));

    public static final Map<TagKey<Biome>, RegistryFilter<Biome>> COLOR_BIOME_PRESENT = new LinkedHashMap<>() {{
        put(ClimateTypeBiomeTags.NONE_COLOR_CHANGE, ClimateTypeFilters.NONE_COLOR_CHANGE);
        put(ClimateTypeBiomeTags.SLIGHTLY_COLOR_CHANGE, ClimateTypeFilters.SLIGHTLY_COLOR_CHANGE);
        put(ClimateTypeBiomeTags.MONSOONAL_COLOR_CHANGE, ClimateTypeFilters.MONSOONAL_COLOR_CHANGE);
        put(ClimateTypeBiomeTags.SEASONAL_HOT_COLOR_CHANGE, ClimateTypeFilters.SEASONAL_HOT_COLOR_CHANGE);
        put(ClimateTypeBiomeTags.SEASONAL_COLD_COLOR_CHANGE, ClimateTypeFilters.SEASONAL_COLD_COLOR_CHANGE);
        put(ClimateTypeBiomeTags.SEASONAL_COLOR_CHANGE, ClimateTypeFilters.SEASONAL_COLOR_CHANGE);
    }};

}

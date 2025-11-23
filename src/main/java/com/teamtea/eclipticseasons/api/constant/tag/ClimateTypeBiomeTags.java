package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class ClimateTypeBiomeTags {

    // Agro
    public static final TagKey<Biome> COLD_REGION = create("agro/cold");
    public static final TagKey<Biome> WARM_REGION = create("agro/warm");
    public static final TagKey<Biome> HOT_REGION = create("agro/hot");

    // Biome Rain
    public static final TagKey<Biome> SEASONAL = create("rain/seasonal");
    public static final TagKey<Biome> SEASONAL_COLD = create("rain/seasonal/cold");
    public static final TagKey<Biome> SEASONAL_HOT = create("rain/seasonal/hot");
    public static final TagKey<Biome> MONSOONAL = create("rain/monsoonal");
    public static final TagKey<Biome> RAINLESS = create("rain/rainless");
    public static final TagKey<Biome> ARID = create("rain/arid");
    public static final TagKey<Biome> DROUGHTY = create("rain/droughty");
    public static final TagKey<Biome> SOFT = create("rain/soft");
    public static final TagKey<Biome> RAINY = create("rain/rainy");

    // Biome Color
    public static final TagKey<Biome> NONE_COLOR_CHANGE = create("color/stable");
    public static final TagKey<Biome> SLIGHTLY_COLOR_CHANGE = create("color/slightly");
    public static final TagKey<Biome> MONSOONAL_COLOR_CHANGE = create("color/monsoonal");
    public static final TagKey<Biome> SEASONAL_COLOR_CHANGE = create("color/seasonal");
    public static final TagKey<Biome> SEASONAL_COLD_COLOR_CHANGE = create("color/seasonal/cold");
    public static final TagKey<Biome> SEASONAL_HOT_COLOR_CHANGE = create("color/seasonal/hot");

    public static final List<TagKey<Biome>> OVERWORLD_AGRO_BIOME_TYPES = List.of(COLD_REGION, WARM_REGION, HOT_REGION);

    public static final List<TagKey<Biome>> BIOME_COLOR_TYPES = List.of(NONE_COLOR_CHANGE, SLIGHTLY_COLOR_CHANGE, MONSOONAL_COLOR_CHANGE, SEASONAL_HOT_COLOR_CHANGE, SEASONAL_COLD_COLOR_CHANGE, SEASONAL_COLOR_CHANGE);
    public static final List<TagKey<Biome>> BIOME_TYPES = List.of(RAINLESS, ARID, DROUGHTY, SOFT, RAINY, MONSOONAL, SEASONAL_HOT, SEASONAL_COLD, SEASONAL);
    public static final List<TagKey<Biome>> COMMON_BIOME_TYPES = List.of(RAINLESS, ARID, DROUGHTY, SOFT, RAINY);


    public static final TagKey<Biome> IS_SMALL = ClimateTypeBiomeTags.create("is_small");

    public static final TagKey<Biome> EXTREME_COLD = ClimateTypeBiomeTags.create("extreme_cold");

    public static TagKey<Biome> create(String s) {
        return TagKey.create(Registries.BIOME, EclipticSeasons.rl(s));
    }
}

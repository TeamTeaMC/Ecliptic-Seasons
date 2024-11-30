package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class ClimateTypeBiomeTags {
    public static final TagKey<Biome> SEASONAL = create("seasonal");
    public static final TagKey<Biome> MONSOONAL = create("monsoonal");
    public static final TagKey<Biome> RAINLESS = create("rainless");
    public static final TagKey<Biome> ARID = create("arid");
    public static final TagKey<Biome> DROUGHTY = create("droughty");
    public static final TagKey<Biome> SOFT = create("soft");
    public static final TagKey<Biome> RAINY = create("rainy");
    public static final List<TagKey<Biome>> BIOME_TYPES = List.of(RAINLESS, ARID, DROUGHTY, SOFT, RAINY, MONSOONAL, SEASONAL);
    public static final List<TagKey<Biome>> COMMON_BIOME_TYPES = List.of(RAINLESS, ARID, DROUGHTY, SOFT, RAINY);

    public static final TagKey<Biome> IS_SMALL = ClimateTypeBiomeTags.create("is_small");

    public static TagKey<Biome> create(String s) {
        return TagKey.create(Registries.BIOME, EclipticSeasons.rl(s));
    }
}

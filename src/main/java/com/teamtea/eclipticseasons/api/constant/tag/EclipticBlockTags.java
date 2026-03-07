package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EclipticBlockTags {

    public static final TagKey<Block> NONE_FALLEN_LEAVES = create("none_fallen_leaves");
    public static final TagKey<Block> HABITAT_BUTTERFLY = create("habitat/butterfly");
    public static final TagKey<Block> HABITAT_FIREFLY = create("habitat/firefly");

    public static final TagKey<Block> SNOW_OVERLAY_CANNOT_SURVIVE_ON =  create("snow_overlay_cannot_survive_on");
    public static final TagKey<Block> SNOW_LAYER_CANNOT_SURVIVE_IN =  create("snow_layer_cannot_survive_in");

    public static final TagKey<Block> SOFT_HEAT_SOURCES =  create("soft_heat_sources");

    public static final TagKey<Block> DARK_GROW_PLANTS = create("dark_grow_plants");

    public static final TagKey<Block> NATURAL_PLANTS = create("natural_plants");
    public static final TagKey<Block> VOLATILE_PLANTS = create("volatile_plants");
    public static final TagKey<Block> VOLATILE = create("volatile");
    public static final TagKey<Block> UNAFFECTED_BY_SEASONS = create(("crops/unaffected_by_seasons"));
    public static final TagKey<Block> UNAFFECTED_BY_HUMIDITY = create(("crops/unaffected_by_humidity"));

    public static final TagKey<Block> NOT_KILLED_BY_CLIMATE = create(("crops/not_killed_by_climate"));


    public static TagKey<Block> create(String s) {
        return TagKey.create(Registries.BLOCK, EclipticSeasons.rl(s));
    }
}

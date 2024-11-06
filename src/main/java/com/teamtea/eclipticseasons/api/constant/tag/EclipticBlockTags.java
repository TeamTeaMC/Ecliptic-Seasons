package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EclipticBlockTags {

    public static final TagKey<Block> NONE_FALLEN_LEAVES = create("none_fallen_leaves");
    public static final TagKey<Block> HABITAT_BUTTERFLY = create("habitat/butterfly");
    public static final TagKey<Block> HABITAT_FIREFLY = create("habitat/firefly");
    public static TagKey<Block> create(String s) {
        return TagKey.create(Registries.BLOCK, EclipticSeasons.rl(s));
    }
}

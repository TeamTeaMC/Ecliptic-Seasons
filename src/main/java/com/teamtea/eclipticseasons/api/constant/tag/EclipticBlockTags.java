package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EclipticBlockTags {
    public static final TagKey<Block> SNOW_OVERLAY_CANNOT_SURVIVE_ON = create("snow_overlay_cannot_survive_on");
    public static final TagKey<Block> NOT_GREEN_HOUSE_MATERIAL = create("not_green_house_material");

    public static TagKey<Block> create(String s) {
        return TagKey.create(Registry.BLOCK_REGISTRY, EclipticSeasons.rl(s));
    }

}

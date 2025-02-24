package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;


public class EclipticBlockTags {

    public static final Tags.IOptionalNamedTag<Block> SNOW_OVERLAY_CANNOT_SURVIVE_ON = create("snow_overlay_cannot_survive_on");
    public static final Tags.IOptionalNamedTag<Block> NOT_GREEN_HOUSE_MATERIAL = create("not_green_house_material");

    public static Tags.IOptionalNamedTag<Block> create(String s) {
        return BlockTags.createOptional(EclipticSeasons.rl(s));
    }

}

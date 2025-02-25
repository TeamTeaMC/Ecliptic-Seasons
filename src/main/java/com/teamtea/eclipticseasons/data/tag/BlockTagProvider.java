package com.teamtea.eclipticseasons.data.tag;

import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;

import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagProvider extends BlockTagsProvider {


    public BlockTagProvider(DataGenerator generator, String modid, ExistingFileHelper helper) {
        super(generator, modid, helper);
    }

    @Override
    protected void addTags() {
        tag(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)
                .addTag(BlockTags.ICE).add(Blocks.SNOW).add(Blocks.SNOW_BLOCK);

        tag(EclipticBlockTags.NOT_GREEN_HOUSE_MATERIAL)
                .addTag(BlockTags.ICE).add(Blocks.SNOW).add(Blocks.SNOW_BLOCK);
    }
}

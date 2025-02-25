package com.teamtea.eclipticseasons.data.tag;

import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagProvider extends BlockTagsProvider {


    public BlockTagProvider(DataGenerator generator, String modid, ExistingFileHelper helper) {
        super(generator, modid, helper);
    }

    @Override
    protected void addTags() {
        tag(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)
                .addTag(BlockTags.ICE).addTag(BlockTags.SNOW).addTag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON);

        tag(EclipticBlockTags.NOT_GREEN_HOUSE_MATERIAL)
                .addTag(BlockTags.ICE).addTag(BlockTags.SNOW);
    }
}

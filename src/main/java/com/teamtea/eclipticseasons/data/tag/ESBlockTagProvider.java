package com.teamtea.eclipticseasons.data.tag;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


public final class ESBlockTagProvider extends BlockTagsProvider {
    public ESBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(EclipticBlockTags.NONE_FALLEN_LEAVES).add(Blocks.CHERRY_LEAVES,Blocks.SPRUCE_LEAVES);
        tag(EclipticBlockTags.HABITAT_BUTTERFLY).addTag(BlockTags.FLOWERS);
        tag(EclipticBlockTags.HABITAT_FIREFLY).addTag(BlockTags.SMALL_FLOWERS).add(Blocks.SHORT_GRASS, Blocks.TALL_GRASS);

        tag(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)
                .addTag(BlockTags.SNOW)
                .addTag(BlockTags.ICE)
                .addTag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON);


        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).addOptional(fd_rl("tomatoes"));

    }

    public ResourceLocation srl(String croptopia, String name) {
        return ResourceLocation.fromNamespaceAndPath(croptopia, name);
    }

    public ResourceLocation fd_rl(String name) {
        return srl("farmersdelight", name);
    }
}

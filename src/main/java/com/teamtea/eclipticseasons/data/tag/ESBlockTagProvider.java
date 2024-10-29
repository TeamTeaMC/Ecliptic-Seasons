package com.teamtea.eclipticseasons.data.tag;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.SeasonalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;


import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


public final class ESBlockTagProvider extends BlockTagsProvider {
    public ESBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(SeasonalBlockTags.NONE_FALLEN_LEAVES).add(Blocks.CHERRY_LEAVES);
        tag(SeasonalBlockTags.HABITAT_BUTTERFLY).addTag(BlockTags.FLOWERS);
        tag(SeasonalBlockTags.HABITAT_FIREFLY).addTag(BlockTags.SMALL_FLOWERS).add(Blocks.GRASS, Blocks.TALL_GRASS);

        tag(CropSeasonType.SUMMER.getBlockTag()).addOptional(fd_rl("tomatoes"));
        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).addOptional(fd_rl("tomatoes"));

    }



    public ResourceLocation fd_rl(String name) {
        return new ResourceLocation("farmersdelight", name);
    }
}

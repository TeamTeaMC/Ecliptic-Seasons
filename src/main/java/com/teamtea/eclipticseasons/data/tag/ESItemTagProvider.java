package com.teamtea.eclipticseasons.data.tag;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;


public final class ESItemTagProvider extends ItemTagsProvider {

    public ESItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture) {
        super(packOutput, providerCompletableFuture, tagLookupCompletableFuture);
    }


    @Override
    public String getName() {
        return EclipticSeasonsApi.MODID+ " Item Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(CropSeasonType.SUMMER.getTag()).add(Items.MELON_SEEDS, Items.COCOA_BEANS, Items.CACTUS);
        tag(CropSeasonType.AUTUMN.getTag()).add(Items.PUMPKIN_SEEDS);
        tag(CropSeasonType.SP_AU.getTag()).add(Items.POTATO, Items.BEETROOT_SEEDS, Items.CARROT);
        tag(CropSeasonType.SP_SU_AU.getTag()).add(Items.KELP, Items.TORCHFLOWER_SEEDS);
        tag(CropSeasonType.SP_SU.getTag()).add(Items.WHEAT_SEEDS).add(Items.SUGAR_CANE);
        tag(CropSeasonType.ALL.getTag()).add(Items.GLOW_BERRIES);
        tag(CropSeasonType.SP_WI.getTag()).add(Items.SWEET_BERRIES);
        tag(CropSeasonType.SPRING.getTag()).add(Items.BAMBOO);


        tag(CropHumidityType.DRY_AVERAGE.getTag()).add(Items.CACTUS);
        tag(CropHumidityType.DRY_MOIST.getTag()).add(Items.SWEET_BERRIES);
        tag(CropHumidityType.DRY_HUMID.getTag()).add(Items.MELON_SEEDS);
        tag(CropHumidityType.AVERAGE_HUMID.getTag()).add(Items.GLOW_BERRIES,Items.SUGAR_CANE);
        tag(CropHumidityType.AVERAGE_MOIST.getTag()).add(Items.WHEAT_SEEDS, Items.CARROT, Items.BEETROOT_SEEDS, Items.POTATO, Items.PUMPKIN_SEEDS);
        tag(CropHumidityType.AVERAGE_MOIST.getTag()).add(Items.COCOA_BEANS, Items.KELP, Items.TORCHFLOWER_SEEDS);
        tag(CropHumidityType.MOIST_HUMID.getTag()).add(Items.BAMBOO).add(Items.BROWN_MUSHROOM,Items.RED_MUSHROOM);

        // others
        tag(CropHumidityType.AVERAGE_MOIST.getTag()).addOptional(fd_rl("tomato_seeds"));
        tag(CropHumidityType.MOIST_HUMID.getTag()).addOptional(fd_rl("rice")).addOptional(fd_rl("brown_mushroom_colony")).addOptional(fd_rl("red_mushroom_colony"));

        for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
            tag(cropSeasonType.getTag());
        }
        for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
            tag(cropHumidityType.getTag());
        }

    }


    public ResourceLocation srl(String croptopia, String name) {
        return new ResourceLocation(croptopia, name);
    }

    public ResourceLocation fd_rl(String name) {
        return srl("farmersdelight", name);
    }
}

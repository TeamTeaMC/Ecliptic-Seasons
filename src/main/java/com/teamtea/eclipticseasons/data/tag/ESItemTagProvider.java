package com.teamtea.eclipticseasons.data.tag;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
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
        return "Tea the Story Item Tags";
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
        tag(CropSeasonType.SUMMER.getTag()).addOptional(fd_rl("rice")).addOptional(fd_rl("tomato_seeds"));
        tag(CropHumidityType.AVERAGE_MOIST.getTag()).addOptional(fd_rl("tomato_seeds"));
        tag(CropHumidityType.MOIST_HUMID.getTag()).addOptional(fd_rl("rice")).addOptional(fd_rl("brown_mushroom_colony")).addOptional(fd_rl("red_mushroom_colony"));
        tag(CropSeasonType.SP_AU.getTag()).addOptional(fd_rl("cabbage_seeds")).addOptional(fd_rl("onion"));


        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "cauliflower_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "broccoli_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "corn_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "cucumber_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "currant_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "eggplant_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "grape_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "hops_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "kale_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "olive_seed"));
        tag(CropSeasonType.SPRING.getTag()).addOptional(srl("croptopia", "peanut_seed"));

        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "rhubarb_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "rutabaga_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "squash_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "yam_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "brusselsprout_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "carrot_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "parsnip_seed"));
        tag(CropSeasonType.WINTER.getTag()).addOptional(srl("croptopia", "potato_seed"));


        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "artichoke_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "asparagus_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "bellpepper_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "cabbage_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "celery_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "garlic_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "leek_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "lettuce_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "radish_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "tomato_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "zucchini_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "oat_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "barley_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "soybean_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "mustard_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "turmeric_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "ginger_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "basil_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "blackberry_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "blueberry_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "cranberry_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "cantaloupe_seed"));
        tag(CropSeasonType.SU_AU.getTag()).addOptional(srl("croptopia", "elderberry_seed"));


        tag(CropSeasonType.SP_WI.getTag()).addOptional(srl("croptopia", "sweetpotato_seed"));

        tag(CropSeasonType.SP_AU.getTag()).addOptional(srl("croptopia", "chile_pepper_seed"));

        tag(CropSeasonType.SU_AU_WI.getTag()).addOptional(srl("croptopia", "spinach_seed"));
        tag(CropSeasonType.SU_AU_WI.getTag()).addOptional(srl("croptopia", "turnip_seed"));

    }


    public ResourceLocation srl(String croptopia, String name) {
        return new ResourceLocation(croptopia, name);
    }

    public ResourceLocation fd_rl(String name) {
        return srl("farmersdelight", name);
    }
}

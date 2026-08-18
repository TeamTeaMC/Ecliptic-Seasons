package com.teamtea.eclipticseasons.data.general.tag;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
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
        return "ES Item Tags";
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
        for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
            tag(cropSeasonType.getTag());
        }
        for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
            tag(cropHumidityType.getTag());
        }

        tag(CropHumidityType.AVERAGE_MOIST.getTag()).addOptional(fd_rl("tomato_seeds")).addOptional(fd_rl("cabbage_seeds")).addOptional(fd_rl("onion"));
        tag(CropHumidityType.MOIST_HUMID.getTag()).addOptional(fd_rl("rice")).addOptional(fd_rl("brown_mushroom_colony")).addOptional(fd_rl("red_mushroom_colony"));

        tag(ESItemTags.COOLING_ITEMS).add(Items.SNOWBALL, Items.SNOW_BLOCK, Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE);
        tag(ESItemTags.HEAT_PROTECTIVE_HELMETS);
        tag(ESItemTags.UNAFFECTED_BY_SEASONS);
        tag(ESItemTags.UNAFFECTED_BY_HUMIDITY);

        tag(ESItemTags.AGRICULTURE_CONTENT).add(
                ItemRegistry.growth_detector.get(),
                ItemRegistry.greenhouse_core_container_item.get(),
                ItemRegistry.spring_greenhouse_core_item.get(),
                ItemRegistry.summer_greenhouse_core_item.get(),
                ItemRegistry.autumn_greenhouse_core_item.get(),
                ItemRegistry.winter_greenhouse_core_item.get(),
                ItemRegistry.spring_greenhouse_essence_item.get(),
                ItemRegistry.summer_greenhouse_essence_item.get(),
                ItemRegistry.autumn_greenhouse_essence_item.get(),
                ItemRegistry.winter_greenhouse_essence_item.get(),
                ItemRegistry.seasonal_prayer_scroll_item.get(),
                ItemRegistry.block_in_wooden_grate_block_item.get(),
                ItemRegistry.humidity_tank_item.get(),
                ItemRegistry.dehumidifier_item.get()
                // ItemRegistry.calendar_item.get(),
                // ItemRegistry.season_sensor_item.get(),
                // ItemRegistry.broom.get(),
                // ItemRegistry.salt_wand.get(),
                // ItemRegistry.ice_wand.get(),
                // ItemRegistry.hygrometer.get(),
                // ItemRegistry.snowless_hometown.get(),
                // ItemRegistry.bamboo_wind_chimes_item.get(),
                // ItemRegistry.paper_wind_chimes_item.get(),
                // ItemRegistry.wind_chimes_item.get(),
                // ItemRegistry.pinwheel_orange_item.get(),
                // ItemRegistry.pinwheel_lime_item.get(),
                // ItemRegistry.pinwheel_blue_item.get()
        );
    }


    public ResourceLocation srl(String croptopia, String name) {
        return ResourceLocation.fromNamespaceAndPath(croptopia, name);
    }

    public ResourceLocation fd_rl(String name) {
        return srl("farmersdelight", name);
    }
}

package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ESItemModelProvider extends ItemModelProvider {


    public static final String GENERATED = "item/generated";
    public static final String HANDHELD = "item/handheld";

    public ESItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    private String blockName(BlockItem blockItem) {
        return BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).getPath();
    }

    @Override
    protected void registerModels() {
        addSimple(ItemRegistry.calendar_item.get());
        addSimple(ItemRegistry.broom.get());

        addSimple(ItemRegistry.wind_chimes_item.get());
        addSimple(ItemRegistry.paper_wind_chimes_item.get());
        addSimple(ItemRegistry.bamboo_wind_chimes_item.get());

        addSimple(ItemRegistry.pinwheel_blue_item.get(), "pinwheel_blue_item");
        addSimple(ItemRegistry.pinwheel_lime_item.get(), "pinwheel_lime_item");
        addSimple(ItemRegistry.pinwheel_orange_item.get(), "pinwheel_orange_item");

        addSimple(ItemRegistry.seasonal_prayer_scroll_item.get());
        addSimple(ItemRegistry.growth_detector.get());

        withExistingParent(resourceItem(ItemRegistry.block_in_wooden_grate_block_item.getId().getPath()).getPath(),
                resourceBlock(ItemRegistry.block_in_wooden_grate_block_item.getId().getPath()));

        withExistingParent(itemName(ItemRegistry.spring_greenhouse_essence_item.get()), resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.summer_greenhouse_essence_item.get()), resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.autumn_greenhouse_essence_item.get()), resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.winter_greenhouse_essence_item.get()), resourceItem("empty"));

    }

    public void addSimple(Item item) {
        withExistingParent(itemName(item), new ResourceLocation(GENERATED))
                .texture("layer0", resourceItem(itemName(item)));
    }

    public void addSimple(ItemLike item, String texture) {
        withExistingParent(itemName(item), new ResourceLocation(GENERATED))
                .texture("layer0", resourceItem(texture));
    }
    
    private String itemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private String itemName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public ResourceLocation resourceItem(String path) {
        return EclipticSeasons.rl("item/" + path);
    }

    public ResourceLocation resourceBlock(String path) {
        return EclipticSeasons.rl("block/" + path);
    }

}

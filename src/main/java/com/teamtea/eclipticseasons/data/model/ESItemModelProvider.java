package com.teamtea.eclipticseasons.data.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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
        addSimple(EclipticSeasons.ModContents.calendar_item.value());
        addSimple(EclipticSeasons.ModContents.wind_chimes_item.value());
        addSimple(EclipticSeasons.ModContents.paper_wind_chimes_item.value());
        addSimple(EclipticSeasons.ModContents.bamboo_wind_chimes_item.value());

        addSimple(EclipticSeasons.ModContents.pinwheel_blue_item.value(), "pinwheel_blue_item");
        addSimple(EclipticSeasons.ModContents.pinwheel_lime_item.value(), "pinwheel_lime_item");
        addSimple(EclipticSeasons.ModContents.pinwheel_orange_item.value(), "pinwheel_orange_item");

        withExistingParent(itemName(EclipticSeasons.ModContents.broom_item.value()), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", ResourceLocation.withDefaultNamespace("item/" + itemName(Items.STICK)));
        withExistingParent(itemName(EclipticSeasons.ModContents.snowy_maker_item.value()), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", ResourceLocation.withDefaultNamespace("item/" + itemName(Items.STICK)));
    }

    public void addSimple(Item item) {
        withExistingParent(itemName(item), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(itemName(item)));
    }

    public void addSimple(ItemLike item, String texture) {
        withExistingParent(itemName(item), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(texture));
    }

    private String itemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    public ResourceLocation resourceItem(String path) {
        return EclipticSeasons.rl("item/" + path);
    }


}

package com.teamtea.eclipticseasons.data.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.registry.ModContents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
        addSimple(ModContents.calendar_item.get());
        // addSimple(EclipticSeasons.ModContents.wind_chimes_item.value());
        // addSimple(EclipticSeasons.ModContents.paper_wind_chimes_item.value());
        // addSimple(EclipticSeasons.ModContents.bamboo_wind_chimes_item.value());
        //
        // withExistingParent(itemName(EclipticSeasons.ModContents.paper_wind_mill_item.value()),  new ResourceLocation(GENERATED))
        //         .texture("layer0",  new ResourceLocation("item/"+itemName(Items.STICK)));
        // withExistingParent(itemName(EclipticSeasons.ModContents.broom_item.value()),  new ResourceLocation(GENERATED))
        //         .texture("layer0",  new ResourceLocation("item/"+itemName(Items.STICK)));
        // withExistingParent(itemName(EclipticSeasons.ModContents.snowy_maker_item.value()),  new ResourceLocation(GENERATED))
        //         .texture("layer0",  new ResourceLocation("item/"+itemName(Items.STICK)));
    }

    public void addSimple(Item item) {
        withExistingParent(itemName(item), new ResourceLocation(GENERATED))
                .texture("layer0", resourceItem(itemName(item)));
    }

    private String itemName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public ResourceLocation resourceItem(String path) {
        return EclipticSeasons.rl("item/" + path);
    }


}

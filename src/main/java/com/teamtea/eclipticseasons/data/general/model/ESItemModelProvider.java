package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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
        addSimple(ItemRegistry.calendar_item.value());
        addSimple(ItemRegistry.wind_chimes_item.value());
        addSimple(ItemRegistry.paper_wind_chimes_item.value());
        addSimple(ItemRegistry.bamboo_wind_chimes_item.value());

        addSimple(ItemRegistry.broom.value());
        addSimple(ItemRegistry.ice_wand.value());

        addSimple(ItemRegistry.seasonal_prayer_scroll_item.value());
        addSimple(ItemRegistry.growth_detector.value());


        addSimple(ItemRegistry.pinwheel_blue_item.value(), "pinwheel_blue_item");
        addSimple(ItemRegistry.pinwheel_lime_item.value(), "pinwheel_lime_item");
        addSimple(ItemRegistry.pinwheel_orange_item.value(), "pinwheel_orange_item");

        addStandProperties(ItemRegistry.hygrometer.get(), Rainfall.collectValues().length);
        addStandProperties(ItemRegistry.hyetometer.get(), Humidity.collectValues().length);
        addStandProperties(ItemRegistry.thermometer.get(), Temperature.collectValues().length);

        withExistingParent(itemName(ItemRegistry.spring_greenhouse_essence_item.get()),resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.summer_greenhouse_essence_item.get()),resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.autumn_greenhouse_essence_item.get()),resourceItem("empty"));
        withExistingParent(itemName(ItemRegistry.winter_greenhouse_essence_item.get()),resourceItem("empty"));

        withExistingParent(resourceItem(ItemRegistry.block_in_wooden_grate_block_item.getId().getPath()).getPath(),
                resourceBlock(ItemRegistry.block_in_wooden_grate_block_item.getId().getPath()));

    }

    public void addSimple(Item item) {
        withExistingParent(itemName(item), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(itemName(item)));
    }

    public void addSimple(ItemLike item, String texture) {
        withExistingParent(itemName(item), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(texture));
    }

    public void addSimple(String item, String texture) {
        withExistingParent(resourceItem(item).getPath(), ResourceLocation.withDefaultNamespace(GENERATED))
                .texture("layer0", resourceItem(texture));
    }

    private String itemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    public ResourceLocation resourceItem(String path) {
        return EclipticSeasons.rl("item/" + path);
    }

    public void addStandProperties(Item item, int length) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item.asItem());
        for (int i = 0; i < length; i++) {
            String name = itemKey.getPath() + "_stage_" + i;
            addSimple(name, name);
        }
        ItemModelBuilder itemModelBuilder = withExistingParent(itemName(item),
                itemKey.withSuffix("_stage_0"));
        for (int i = 0; i < length; i++) {
            itemModelBuilder
                    .override()
                    .predicate(itemKey, i * (1 / ((float) length - 1)))
                    .model(new ModelFile.ExistingModelFile(resourceItem(itemKey.getPath() + "_stage_" + i), existingFileHelper))
                    .end();

        }
    }

    public ResourceLocation resourceBlock(String path) {
        return EclipticSeasons.rl("block/" + path);
    }
}

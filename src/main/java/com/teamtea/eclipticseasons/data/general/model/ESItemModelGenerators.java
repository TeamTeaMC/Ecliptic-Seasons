package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.client.itemproperties.CounterModelProperty;
import com.teamtea.eclipticseasons.client.itemproperties.CounterState;
import com.teamtea.eclipticseasons.client.model.entity.TryModel;
import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreSpecialRenderer;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import lombok.Builder;
import lombok.Data;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
@Builder
public class ESItemModelGenerators {
    private final ItemModelGenerators models;

    public void run() {
        addSimple(ItemRegistry.calendar_item.value());

        addSimple(ItemRegistry.wind_chimes_item.value());
        addSimple(ItemRegistry.paper_wind_chimes_item.value());
        addSimple(ItemRegistry.bamboo_wind_chimes_item.value());

        addSimple(ItemRegistry.pinwheel_blue_item.value(), "pinwheel_blue_item");
        addSimple(ItemRegistry.pinwheel_lime_item.value(), "pinwheel_lime_item");
        addSimple(ItemRegistry.pinwheel_orange_item.value(), "pinwheel_orange_item");

        addSimple(ItemRegistry.broom.value());
        addSimple(ItemRegistry.ice_wand.value());
        addSimple(ItemRegistry.salt_wand.value());

        addSimple(ItemRegistry.seasonal_prayer_scroll_item.value());
        addSimple(ItemRegistry.growth_detector.value());

        addSimple(ItemRegistry.snowless_hometown.value());

        generateStandardCompassItem(ItemRegistry.hygrometer.get(), CounterState.Query.hygrometer, Humidity.collectValues().length);
        generateStandardCompassItem(ItemRegistry.hyetometer.get(), CounterState.Query.hyetometer, Rainfall.collectValues().length);
        generateStandardCompassItem(ItemRegistry.thermometer.get(), CounterState.Query.thermometer, Temperature.collectValues().length);

        addEmpty(ItemRegistry.spring_greenhouse_essence_item.get(), ItemRegistry.spring_greenhouse_core_item.get());
        addEmpty(ItemRegistry.summer_greenhouse_essence_item.get(), ItemRegistry.summer_greenhouse_core_item.get());
        addEmpty(ItemRegistry.autumn_greenhouse_essence_item.get(), ItemRegistry.autumn_greenhouse_core_item.get());
        addEmpty(ItemRegistry.winter_greenhouse_essence_item.get(), ItemRegistry.winter_greenhouse_core_item.get());
        addEmpty(ItemRegistry.spring_greenhouse_core_item.get(), ItemRegistry.spring_greenhouse_core_item.get());
        addEmpty(ItemRegistry.summer_greenhouse_core_item.get(), ItemRegistry.summer_greenhouse_core_item.get());
        addEmpty(ItemRegistry.autumn_greenhouse_core_item.get(), ItemRegistry.autumn_greenhouse_core_item.get());
        addEmpty(ItemRegistry.winter_greenhouse_core_item.get(), ItemRegistry.winter_greenhouse_core_item.get());
        addEmpty(ItemRegistry.greenhouse_core_container_item.get(), ItemRegistry.greenhouse_core_container_item.get());
    }

    public void addSimple(Item item) {
        models.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    public void addSimple(ItemLike item, String texture) {
        models.itemModelOutput.accept(item.asItem(),
                ItemModelUtils.plainModel(createFlatItemModel(item.asItem(), new Material(EclipticSeasons.rl("item/%s".formatted(texture))), ModelTemplates.FLAT_ITEM)));
    }

    public void addEmpty(Item item, BlockItem blockItem) {
        // ModelTemplate empty = ModelTemplates.createItem(EclipticSeasons.rl("empty").toString());
        // models.itemModelOutput.accept(item.asItem(),
        //         ItemModelUtils.plainModel(models.createFlatItemModel(item, empty)));
        models.itemModelOutput.accept(
                item,
                new SpecialModelWrapper.Unbaked(
                        // The parent model to read the particle texture and display transformation from
                        // Points to 'assets/minecraft/models/item/template_skull.json'
                        EclipticSeasons.rl("block/block_in_wooden_grate_block"),
                        Optional.empty(),
                        // The special model renderer to use
                        new GreenHouseCoreSpecialRenderer.Unbaked(
                                // The texture to use
                                // Points to 'assets/examplemod/textures/entity/example/example_texture.png'
                            (    blockItem == ItemRegistry.greenhouse_core_container_item.get() ?
                                        TryModel.greenhouse_core_container.sprite(): GreenHouseCoreSpecialRenderer.getMaterialFromItemS(blockItem))
                                    .withSuffix(".png").withPrefix("textures/"),
                                item != blockItem
                                // Identifier.fromNamespaceAndPath("examplemod", "example/example_texture")
                        )
                )
        );
    }


    public Identifier createFlatItemModel(Item item, Material material, ModelTemplate template) {
        return template.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(material), models.modelOutput);
    }

    public void generateStandardCompassItem(Item compass, CounterState.Query query, int length) {
        List<RangeSelectItemModel.Entry> overrides = this.createCompassModels(compass, length);
        models.itemModelOutput
                .accept(
                        compass,
                        ItemModelUtils.rangeSelect(new CounterModelProperty(query, length), 1.0F, overrides)
                );
    }

    public List<RangeSelectItemModel.Entry> createCompassModels(Item compass, int length) {
        List<RangeSelectItemModel.Entry> overrides = new ArrayList<>();
        ItemModel.Unbaked base = ItemModelUtils.plainModel(models.createFlatItemModel(compass, "_stage_0", ModelTemplates.FLAT_ITEM));
        // overrides.add(ItemModelUtils.override(base, 0.0F));

        for (int i = 1; i < length; i++) {
            ItemModel.Unbaked overrideModel = ItemModelUtils.plainModel(
                    models.createFlatItemModel(compass, "_stage_" + i, ModelTemplates.FLAT_ITEM)
            );
            overrides.add(ItemModelUtils.override(overrideModel, i * (1 / ((float) length - 1))));
        }

        overrides.add(ItemModelUtils.override(base, 0));
        return overrides;
    }
}
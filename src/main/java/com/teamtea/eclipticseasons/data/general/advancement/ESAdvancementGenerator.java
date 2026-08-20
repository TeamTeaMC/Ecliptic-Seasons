package com.teamtea.eclipticseasons.data.general.advancement;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.common.advancement.ParentNeedCriterion;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ESLootTables;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.Tags;


import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ESAdvancementGenerator implements ConditionalAdvancementSubProvider {
    AdvancementHolder seasons;

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
        throw new UnsupportedOperationException();
    }

    public void generate(HolderLookup.Provider registries, ConditionalAdvancementOutput consumer) {
        HolderLookup<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
        HolderLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
        HolderLookup<EntityType<?>> types = registries.lookupOrThrow(Registries.ENTITY_TYPE);
        SeasonalSimulationLevelCondition condition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.AGRICULTURE).build();
        SeasonalSimulationLevelCondition surviveCondition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.SURVIVAL).build();

        seasons = Advancement.Builder.advancement()
                .display(ItemRegistry.calendar_item.get(),
                        Component.translatable("advancement.eclipticseasons.base"),
                        Component.translatable("advancement.eclipticseasons.base.desc"),
                        Identifier.parse("minecraft:gui/advancements/backgrounds/husbandry"),
                        AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer, getNameId("main/base"));

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(ItemRegistry.bamboo_wind_chimes_item.get(),
                        Component.translatable("advancement.eclipticseasons.root"),
                        Component.translatable("advancement.eclipticseasons.root.desc"),
                        null,
                        AdvancementType.TASK, true, true, false)
                .addCriterion("solar_terms", SolarTermsCriterion.TriggerInstance.simple())
                .requirements(AdvancementRequirements.Strategy.AND)
                .rewards(AdvancementRewards.Builder.loot(ESLootTables.snowless_hometown))
                .save(consumer, getNameId("main/root"));

        AdvancementHolder heatStroke = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.MAGMA_BLOCK,
                        Component.translatable("advancement.eclipticseasons.heat_stroke"),
                        Component.translatable("advancement.eclipticseasons.heat_stroke.desc"),
                        null,
                        AdvancementType.TASK, true, false, true)
                .addCriterion("heat_stroke", SolarTermsCriterion.TriggerInstance.simple2())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer.withConditions(surviveCondition), getNameId("main/heat_stroke"));

        BlockRegistry.initCopperGrateMap();
        AdvancementHolder green_house = buildAdvancementHolder(seasons, ItemRegistry.growth_detector.get(),
                Component.translatable("advancement.eclipticseasons.green_house"),
                Component.translatable("advancement.eclipticseasons.green_house.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.growth_detector.get()),
                consumer.withConditions(condition), "main/green_house");

        AdvancementHolder greenhouse_core_container = buildAdvancementHolder(green_house, ItemRegistry.greenhouse_core_container_item.get(),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container"),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.greenhouse_core_container.get())),
                consumer.withConditions(condition), "main/greenhouse_core_container");


        AdvancementHolder greenhouse_core =
                Advancement.Builder.advancement()
                        .parent(greenhouse_core_container)
                        .display(ItemRegistry.spring_greenhouse_essence_item.get(),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core"),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core.desc"),
                                null,
                                AdvancementType.TASK, false, true, false)
                        .addCriterion("core_require_spring", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.spring_greenhouse_core.get())),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_summer", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.summer_greenhouse_core.get())),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_autumn", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.autumn_greenhouse_core.get())),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_winter", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.winter_greenhouse_core.get())),
                                ItemPredicate.Builder.item()
                        ))
                        // .addCriterion("parent_spring", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/spring_end")))
                        // .addCriterion("parent_summer", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/summer_end")))
                        // .addCriterion("parent_autumn", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/autumn_end")))
                        // .addCriterion("parent_winter", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/winter_end")))
                        .requirements(new AdvancementRequirements(List.of(
                                List.of("core_require_spring", "core_require_summer", "core_require_autumn", "core_require_winter")
                                // ,List.of("parent_spring", "parent_summer", "parent_autumn", "parent_winter")
                        )))
                        .save(consumer.withConditions(condition), getNameId("quests/greenhouse_core"));

        AdvancementHolder humidity_tank = buildAdvancementHolder(green_house, BlockRegistry.humidity_tank.get(),
                Component.translatable("advancement.eclipticseasons.humidity_tank"),
                Component.translatable("advancement.eclipticseasons.humidity_tank.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.humidity_tank.get())),
                consumer.withConditions(condition), "main/humidity_tank");

        AdvancementHolder dehumidifier = buildAdvancementHolder(humidity_tank, BlockRegistry.dehumidifier.get(),
                Component.translatable("advancement.eclipticseasons.dehumidifier"),
                Component.translatable("advancement.eclipticseasons.dehumidifier.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.dehumidifier.get())),
                consumer.withConditions(condition), "main/dehumidifier");

        AdvancementHolder seasonal_prayer_scroll =
                Advancement.Builder.advancement().parent(seasons)
                        .display(ItemRegistry.seasonal_prayer_scroll_item.get(), Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll"),
                                Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll.desc"),
                                null,
                                AdvancementType.TASK, false, true, true)
                        .addCriterion("core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.spring_greenhouse_essence_item.get(),
                                ItemRegistry.summer_greenhouse_essence_item.get(), ItemRegistry.autumn_greenhouse_essence_item.get(), ItemRegistry.winter_greenhouse_essence_item.get()))
                        .requirements(AdvancementRequirements.Strategy.AND)
                        .save(consumer.withConditions(condition), getNameId("main/seasonal_prayer_scroll"));

        AdvancementHolder season_ritual = buildAdvancementHolder(seasonal_prayer_scroll, ItemRegistry.spring_greenhouse_core_item.get(),
                Component.translatable("advancement.eclipticseasons.seasonal_ritual"),
                Component.translatable("advancement.eclipticseasons.seasonal_ritual.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.spring_greenhouse_core.get(), BlockRegistry.summer_greenhouse_core.get(), BlockRegistry.autumn_greenhouse_core.get(), BlockRegistry.winter_greenhouse_core.get())),
                        ItemPredicate.Builder.item().of(items, ItemRegistry.seasonal_prayer_scroll_item.get())
                ),
                consumer.withConditions(condition), "main/seasonal_ritual");

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(ItemRegistry.pinwheel_lime_item.get(),
                        Component.translatable("advancement.eclipticseasons.quest"),
                        Component.translatable("advancement.eclipticseasons.quest.desc"),
                        null,
                        AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer.withConditions(condition), getNameId("main/quest"));

        buildSpring(items, types, consumer.withConditions(condition));
        buildSummer(items, consumer.withConditions(condition));
        buildAutumn(items, blocks, consumer.withConditions(condition));
        buildWinter(items, consumer.withConditions(condition));
    }

    private void buildSpring(HolderLookup<Item> items, HolderLookup<EntityType<?>> types, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder spring_start = buildAdvancementHolder(seasons, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_start"),
                Component.translatable("advancement.eclipticseasons.spring_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.WHEAT_SEEDS)
                ),
                consumer, "quests/spring_start");

        AdvancementHolder spring_harvest = buildAdvancementHolder(spring_start, Items.WHEAT,
                Component.translatable("advancement.eclipticseasons.spring_harvest"),
                Component.translatable("advancement.eclipticseasons.spring_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT),
                consumer, "quests/spring_harvest");

        AdvancementHolder spring_feed =
                Advancement.Builder.advancement()
                        .parent(spring_harvest)
                        .display(Items.WHITE_WOOL,
                                Component.translatable("advancement.eclipticseasons.spring_feed"),
                                Component.translatable("advancement.eclipticseasons.spring_feed.desc"),
                                null,
                                AdvancementType.TASK, false, true, false)
                        // .addCriterion("core_require_sheep", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                        //         ItemPredicate.Builder.item().of(items, ItemTags.SHEEP_FOOD),
                        //         Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(types, EntityType.SHEEP)))
                        // ))
                        // .addCriterion("core_require_cow", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                        //         ItemPredicate.Builder.item().of(items, ItemTags.COW_FOOD),
                        //         Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(types, EntityType.COW)))
                        // ))
                        .addCriterion("core_require", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(items, Tags.Items.ANIMAL_FOODS),
                                Optional.empty()
                        ))
                        .addCriterion("parent", ParentNeedCriterion.TriggerInstance.simple(spring_harvest))
                        .requirements(new AdvancementRequirements(List.of(List.of("core_require"), List.of("parent"))))
                        .save(consumer, getNameId("quests/spring_feed"));

        AdvancementHolder spring_seed = buildAdvancementHolder(spring_feed, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_seed"),
                Component.translatable("advancement.eclipticseasons.spring_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT_SEEDS),
                consumer, "quests/spring_seed");

        AdvancementHolder spring_bread = buildAdvancementHolder(spring_seed, Items.BREAD,
                Component.translatable("advancement.eclipticseasons.spring_bread"),
                Component.translatable("advancement.eclipticseasons.spring_bread.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BREAD),
                consumer, "quests/spring_bread");

        AdvancementHolder spring_hay = buildAdvancementHolder(spring_bread, Items.HAY_BLOCK,
                Component.translatable("advancement.eclipticseasons.spring_hay"),
                Component.translatable("advancement.eclipticseasons.spring_hay.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HAY_BLOCK),
                consumer, "quests/spring_hay", ESLootTables.spring_greenhouse_essence);

        // AdvancementHolder spring_end = buildAdvancementHolder(spring_hay, ItemRegistry.spring_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.spring_end"),
        //         Component.translatable("advancement.eclipticseasons.spring_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.spring_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/spring_end", ESLootTables.spring_greenhouse_essence);
    }

    private void buildSummer(HolderLookup<Item> items, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder summer_start = buildAdvancementHolder(seasons, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_start"),
                Component.translatable("advancement.eclipticseasons.summer_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.MELON_SEEDS)
                ),
                consumer, "quests/summer_start");

        AdvancementHolder summer_harvest = buildAdvancementHolder(summer_start, Items.MELON,
                Component.translatable("advancement.eclipticseasons.summer_harvest"),
                Component.translatable("advancement.eclipticseasons.summer_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON),
                consumer, "quests/summer_harvest");

        AdvancementHolder summer_melon_slice = buildAdvancementHolder(summer_harvest, Items.MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SLICE),
                consumer, "quests/summer_melon_slice");

        AdvancementHolder summer_seed = buildAdvancementHolder(summer_melon_slice, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_seed"),
                Component.translatable("advancement.eclipticseasons.summer_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SEEDS),
                consumer, "quests/summer_seed");

        AdvancementHolder summer_glistering_melon_slice = buildAdvancementHolder(summer_seed, Items.GOLD_NUGGET,
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET),
                consumer, "quests/summer_glistering_melon_slice");

        AdvancementHolder summer_eat_glistering_melon_slice = buildAdvancementHolder(summer_glistering_melon_slice, Items.GLISTERING_MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLISTERING_MELON_SLICE),
                consumer, "quests/summer_eat_glistering_melon_slice", ESLootTables.summer_greenhouse_essence);

        // AdvancementHolder summer_end = buildAdvancementHolder(summer_eat_glistering_melon_slice, ItemRegistry.summer_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.summer_end"),
        //         Component.translatable("advancement.eclipticseasons.summer_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.summer_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/summer_end", ESLootTables.summer_greenhouse_essence);
    }


    private void buildAutumn(HolderLookup<Item> items, HolderLookup<Block> blocks, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder autumn_start = buildAdvancementHolder(seasons, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_start"),
                Component.translatable("advancement.eclipticseasons.autumn_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.PUMPKIN_SEEDS)
                ),
                consumer, "quests/autumn_start");

        AdvancementHolder autumn_harvest = buildAdvancementHolder(autumn_start, Items.PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_harvest"),
                Component.translatable("advancement.eclipticseasons.autumn_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN),
                consumer, "quests/autumn_harvest");

        AdvancementHolder autumn_seed = buildAdvancementHolder(autumn_harvest, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_seed"),
                Component.translatable("advancement.eclipticseasons.autumn_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_SEEDS),
                consumer, "quests/autumn_seed");

        AdvancementHolder autumn_carved_pumpkin = buildAdvancementHolder(autumn_seed, Items.CARVED_PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin"),
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.CARVED_PUMPKIN)),
                        ItemPredicate.Builder.item().of(items, Tags.Items.TOOLS_SHEAR)
                ),
                consumer, "quests/autumn_carved_pumpkin");

        AdvancementHolder autumn_jack_o_lantern = buildAdvancementHolder(autumn_carved_pumpkin, Items.JACK_O_LANTERN,
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern"),
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.JACK_O_LANTERN),
                consumer, "quests/autumn_jack_o_lantern");

        AdvancementHolder autumn_pumpkin_pie = buildAdvancementHolder(autumn_jack_o_lantern, Items.PUMPKIN_PIE,
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie"),
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_PIE),
                consumer, "quests/autumn_pumpkin_pie", ESLootTables.autumn_greenhouse_essence);

        // AdvancementHolder autumn_end = buildAdvancementHolder(autumn_pumpkin_pie, ItemRegistry.autumn_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.autumn_end"),
        //         Component.translatable("advancement.eclipticseasons.autumn_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.autumn_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/autumn_end", ESLootTables.autumn_greenhouse_essence);
    }

    private void buildWinter(HolderLookup<Item> items, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder winter_start = buildAdvancementHolder(seasons, ItemRegistry.broom.get(),
                Component.translatable("advancement.eclipticseasons.winter_start"),
                Component.translatable("advancement.eclipticseasons.winter_start.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.broom.get()),
                consumer, "quests/winter_start");

        AdvancementHolder winter_harvest = buildAdvancementHolder(winter_start, Items.POWDER_SNOW_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_harvest"),
                Component.translatable("advancement.eclipticseasons.winter_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POWDER_SNOW_BUCKET),
                consumer, "quests/winter_harvest");

        AdvancementHolder winter_campfire = buildAdvancementHolder(winter_harvest, Items.CAMPFIRE,
                Component.translatable("advancement.eclipticseasons.winter_campfire"),
                Component.translatable("advancement.eclipticseasons.winter_campfire.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAMPFIRE),
                consumer, "quests/winter_campfire");

        AdvancementHolder winter_milk = buildAdvancementHolder(winter_campfire, Items.MILK_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_milk"),
                Component.translatable("advancement.eclipticseasons.winter_milk.desc"),
                "core_require", UsingItemTrigger.TriggerInstance.lookingAt(
                        EntityPredicate.Builder.entity(),
                        ItemPredicate.Builder.item().of(items, Items.MILK_BUCKET)
                ),
                consumer, "quests/winter_milk");

        AdvancementHolder winter_carpet = buildAdvancementHolder(winter_milk, Items.WHITE_CARPET,
                Component.translatable("advancement.eclipticseasons.winter_carpet"),
                Component.translatable("advancement.eclipticseasons.winter_carpet.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, ItemTags.WOOL_CARPETS)),
                consumer, "quests/winter_carpet");

        AdvancementHolder winter_cake = buildAdvancementHolder(winter_carpet, Items.CAKE,
                Component.translatable("advancement.eclipticseasons.winter_cake"),
                Component.translatable("advancement.eclipticseasons.winter_cake.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAKE),
                consumer, "quests/winter_cake", ESLootTables.winter_greenhouse_essence);

        // AdvancementHolder winter_end = buildAdvancementHolder(winter_cake, ItemRegistry.winter_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.winter_end"),
        //         Component.translatable("advancement.eclipticseasons.winter_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.winter_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/winter_end", ESLootTables.winter_greenhouse_essence);
    }


    public AdvancementHolder buildAdvancementHolder(AdvancementHolder parent,
                                                    ItemLike icon,
                                                    Component tittle, Component desc,
                                                    String criterionKey, Criterion<?> criterion,
                                                    Consumer<AdvancementHolder> consumer, String id) {
        return buildAdvancementHolder(parent, icon, tittle, desc, criterionKey, criterion, consumer, id, null);
    }

    public AdvancementHolder buildAdvancementHolder(AdvancementHolder parent,
                                                    ItemLike icon,
                                                    Component tittle, Component desc,
                                                    String criterionKey, Criterion<?> criterion,
                                                    Consumer<AdvancementHolder> consumer, String id,
                                                    ResourceKey<LootTable> lootTable) {
        Advancement.Builder advancement = Advancement.Builder.advancement();
        if (parent != null) {
            advancement = advancement.parent(parent);
            if (parent != seasons)
                advancement = advancement.addCriterion("parent_need", ParentNeedCriterion.TriggerInstance.simple(parent));
        }
        if (lootTable != null) {
            advancement = advancement.rewards(AdvancementRewards.Builder.loot(lootTable));
        }
        return advancement.display(icon,
                        tittle,
                        desc,
                        null,
                        AdvancementType.TASK, false, true, false)
                .addCriterion(criterionKey, criterion)
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer, getNameId(id));
    }


    private String getNameId(String id) {
        return EclipticSeasonsApi.MODID + ":" + id;
    }


}

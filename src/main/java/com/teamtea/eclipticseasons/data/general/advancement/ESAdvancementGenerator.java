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
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.*;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;


import java.util.List;
import java.util.Optional;

public class ESAdvancementGenerator extends AdvancementSubProvider {
    AdvancementHolder seasons;

    private HolderGetter<EntityType<?>> entityTypes;
    private HolderGetter<Item> items;
    private HolderGetter<Block> blocks;
    private HolderGetter<EntityType<?>> types;
    private HolderGetter<LootTable> lootTables;

    protected ICondition[] conditionsCrop;

    public ESAdvancementGenerator(BootstrapContext<Advancement> output) {
        super(output);
    }

    @Override
    public void generate() {
        this.entityTypes = output.lookup(Registries.ENTITY_TYPE);
        this.items = output.lookup(Registries.ITEM);
        this.blocks = output.lookup(Registries.BLOCK);
        this.types = output.lookup(Registries.ENTITY_TYPE);
        this.lootTables = output.lookup(Registries.LOOT_TABLE);
        this.conditionsCrop = new ICondition[]{SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.AGRICULTURE).build()};
        SeasonalSimulationLevelCondition surviveCondition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.SURVIVAL).build();

        seasons = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(ItemRegistry.calendar_item),
                        Component.translatable("advancement.eclipticseasons.base"),
                        Component.translatable("advancement.eclipticseasons.base.desc"),
                        Optional.of(new ClientAsset.ResourceTexture(Identifier.parse("minecraft:gui/advancements/backgrounds/husbandry"))),
                        AdvancementType.TASK, false, false, false))
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(output, getNameId("main/base"));

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(ItemRegistry.bamboo_wind_chimes_item.get(),
                        Component.translatable("advancement.eclipticseasons.root"),
                        Component.translatable("advancement.eclipticseasons.root.desc"),
                        AdvancementType.TASK, true, true, false)
                .addCriterion("solar_terms", SolarTermsCriterion.TriggerInstance.simple())
                .requirements(AdvancementRequirements.Strategy.AND)
                .rewards(AdvancementRewards.Builder.loot(lootTables.getOrThrow(ESLootTables.snowless_hometown)))
                .save(output, getNameId("main/root"));

        AdvancementHolder heatStroke = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.MAGMA_BLOCK,
                        Component.translatable("advancement.eclipticseasons.heat_stroke"),
                        Component.translatable("advancement.eclipticseasons.heat_stroke.desc"),
                        AdvancementType.TASK, true, false, true)
                .addCriterion("heat_stroke", SolarTermsCriterion.TriggerInstance.simple2())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(output, getNameId("main/heat_stroke"), surviveCondition);

        // BlockRegistry.initCopperGrateMap();
        AdvancementHolder green_house = buildAdvancementHolder(seasons, ItemRegistry.growth_detector.get(),
                Component.translatable("advancement.eclipticseasons.green_house"),
                Component.translatable("advancement.eclipticseasons.green_house.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.growth_detector.get()),
                conditionsCrop, "main/green_house");

        AdvancementHolder greenhouse_core_container = buildAdvancementHolder(green_house, ItemRegistry.greenhouse_core_container_item.get(),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container"),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.greenhouse_core_container.get())),
                conditionsCrop, "main/greenhouse_core_container");


        AdvancementHolder greenhouse_core =
                Advancement.Builder.advancement()
                        .parent(greenhouse_core_container)
                        .display(ItemRegistry.spring_greenhouse_essence_item.get(),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core"),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core.desc"),
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
                        .save(output, getNameId("quests/greenhouse_core"), conditionsCrop);

        AdvancementHolder humidity_tank = buildAdvancementHolder(green_house, BlockRegistry.humidity_tank.get(),
                Component.translatable("advancement.eclipticseasons.humidity_tank"),
                Component.translatable("advancement.eclipticseasons.humidity_tank.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.humidity_tank.get())),
                conditionsCrop, "main/humidity_tank");

        AdvancementHolder dehumidifier = buildAdvancementHolder(humidity_tank, BlockRegistry.dehumidifier.get(),
                Component.translatable("advancement.eclipticseasons.dehumidifier"),
                Component.translatable("advancement.eclipticseasons.dehumidifier.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(items, BlockRegistry.dehumidifier.get())),
                conditionsCrop, "main/dehumidifier");

        AdvancementHolder seasonal_prayer_scroll =
                Advancement.Builder.advancement().parent(seasons)
                        .display(ItemRegistry.seasonal_prayer_scroll_item.get(), Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll"),
                                Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll.desc"),
                                AdvancementType.TASK, false, true, true)
                        .addCriterion("core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.spring_greenhouse_essence_item.get(),
                                ItemRegistry.summer_greenhouse_essence_item.get(), ItemRegistry.autumn_greenhouse_essence_item.get(), ItemRegistry.winter_greenhouse_essence_item.get()))
                        .requirements(AdvancementRequirements.Strategy.AND)
                        .save(output, getNameId("main/seasonal_prayer_scroll"), conditionsCrop);

        AdvancementHolder season_ritual = buildAdvancementHolder(seasonal_prayer_scroll, ItemRegistry.spring_greenhouse_core_item.get(),
                Component.translatable("advancement.eclipticseasons.seasonal_ritual"),
                Component.translatable("advancement.eclipticseasons.seasonal_ritual.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BlockRegistry.spring_greenhouse_core.get(), BlockRegistry.summer_greenhouse_core.get(), BlockRegistry.autumn_greenhouse_core.get(), BlockRegistry.winter_greenhouse_core.get())),
                        ItemPredicate.Builder.item().of(items, ItemRegistry.seasonal_prayer_scroll_item.get())
                ),
                conditionsCrop, "main/seasonal_ritual");

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(ItemRegistry.pinwheel_lime_item.get(),
                        Component.translatable("advancement.eclipticseasons.quest"),
                        Component.translatable("advancement.eclipticseasons.quest.desc"),
                        AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(output, getNameId("main/quest"), conditionsCrop);

        buildSpring(items, types, conditionsCrop);
        buildSummer(items, conditionsCrop);
        buildAutumn(items, blocks, conditionsCrop);
        buildWinter(items, conditionsCrop);
    }

    private void buildSpring(HolderGetter<Item> items, HolderGetter<EntityType<?>> types, ICondition[] conditions) {
        AdvancementHolder spring_start = buildAdvancementHolder(seasons, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_start"),
                Component.translatable("advancement.eclipticseasons.spring_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.WHEAT_SEEDS)
                ),
                conditions, "quests/spring_start");

        AdvancementHolder spring_harvest = buildAdvancementHolder(spring_start, Items.WHEAT,
                Component.translatable("advancement.eclipticseasons.spring_harvest"),
                Component.translatable("advancement.eclipticseasons.spring_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT),
                conditions, "quests/spring_harvest");

        AdvancementHolder spring_feed =
                Advancement.Builder.advancement()
                        .parent(spring_harvest)
                        .display(Items.WOOL.white(),
                                Component.translatable("advancement.eclipticseasons.spring_feed"),
                                Component.translatable("advancement.eclipticseasons.spring_feed.desc"),
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
                        .save(output, getNameId("quests/spring_feed"), conditions);

        AdvancementHolder spring_seed = buildAdvancementHolder(spring_feed, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_seed"),
                Component.translatable("advancement.eclipticseasons.spring_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT_SEEDS),
                conditions, "quests/spring_seed");

        AdvancementHolder spring_bread = buildAdvancementHolder(spring_seed, Items.BREAD,
                Component.translatable("advancement.eclipticseasons.spring_bread"),
                Component.translatable("advancement.eclipticseasons.spring_bread.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BREAD),
                conditions, "quests/spring_bread");

        AdvancementHolder spring_hay = buildAdvancementHolder(spring_bread, Items.HAY_BLOCK,
                Component.translatable("advancement.eclipticseasons.spring_hay"),
                Component.translatable("advancement.eclipticseasons.spring_hay.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HAY_BLOCK),
                conditions, "quests/spring_hay", ESLootTables.spring_greenhouse_essence);

        // AdvancementHolder spring_end = buildAdvancementHolder(spring_hay, ItemRegistry.spring_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.spring_end"),
        //         Component.translatable("advancement.eclipticseasons.spring_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.spring_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         conditions, "quests/spring_end", ESLootTables.spring_greenhouse_essence);
    }

    private void buildSummer(HolderGetter<Item> items, ICondition[] conditions) {
        AdvancementHolder summer_start = buildAdvancementHolder(seasons, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_start"),
                Component.translatable("advancement.eclipticseasons.summer_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.MELON_SEEDS)
                ),
                conditions, "quests/summer_start");

        AdvancementHolder summer_harvest = buildAdvancementHolder(summer_start, Items.MELON,
                Component.translatable("advancement.eclipticseasons.summer_harvest"),
                Component.translatable("advancement.eclipticseasons.summer_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON),
                conditions, "quests/summer_harvest");

        AdvancementHolder summer_melon_slice = buildAdvancementHolder(summer_harvest, Items.MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SLICE),
                conditions, "quests/summer_melon_slice");

        AdvancementHolder summer_seed = buildAdvancementHolder(summer_melon_slice, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_seed"),
                Component.translatable("advancement.eclipticseasons.summer_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SEEDS),
                conditions, "quests/summer_seed");

        AdvancementHolder summer_glistering_melon_slice = buildAdvancementHolder(summer_seed, Items.GOLD_NUGGET,
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET),
                conditions, "quests/summer_glistering_melon_slice");

        AdvancementHolder summer_eat_glistering_melon_slice = buildAdvancementHolder(summer_glistering_melon_slice, Items.GLISTERING_MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLISTERING_MELON_SLICE),
                conditions, "quests/summer_eat_glistering_melon_slice", ESLootTables.summer_greenhouse_essence);

        // AdvancementHolder summer_end = buildAdvancementHolder(summer_eat_glistering_melon_slice, ItemRegistry.summer_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.summer_end"),
        //         Component.translatable("advancement.eclipticseasons.summer_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.summer_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         conditions, "quests/summer_end", ESLootTables.summer_greenhouse_essence);
    }


    private void buildAutumn(HolderGetter<Item> items, HolderGetter<Block> blocks, ICondition[] conditions) {
        AdvancementHolder autumn_start = buildAdvancementHolder(seasons, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_start"),
                Component.translatable("advancement.eclipticseasons.autumn_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(items, Items.PUMPKIN_SEEDS)
                ),
                conditions, "quests/autumn_start");

        AdvancementHolder autumn_harvest = buildAdvancementHolder(autumn_start, Items.PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_harvest"),
                Component.translatable("advancement.eclipticseasons.autumn_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN),
                conditions, "quests/autumn_harvest");

        AdvancementHolder autumn_seed = buildAdvancementHolder(autumn_harvest, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_seed"),
                Component.translatable("advancement.eclipticseasons.autumn_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_SEEDS),
                conditions, "quests/autumn_seed");

        AdvancementHolder autumn_carved_pumpkin = buildAdvancementHolder(autumn_seed, Items.CARVED_PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin"),
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.CARVED_PUMPKIN)),
                        ItemPredicate.Builder.item().of(items, Tags.Items.TOOLS_SHEAR)
                ),
                conditions, "quests/autumn_carved_pumpkin");

        AdvancementHolder autumn_jack_o_lantern = buildAdvancementHolder(autumn_carved_pumpkin, Items.JACK_O_LANTERN,
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern"),
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.JACK_O_LANTERN),
                conditions, "quests/autumn_jack_o_lantern");

        AdvancementHolder autumn_pumpkin_pie = buildAdvancementHolder(autumn_jack_o_lantern, Items.PUMPKIN_PIE,
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie"),
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_PIE),
                conditions, "quests/autumn_pumpkin_pie", ESLootTables.autumn_greenhouse_essence);

        // AdvancementHolder autumn_end = buildAdvancementHolder(autumn_pumpkin_pie, ItemRegistry.autumn_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.autumn_end"),
        //         Component.translatable("advancement.eclipticseasons.autumn_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.autumn_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         conditions, "quests/autumn_end", ESLootTables.autumn_greenhouse_essence);
    }

    private void buildWinter(HolderGetter<Item> items, ICondition[] conditions) {
        AdvancementHolder winter_start = buildAdvancementHolder(seasons, ItemRegistry.broom.get(),
                Component.translatable("advancement.eclipticseasons.winter_start"),
                Component.translatable("advancement.eclipticseasons.winter_start.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.broom.get()),
                conditions, "quests/winter_start");

        AdvancementHolder winter_harvest = buildAdvancementHolder(winter_start, Items.POWDER_SNOW_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_harvest"),
                Component.translatable("advancement.eclipticseasons.winter_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POWDER_SNOW_BUCKET),
                conditions, "quests/winter_harvest");

        AdvancementHolder winter_campfire = buildAdvancementHolder(winter_harvest, Items.CAMPFIRE,
                Component.translatable("advancement.eclipticseasons.winter_campfire"),
                Component.translatable("advancement.eclipticseasons.winter_campfire.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAMPFIRE),
                conditions, "quests/winter_campfire");

        AdvancementHolder winter_milk = buildAdvancementHolder(winter_campfire, Items.MILK_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_milk"),
                Component.translatable("advancement.eclipticseasons.winter_milk.desc"),
                "core_require", UsingItemTrigger.TriggerInstance.lookingAt(
                        EntityPredicate.Builder.entity(),
                        ItemPredicate.Builder.item().of(items, Items.MILK_BUCKET)
                ),
                conditions, "quests/winter_milk");

        AdvancementHolder winter_carpet = buildAdvancementHolder(winter_milk, Items.CARPET.white(),
                Component.translatable("advancement.eclipticseasons.winter_carpet"),
                Component.translatable("advancement.eclipticseasons.winter_carpet.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, ItemTags.WOOL_CARPETS)),
                conditions, "quests/winter_carpet");

        AdvancementHolder winter_cake = buildAdvancementHolder(winter_carpet, Items.CAKE,
                Component.translatable("advancement.eclipticseasons.winter_cake"),
                Component.translatable("advancement.eclipticseasons.winter_cake.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAKE),
                conditions, "quests/winter_cake", ESLootTables.winter_greenhouse_essence);

        // AdvancementHolder winter_end = buildAdvancementHolder(winter_cake, ItemRegistry.winter_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.winter_end"),
        //         Component.translatable("advancement.eclipticseasons.winter_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks,BlockRegistry.winter_greenhouse_core.get())),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         conditions, "quests/winter_end", ESLootTables.winter_greenhouse_essence);
    }


    public AdvancementHolder buildAdvancementHolder(AdvancementHolder parent,
                                                    ItemLike icon,
                                                    Component tittle, Component desc,
                                                    String criterionKey, Criterion<?> criterion,
                                                    ICondition[] conditions, String id) {
        return buildAdvancementHolder(parent, icon, tittle, desc, criterionKey, criterion, conditions, id, null);
    }

    public AdvancementHolder buildAdvancementHolder(AdvancementHolder parent,
                                                    ItemLike icon,
                                                    Component tittle, Component desc,
                                                    String criterionKey, Criterion<?> criterion,
                                                    ICondition[] conditions, String id,
                                                    ResourceKey<LootTable> lootTable) {
        Advancement.Builder advancement = Advancement.Builder.advancement();
        if (parent != null) {
            advancement = advancement.parent(parent);
            if (parent != seasons)
                advancement = advancement.addCriterion("parent_need", ParentNeedCriterion.TriggerInstance.simple(parent));
        }
        if (lootTable != null) {
            advancement = advancement.rewards(AdvancementRewards.Builder.loot(lootTables.getOrThrow(lootTable)));
        }
        return advancement.display(icon.asItem(),
                        tittle,
                        desc,
                        AdvancementType.TASK, false, true, false)
                .addCriterion(criterionKey, criterion)
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(output, getNameId(id), conditions);
    }


    private String getNameId(String id) {
        return EclipticSeasonsApi.MODID + ":" + id;
    }


}

package com.teamtea.eclipticseasons.data.advancement;

import com.teamtea.eclipticseasons.common.advancement.ParentNeedCriterion;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ESLootTables;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ESAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    private String getNameId(String id) {
        return EclipticSeasonsApi.MODID + ":" + id;
    }

    Advancement seasons = null;

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        seasons = Advancement.Builder.advancement()
                .display(ItemRegistry.calendar_item.get(),
                        Component.translatable("advancement.eclipticseasons.base"),
                        Component.translatable("advancement.eclipticseasons.base.desc"),
                        new ResourceLocation("minecraft:textures/block/bricks.png"),
                        FrameType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/base"));

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.PAPER,
                        Component.translatable("advancement.eclipticseasons.root"),
                        Component.translatable("advancement.eclipticseasons.root.desc"),
                        new ResourceLocation("minecraft:textures/block/bricks.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("solar_terms", SolarTermsCriterion.TriggerInstance.simple())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/root"));

        Advancement heatStroke = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.MAGMA_BLOCK,
                        Component.translatable("advancement.eclipticseasons.heat_stroke"),
                        Component.translatable("advancement.eclipticseasons.heat_stroke.desc"),
                        null,
                        FrameType.TASK, true, false, false)
                .addCriterion("heat_stroke", SolarTermsCriterion.TriggerInstance.simple2())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/heat_stroke"));

        Advancement green_house = buildAdvancement(seasons, ItemRegistry.growth_detector.get(),
                Component.translatable("advancement.eclipticseasons.green_house"),
                Component.translatable("advancement.eclipticseasons.green_house.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.growth_detector.get()),
                consumer, "main/green_house");

        Advancement greenhouse_core_container = buildAdvancement(green_house, ItemRegistry.greenhouse_core_container_item.get(),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container"),
                Component.translatable("advancement.eclipticseasons.greenhouse_core_container.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.greenhouse_core_container.get()),
                consumer, "main/greenhouse_core_container");


        Advancement greenhouse_core =
                Advancement.Builder.advancement()
                        .parent(greenhouse_core_container)
                        .display(ItemRegistry.spring_greenhouse_core_item.get(),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core"),
                                Component.translatable("advancement.eclipticseasons.greenhouse_core.desc"),
                                null,
                                FrameType.TASK, false, true, false)
                        .addCriterion("core_require_spring", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.spring_greenhouse_core.get()).build()),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_summer", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.summer_greenhouse_core.get()).build()),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_autumn", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.autumn_greenhouse_core.get()).build()),
                                ItemPredicate.Builder.item()
                        ))
                        .addCriterion("core_require_winter", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.winter_greenhouse_core.get()).build()),
                                ItemPredicate.Builder.item()
                        ))
                        // .addCriterion("parent_spring", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/spring_end")))
                        // .addCriterion("parent_summer", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/summer_end")))
                        // .addCriterion("parent_autumn", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/autumn_end")))
                        // .addCriterion("parent_winter", ParentNeedCriterion.TriggerInstance.simple(EclipticSeasons.rl("quests/winter_end")))
                        .requirements(new String[][]{{"core_require_spring", "core_require_summer", "core_require_autumn", "core_require_winter"}})
                        .save(consumer, getNameId("quests/greenhouse_core"));

        Advancement copper_grate = buildAdvancement(green_house, BlockRegistry.block_in_wooden_grate_block.get(),
                Component.translatable("advancement.eclipticseasons.copper_grate"),
                Component.translatable("advancement.eclipticseasons.copper_grate.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.block_in_wooden_grate_block.get()),
                consumer, "main/copper_grate");

        Advancement block_in_copper_grate = buildAdvancement(copper_grate, Items.SPONGE,
                Component.translatable("advancement.eclipticseasons.block_in_copper_grate"),
                Component.translatable("advancement.eclipticseasons.block_in_copper_grate.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.block_in_wooden_grate_block.get()).build()),
                        ItemPredicate.Builder.item()
                ),
                consumer, "main/block_in_copper_grate");

        Advancement seasonal_prayer_scroll =
                Advancement.Builder.advancement().parent(seasons)
                        .display(ItemRegistry.seasonal_prayer_scroll_item.get(), Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll"),
                                Component.translatable("advancement.eclipticseasons.seasonal_prayer_scroll.desc"),
                                null,
                                FrameType.TASK, false, true, true)
                        .addCriterion("core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.spring_greenhouse_essence_item.get(),
                                ItemRegistry.summer_greenhouse_essence_item.get(), ItemRegistry.autumn_greenhouse_essence_item.get(), ItemRegistry.winter_greenhouse_essence_item.get()))
                        .requirements(RequirementsStrategy.AND)
                        .save(consumer, getNameId("main/seasonal_prayer_scroll"));

        Advancement decorate_oak_hanging_sign = buildAdvancement(seasonal_prayer_scroll, Items.OAK_HANGING_SIGN,
                Component.translatable("advancement.eclipticseasons.decorate_oak_hanging_sign"),
                Component.translatable("advancement.eclipticseasons.decorate_oak_hanging_sign.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.season_quest_wall_hanging_sign.get(), BlockRegistry.season_quest_ceiling_hanging_sign.get()).build()),
                        ItemPredicate.Builder.item()
                ),
                consumer, "main/decorate_oak_hanging_sign");

        seasons = Advancement.Builder.advancement()
                .parent(seasons)
                .display(ItemRegistry.spring_greenhouse_essence_item.get(),
                        Component.translatable("advancement.eclipticseasons.quest"),
                        Component.translatable("advancement.eclipticseasons.quest.desc"),
                        null,
                        FrameType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/quest"));

        buildSpring(consumer);
        buildSummer(consumer);
        buildAutumn(consumer);
        buildWinter(consumer);
    }

    private void buildSpring(Consumer<Advancement> consumer) {
        Advancement spring_start = buildAdvancement(seasons, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_start"),
                Component.translatable("advancement.eclipticseasons.spring_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(Items.WHEAT_SEEDS)
                ),
                consumer, "quests/spring_start");

        Advancement spring_harvest = buildAdvancement(spring_start, Items.WHEAT,
                Component.translatable("advancement.eclipticseasons.spring_harvest"),
                Component.translatable("advancement.eclipticseasons.spring_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT),
                consumer, "quests/spring_harvest");

        Advancement spring_feed =
                Advancement.Builder.advancement()
                        .parent(spring_harvest)
                        .display(Items.WHITE_WOOL,
                                Component.translatable("advancement.eclipticseasons.spring_feed"),
                                Component.translatable("advancement.eclipticseasons.spring_feed.desc"),
                                null,
                                FrameType.TASK, false, true, false)
                        .addCriterion("core_require_sheep", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(Items.WHEAT),
                                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.SHEEP).build())
                        ))
                        .addCriterion("core_require_cow", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(Items.WHEAT),
                                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.COW).build())
                        ))
                        .addCriterion("core_require_chicken", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(Tags.Items.SEEDS),
                                EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.CHICKEN).build())
                        ))
                        .addCriterion("parent", ParentNeedCriterion.TriggerInstance.simple(spring_harvest))
                        // .requirements(RequirementsStrategy.OR)
                        .requirements(new String[][]{{"core_require_sheep", "core_require_cow", "core_require_chicken"}, {"parent"}})
                        .save(consumer, getNameId("quests/spring_feed"));

        Advancement spring_seed = buildAdvancement(spring_feed, Items.WHEAT_SEEDS,
                Component.translatable("advancement.eclipticseasons.spring_seed"),
                Component.translatable("advancement.eclipticseasons.spring_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT_SEEDS),
                consumer, "quests/spring_seed");

        Advancement spring_bread = buildAdvancement(spring_seed, Items.BREAD,
                Component.translatable("advancement.eclipticseasons.spring_bread"),
                Component.translatable("advancement.eclipticseasons.spring_bread.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BREAD),
                consumer, "quests/spring_bread");

        Advancement spring_hay = buildAdvancement(spring_bread, Items.HAY_BLOCK,
                Component.translatable("advancement.eclipticseasons.spring_hay"),
                Component.translatable("advancement.eclipticseasons.spring_hay.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HAY_BLOCK),
                consumer, "quests/spring_hay", ESLootTables.spring_greenhouse_essence);

        // Advancement spring_end = buildAdvancement(spring_hay, ItemRegistry.spring_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.spring_end"),
        //         Component.translatable("advancement.eclipticseasons.spring_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.spring_greenhouse_core.get()).build()),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/spring_end", ESLootTables.spring_greenhouse_essence);
    }

    private void buildSummer(Consumer<Advancement> consumer) {
        Advancement summer_start = buildAdvancement(seasons, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_start"),
                Component.translatable("advancement.eclipticseasons.summer_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(Items.MELON_SEEDS)
                ),
                consumer, "quests/summer_start");

        Advancement summer_harvest = buildAdvancement(summer_start, Items.MELON,
                Component.translatable("advancement.eclipticseasons.summer_harvest"),
                Component.translatable("advancement.eclipticseasons.summer_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON),
                consumer, "quests/summer_harvest");

        Advancement summer_melon_slice = buildAdvancement(summer_harvest, Items.MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SLICE),
                consumer, "quests/summer_melon_slice");

        Advancement summer_seed = buildAdvancement(summer_melon_slice, Items.MELON_SEEDS,
                Component.translatable("advancement.eclipticseasons.summer_seed"),
                Component.translatable("advancement.eclipticseasons.summer_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MELON_SEEDS),
                consumer, "quests/summer_seed");

        Advancement summer_glistering_melon_slice = buildAdvancement(summer_seed, Items.GOLD_NUGGET,
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET),
                consumer, "quests/summer_glistering_melon_slice");

        Advancement summer_eat_glistering_melon_slice = buildAdvancement(summer_glistering_melon_slice, Items.GLISTERING_MELON_SLICE,
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice"),
                Component.translatable("advancement.eclipticseasons.summer_eat_glistering_melon_slice.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLISTERING_MELON_SLICE),
                consumer, "quests/summer_eat_glistering_melon_slice", ESLootTables.summer_greenhouse_essence);

        // Advancement summer_end = buildAdvancement(summer_eat_glistering_melon_slice, ItemRegistry.summer_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.summer_end"),
        //         Component.translatable("advancement.eclipticseasons.summer_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.summer_greenhouse_core.get()).build()),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/summer_end", ESLootTables.summer_greenhouse_essence);
    }


    private void buildAutumn(Consumer<Advancement> consumer) {
        Advancement autumn_start = buildAdvancement(seasons, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_start"),
                Component.translatable("advancement.eclipticseasons.autumn_start.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location(),
                        ItemPredicate.Builder.item().of(Items.PUMPKIN_SEEDS)
                ),
                consumer, "quests/autumn_start");

        Advancement autumn_harvest = buildAdvancement(autumn_start, Items.PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_harvest"),
                Component.translatable("advancement.eclipticseasons.autumn_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN),
                consumer, "quests/autumn_harvest");

        Advancement autumn_seed = buildAdvancement(autumn_harvest, Items.PUMPKIN_SEEDS,
                Component.translatable("advancement.eclipticseasons.autumn_seed"),
                Component.translatable("advancement.eclipticseasons.autumn_seed.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_SEEDS),
                consumer, "quests/autumn_seed");

        Advancement autumn_carved_pumpkin = buildAdvancement(autumn_seed, Items.CARVED_PUMPKIN,
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin"),
                Component.translatable("advancement.eclipticseasons.autumn_carved_pumpkin.desc"),
                "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.CARVED_PUMPKIN).build()),
                        ItemPredicate.Builder.item().of(Tags.Items.SHEARS)
                ),
                consumer, "quests/autumn_carved_pumpkin");

        Advancement autumn_jack_o_lantern = buildAdvancement(autumn_carved_pumpkin, Items.JACK_O_LANTERN,
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern"),
                Component.translatable("advancement.eclipticseasons.autumn_jack_o_lantern.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.JACK_O_LANTERN),
                consumer, "quests/autumn_jack_o_lantern");

        Advancement autumn_pumpkin_pie = buildAdvancement(autumn_jack_o_lantern, Items.PUMPKIN_PIE,
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie"),
                Component.translatable("advancement.eclipticseasons.autumn_pumpkin_pie.desc"),
                "core_require",  InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN_PIE),
                consumer, "quests/autumn_pumpkin_pie", ESLootTables.autumn_greenhouse_essence);

        // Advancement autumn_end = buildAdvancement(autumn_pumpkin_pie, ItemRegistry.autumn_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.autumn_end"),
        //         Component.translatable("advancement.eclipticseasons.autumn_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.autumn_greenhouse_core.get()).build()),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/autumn_end", ESLootTables.autumn_greenhouse_essence);
    }

    private void buildWinter(Consumer<Advancement> consumer) {
        Advancement winter_start = buildAdvancement(seasons, ItemRegistry.broom.get(),
                Component.translatable("advancement.eclipticseasons.winter_start"),
                Component.translatable("advancement.eclipticseasons.winter_start.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.broom.get()),
                consumer, "quests/winter_start");

        Advancement winter_harvest = buildAdvancement(winter_start, Items.POWDER_SNOW_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_harvest"),
                Component.translatable("advancement.eclipticseasons.winter_harvest.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POWDER_SNOW_BUCKET),
                consumer, "quests/winter_harvest");

        Advancement winter_campfire = buildAdvancement(winter_harvest, Items.CAMPFIRE,
                Component.translatable("advancement.eclipticseasons.winter_campfire"),
                Component.translatable("advancement.eclipticseasons.winter_campfire.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAMPFIRE),
                consumer, "quests/winter_campfire");

        Advancement winter_milk = buildAdvancement(winter_campfire, Items.MILK_BUCKET,
                Component.translatable("advancement.eclipticseasons.winter_milk"),
                Component.translatable("advancement.eclipticseasons.winter_milk.desc"),
                "core_require", UsingItemTrigger.TriggerInstance.lookingAt(
                        EntityPredicate.Builder.entity(),
                        ItemPredicate.Builder.item().of(Items.MILK_BUCKET)
                ),
                consumer, "quests/winter_milk");

        Advancement winter_carpet = buildAdvancement(winter_milk, Items.WHITE_CARPET,
                Component.translatable("advancement.eclipticseasons.winter_carpet"),
                Component.translatable("advancement.eclipticseasons.winter_carpet.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.WOOL_CARPETS).build()),
                consumer, "quests/winter_carpet");

        Advancement winter_cake = buildAdvancement(winter_carpet, Items.CAKE,
                Component.translatable("advancement.eclipticseasons.winter_cake"),
                Component.translatable("advancement.eclipticseasons.winter_cake.desc"),
                "core_require", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CAKE),
                consumer, "quests/winter_cake", ESLootTables.winter_greenhouse_essence);

        // Advancement winter_end = buildAdvancement(winter_cake, ItemRegistry.winter_greenhouse_core_item.get(),
        //         Component.translatable("advancement.eclipticseasons.winter_end"),
        //         Component.translatable("advancement.eclipticseasons.winter_end.desc"),
        //         "core_require", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
        //                 LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockRegistry.winter_greenhouse_core.get()).build()),
        //                 ItemPredicate.Builder.item()
        //         ),
        //         consumer, "quests/winter_end", ESLootTables.winter_greenhouse_essence);
    }

    public Advancement buildAdvancement(Advancement parent,
                                        ItemLike icon,
                                        Component tittle, Component desc,
                                        String criterionKey, CriterionTriggerInstance criterion,
                                        Consumer<Advancement> consumer, String id) {
        return buildAdvancement(parent, icon, tittle, desc, criterionKey, criterion, consumer, id, null);
    }

    public Advancement buildAdvancement(Advancement parent,
                                        ItemLike icon,
                                        Component tittle, Component desc,
                                        String criterionKey, CriterionTriggerInstance criterion,
                                        Consumer<Advancement> consumer, String id,
                                        ResourceLocation lootTable) {
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
                        FrameType.TASK, false, true, false)
                .addCriterion(criterionKey, criterion)
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId(id));
    }
}

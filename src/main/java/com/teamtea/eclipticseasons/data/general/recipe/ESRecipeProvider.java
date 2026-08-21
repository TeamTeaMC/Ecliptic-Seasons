package com.teamtea.eclipticseasons.data.general.recipe;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.function.Consumer;

public final class ESRecipeProvider extends RecipeProvider {

    public ESRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        SeasonalSimulationLevelCondition agricultureCondition =
                SeasonalSimulationLevelCondition.builder()
                        .level(SeasonalSimulationLevel.AGRICULTURE)
                        .build();

        SeasonalSimulationLevelCondition survivalCondition =
                SeasonalSimulationLevelCondition.builder()
                        .level(SeasonalSimulationLevel.SURVIVAL)
                        .build();

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        ItemRegistry.calendar_item.get()
                )
                .define('x', Items.PAPER)
                .define('y', Items.BOOK)
                .define('z', Tags.Items.FEATHERS)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.pinwheel_blue.get()
                )
                .define('x', Items.PAPER)
                .define('y', Items.BLUE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.pinwheel_lime.get()
                )
                .define('x', Items.PAPER)
                .define('y', Items.LIME_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.pinwheel_orange.get()
                )
                .define('x', Items.PAPER)
                .define('y', Items.ORANGE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.wind_chimes.get()
                )
                .define('x', Items.STRING)
                .define('z', Items.BAMBOO)
                .pattern(" x ")
                .pattern("zzz")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Items.STRING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.bamboo_wind_chimes.get()
                )
                .define('x', Items.STRING)
                .define('z', Items.BAMBOO_BLOCK)
                .define('y', Items.PAPER)
                .pattern(" x ")
                .pattern(" z ")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Items.STRING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.DECORATIONS,
                        BlockRegistry.paper_wind_chimes.get()
                )
                .define('x', Items.STRING)
                .define('y', Items.PAPER)
                .define('i', Items.BLUE_DYE)
                .define('j', Items.YELLOW_DYE)
                .pattern("xyi")
                .pattern(" yj")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ConditionalRecipe.builder()
                .addCondition(survivalCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.hyetometer.get()
                                )
                                .define('x', Tags.Items.DUSTS_REDSTONE)
                                .define('y', Items.GLASS_BOTTLE)
                                .define('z', Tags.Items.INGOTS_COPPER)
                                .pattern("xz")
                                .pattern(" y")
                                .group("hyetometer")
                                .unlockedBy(
                                        "has_glass_bottle",
                                        has(Items.GLASS_BOTTLE)
                                )
                                .unlockedBy(
                                        "self",
                                        has(ItemRegistry.hyetometer.get())
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "hyetometer"
                ))
                .build(consumer, EclipticSeasons.rl("hyetometer"));

        ConditionalRecipe.builder()
                .addCondition(survivalCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.thermometer.get()
                                )
                                .define('x', Tags.Items.DUSTS_REDSTONE)
                                .define(
                                        'y',
                                        PartialNBTIngredient.of(
                                                PotionUtils.setPotion(
                                                        Items.POTION.getDefaultInstance(),
                                                        Potions.WATER
                                                ).getOrCreateTag(),
                                                Items.POTION
                                        )
                                )
                                .pattern(" x")
                                .pattern("y ")
                                .group("thermometer")
                                .unlockedBy(
                                        "has_glass_bottle",
                                        has(Items.GLASS_BOTTLE)
                                )
                                .unlockedBy(
                                        "self",
                                        has(ItemRegistry.thermometer.get())
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "thermometer"
                ))
                .build(consumer, EclipticSeasons.rl("thermometer"));

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.TOOLS,
                        ItemRegistry.hygrometer.get()
                )
                .define('x', Tags.Items.GEMS_AMETHYST)
                .define('y', Tags.Items.INGOTS_COPPER)
                .define('z', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.GLASS_PANES)
                .define('c', Items.CALCITE)
                .pattern("yxy")
                .pattern("gcg")
                .pattern("czc")
                .group("hygrometer")
                .unlockedBy(
                        "has_amethyst",
                        has(Tags.Items.GEMS_AMETHYST)
                )
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.TOOLS,
                        ItemRegistry.broom.get()
                )
                .define('h', Items.HAY_BLOCK)
                .define('r', Tags.Items.RODS_WOODEN)
                .pattern(" h")
                .pattern("r ")
                .group("broom")
                .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(consumer);

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.growth_detector.get()
                                )
                                .define('x', Tags.Items.GLASS_PANES)
                                .define('y', Tags.Items.GLASS)
                                .define('z', Tags.Items.RODS_WOODEN)
                                .pattern("  x")
                                .pattern(" y ")
                                .pattern("z  ")
                                .group("growth_detector")
                                .unlockedBy("has_glass", has(Tags.Items.GLASS))
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "growth_detector"
                ))
                .build(consumer, EclipticSeasons.rl("growth_detector"));

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.seasonal_prayer_scroll_item.get()
                                )
                                .define('x', Tags.Items.SEEDS)
                                .define('y', Items.PAPER)
                                .pattern("xx")
                                .pattern("xy")
                                .group("seasonal_prayer_scroll")
                                .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "seasonal_prayer_scroll"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("seasonal_prayer_scroll")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.greenhouse_core_container_item.get()
                                )
                                .define('x', Tags.Items.GLASS_TINTED)
                                .define('z', Tags.Items.INGOTS_COPPER)
                                .pattern("zxz")
                                .pattern("x x")
                                .pattern("zxz")
                                .group("greenhouse_core_container")
                                .unlockedBy(
                                        "has_amethyst",
                                        has(Tags.Items.GEMS_AMETHYST)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "greenhouse_core_container"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("greenhouse_core_container")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.block_in_wooden_grate_block_item.get(),
                                        4
                                )
                                .define('r', ItemTags.LOGS)
                                .pattern(" r ")
                                .pattern("r r")
                                .pattern(" r ")
                                .group("block_in_wooden_grate_block")
                                .unlockedBy("has_logs", has(ItemTags.LOGS))
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "block_in_wooden_grate_block"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("block_in_wooden_grate_block")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapelessRecipeBuilder.shapeless(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.spring_greenhouse_core_item.get()
                                )
                                .requires(ItemRegistry.spring_greenhouse_essence_item.get())
                                .requires(ItemRegistry.greenhouse_core_container_item.get())
                                .group("spring_greenhouse_core")
                                .unlockedBy(
                                        "has_amethyst",
                                        has(Tags.Items.GEMS_AMETHYST)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "spring_greenhouse_core"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("spring_greenhouse_core")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapelessRecipeBuilder.shapeless(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.summer_greenhouse_core_item.get()
                                )
                                .requires(ItemRegistry.summer_greenhouse_essence_item.get())
                                .requires(ItemRegistry.greenhouse_core_container_item.get())
                                .group("summer_greenhouse_core")
                                .unlockedBy(
                                        "has_amethyst",
                                        has(Tags.Items.GEMS_AMETHYST)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "summer_greenhouse_core"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("summer_greenhouse_core")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapelessRecipeBuilder.shapeless(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.autumn_greenhouse_core_item.get()
                                )
                                .requires(ItemRegistry.autumn_greenhouse_essence_item.get())
                                .requires(ItemRegistry.greenhouse_core_container_item.get())
                                .group("autumn_greenhouse_core")
                                .unlockedBy(
                                        "has_amethyst",
                                        has(Tags.Items.GEMS_AMETHYST)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "autumn_greenhouse_core"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("autumn_greenhouse_core")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapelessRecipeBuilder.shapeless(
                                        RecipeCategory.TOOLS,
                                        ItemRegistry.winter_greenhouse_core_item.get()
                                )
                                .requires(ItemRegistry.winter_greenhouse_essence_item.get())
                                .requires(ItemRegistry.greenhouse_core_container_item.get())
                                .group("winter_greenhouse_core")
                                .unlockedBy(
                                        "has_amethyst",
                                        has(Tags.Items.GEMS_AMETHYST)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.TOOLS,
                        "winter_greenhouse_core"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("winter_greenhouse_core")
                );

        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("patchouli"))
                .addRecipe(output -> {
                    ItemStack book = BuiltInRegistries.ITEM.get(
                            new ResourceLocation("patchouli", "guide_book")
                    ).getDefaultInstance();

                    CompoundTag tag = book.getOrCreateTag();
                    tag.putString(
                            "patchouli:book",
                            "eclipticseasons:seasons_chronicle"
                    );

                    NBTShapelessRecipeBuilder.shapeless(
                                    RecipeCategory.MISC,
                                    book
                            )
                            .requires(Items.BOOK)
                            .requires(Tags.Items.SEEDS)
                            .group("seasons_chronicle")
                            .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                            .save(
                                    output,
                                    EclipticSeasons.rl("seasons_chronicle")
                            );
                })
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.MISC,
                        "seasons_chronicle"
                ))
                .build(
                        consumer,
                        EclipticSeasons.rl("seasons_chronicle")
                );

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.DECORATIONS,
                                        BlockRegistry.humidity_tank.get()
                                )
                                .pattern("SBS")
                                .pattern("BCB")
                                .pattern("SIS")
                                .define('S', ItemTags.WOODEN_SLABS)
                                .define('B', ItemTags.PLANKS)
                                .define('C', Items.WATER_BUCKET)
                                .define('I', Items.IRON_INGOT)
                                .unlockedBy(
                                        "has_water_bucket",
                                        has(Items.WATER_BUCKET)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.DECORATIONS,
                        "humidity_tank"
                ))
                .build(consumer, EclipticSeasons.rl("humidity_tank"));

        ConditionalRecipe.builder()
                .addCondition(agricultureCondition)
                .addRecipe(output ->
                        ShapedRecipeBuilder.shaped(
                                        RecipeCategory.BUILDING_BLOCKS,
                                        BlockRegistry.dehumidifier.get()
                                )
                                .pattern("PPP")
                                .pattern("PHN")
                                .pattern("SSS")
                                .define('P', ItemTags.PLANKS)
                                .define('H', Blocks.HAY_BLOCK)
                                .define('N', ItemTags.WOODEN_SLABS)
                                .define('S', Items.IRON_NUGGET)
                                .unlockedBy(
                                        "has_hay_block",
                                        has(Blocks.HAY_BLOCK)
                                )
                                .save(output)
                )
                .generateAdvancement(recipeAdvancement(
                        RecipeCategory.BUILDING_BLOCKS,
                        "dehumidifier"
                ))
                .build(consumer, EclipticSeasons.rl("dehumidifier"));

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.REDSTONE,
                        BlockRegistry.season_sensor.get()
                )
                .pattern("GCG")
                .pattern("SRS")
                .pattern("WWW")
                .define('G', Tags.Items.GLASS)
                .define('C', Items.COPPER_INGOT)
                .define('S', Items.REDSTONE)
                .define('R', Items.CLOCK)
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.TOOLS,
                        ItemRegistry.salt_wand.get()
                )
                .pattern(" Q ")
                .pattern(" S ")
                .pattern(" T ")
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('S', Tags.Items.INGOTS_GOLD)
                .define('T', Items.STICK)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(consumer);
    }

    private static ResourceLocation recipeAdvancement(
            RecipeCategory category,
            String recipeName
    ) {
        return EclipticSeasons.rl(
                "recipes/" + category.getFolderName() + "/" + recipeName
        );
    }
}
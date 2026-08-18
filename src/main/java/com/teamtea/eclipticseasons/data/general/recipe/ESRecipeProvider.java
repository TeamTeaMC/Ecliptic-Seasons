package com.teamtea.eclipticseasons.data.general.recipe;


import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

public class ESRecipeProvider extends VanillaRecipeProvider {

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ESRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "ES Recipes";
        }
    }

    public ESRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
        SeasonalSimulationLevelCondition condition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.AGRICULTURE).build();
        SeasonalSimulationLevelCondition surviveCondition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.SURVIVAL).build();

        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, ItemRegistry.calendar_item.value())
                .define('x', Items.PAPER)
                .define('y', Items.BOOK)
                .define('z', Tags.Items.FEATHERS)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_blue.value())
                .define('x', Items.PAPER)
                .define('y', Items.BLUE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_lime.value())
                .define('x', Items.PAPER)
                .define('y', Items.LIME_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_orange.value())
                .define('x', Items.PAPER)
                .define('y', Items.ORANGE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('z', Items.BAMBOO)
                .pattern(" x ")
                .pattern("zzz")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .save(output);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.bamboo_wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('z', Items.BAMBOO_BLOCK)
                .define('y', Items.PAPER)
                .pattern(" x ")
                .pattern(" z ")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .save(output);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BlockRegistry.paper_wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('y', Items.PAPER)
                .define('i', Items.BLUE_DYE)
                .define('j', Items.YELLOW_DYE)
                .pattern("xyi")
                .pattern(" yj")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_paper", has(Tags.Items.STRINGS))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.broom.get())
                .define('h', Items.HAY_BLOCK)
                .define('r', Tags.Items.RODS_WOODEN)
                .pattern(" h")
                .pattern("r ")
                .group("broom")
                .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.hyetometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', Items.GLASS_BOTTLE)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("xz")
                .pattern(" y")
                .group("hyetometer")
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("self", has(ItemRegistry.hyetometer.get()))
                .save(output.withConditions(surviveCondition));


        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.thermometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', DataComponentIngredient.of(false, () -> DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION))
                .pattern(" x")
                .pattern("y ")
                .group("thermometer")
                .unlockedBy("has_glass", has(Items.GLASS_BOTTLE))
                .unlockedBy("self", has(ItemRegistry.thermometer.get()))
                .save(output.withConditions(surviveCondition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.hygrometer.get())
                .define('x', Tags.Items.GEMS_AMETHYST)
                .define('y', Tags.Items.INGOTS_COPPER)
                .define('z', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.GLASS_PANES)
                .define('c', Items.CALCITE)
                .pattern("yxy")
                .pattern("gcg")
                .pattern("czc")
                .group("hygrometer")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.growth_detector.get())
                .define('x', Tags.Items.GLASS_PANES)
                .define('y', Tags.Items.GLASS_BLOCKS)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("  x")
                .pattern(" y ")
                .pattern("z  ")
                .group("growth_detector")
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(output.withConditions(condition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.seasonal_prayer_scroll_item.get())
                .define('x', Tags.Items.SEEDS)
                .define('y', Items.PAPER)
                .pattern("xx")
                .pattern("xy")
                .group("seasonal_prayer_scroll")
                .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                .save(output.withConditions(condition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.greenhouse_core_container_item.get())
                .define('x', Tags.Items.GLASS_BLOCKS_TINTED)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("zxz")
                .pattern("x x")
                .pattern("zxz")
                .group("greenhouse_core_frame")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output.withConditions(condition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.block_in_wooden_grate_block_item.get(), 4)
                .define('r', ItemTags.LOGS)
                .pattern(" r ")
                .pattern("r r")
                .pattern(" r ")
                .group("block_in_wooden_grate_block")
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(output.withConditions(condition));

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.TOOLS, ItemRegistry.spring_greenhouse_core_item.get())
                .requires(ItemRegistry.spring_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("spring_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output.withConditions(condition));

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.TOOLS, ItemRegistry.summer_greenhouse_core_item.get())
                .requires(ItemRegistry.summer_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("summer_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output.withConditions(condition));

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.TOOLS, ItemRegistry.autumn_greenhouse_core_item.get())
                .requires(ItemRegistry.autumn_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("autumn_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output.withConditions(condition));

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.TOOLS, ItemRegistry.winter_greenhouse_core_item.get())
                .requires(ItemRegistry.winter_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("winter_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(output.withConditions(condition));


        if (ModList.get().isLoaded("patchouli")) {
            ItemStack defaultInstance = BuiltInRegistries.ITEM.get(Identifier.parse("patchouli:guide_book")).get().value().getDefaultInstance();
            defaultInstance.set((DataComponentType) BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse("patchouli:book")).get(), (Object) Identifier.parse("eclipticseasons:seasons_chronicle"));
            RecipeOutput conditionalRecipeOutput = output.withConditions(new ModLoadedCondition("patchouli"));
            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ItemStackTemplate.fromNonEmptyStack(defaultInstance))
                    .requires(Items.BOOK)
                    .requires(Tags.Items.SEEDS)
                    .group("seasons_chronicle")
                    .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                    .save(conditionalRecipeOutput, "seasons_chronicle");
        }

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.humidity_tank.get())
                .pattern("SBS")
                .pattern("BCB")
                .pattern("SIS")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('B', ItemTags.PLANKS)
                .define('C', Items.WATER_BUCKET)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .save(output.withConditions(condition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.dehumidifier.get())
                .pattern("PPP")
                .pattern("PHN")
                .pattern("SSS")
                .define('P', ItemTags.PLANKS)
                .define('H', Blocks.HAY_BLOCK)
                .define('N', ItemTags.WOODEN_SLABS)
                .define('S', Items.IRON_NUGGET)
                .unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK))
                .save(output.withConditions(condition));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, BlockRegistry.season_sensor.get())
                .pattern("GCG")
                .pattern("SRS")
                .pattern("WWW")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('C', Items.COPPER_INGOT)
                .define('S', Items.REDSTONE)
                .define('R', Items.CLOCK)
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, ItemRegistry.salt_wand.get())
                .pattern(" Q ")
                .pattern(" S ")
                .pattern(" T ")
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('S', Tags.Items.INGOTS_GOLD)
                .define('T', Items.STICK)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(output);
    }

}

package com.teamtea.eclipticseasons.data.general.recipe;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

public final class ESRecipeProvider extends RecipeProvider {

    public ESRecipeProvider(PackOutput generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput, HolderLookup.Provider holderLookup) {
        super.buildRecipes(pRecipeOutput, holderLookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ItemRegistry.calendar_item.value())
                .define('x', Items.PAPER)
                .define('y', Items.BOOK)
                .define('z', Tags.Items.FEATHERS)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_blue.value())
                .define('x', Items.PAPER)
                .define('y', Items.BLUE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_lime.value())
                .define('x', Items.PAPER)
                .define('y', Items.LIME_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_orange.value())
                .define('x', Items.PAPER)
                .define('y', Items.ORANGE_DYE)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('z', Items.BAMBOO)
                .pattern(" x ")
                .pattern("zzz")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.bamboo_wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('z', Items.BAMBOO_BLOCK)
                .define('y', Items.PAPER)
                .pattern(" x ")
                .pattern(" z ")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.paper_wind_chimes.value())
                .define('x', Tags.Items.STRINGS)
                .define('y', Items.PAPER)
                .define('i', Items.BLUE_DYE)
                .define('j', Items.YELLOW_DYE)
                .pattern("xyi")
                .pattern(" yj")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_paper", has(Tags.Items.STRINGS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.broom.get())
                .define('h', Items.HAY_BLOCK)
                .define('r', Tags.Items.RODS_WOODEN)
                .pattern(" h")
                .pattern("r ")
                .group("broom")
                .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hyetometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', Items.GLASS_BOTTLE)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("xz")
                .pattern(" y")
                .group("hyetometer")
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("self", has(ItemRegistry.hyetometer.get()))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.thermometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', DataComponentIngredient.of(false, () -> DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION))
                .pattern(" x")
                .pattern("y ")
                .group("thermometer")
                .unlockedBy("has_glass", has(Items.GLASS_BOTTLE))
                .unlockedBy("self", has(ItemRegistry.thermometer.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hygrometer.get())
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
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.growth_detector.get())
                .define('x', Tags.Items.GLASS_PANES)
                .define('y', Tags.Items.GLASS_BLOCKS)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("  x")
                .pattern(" y ")
                .pattern("z  ")
                .group("growth_detector")
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.seasonal_prayer_scroll_item.get())
                .define('x', Tags.Items.SEEDS)
                .define('y', Items.PAPER)
                .pattern("xx")
                .pattern("xy")
                .group("seasonal_prayer_scroll")
                .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.greenhouse_core_container_item.get())
                .define('x', Tags.Items.GLASS_BLOCKS_TINTED)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("zxz")
                .pattern("x x")
                .pattern("zxz")
                .group("greenhouse_core_frame")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.block_in_wooden_grate_block_item.get(), 4)
                .define('r', ItemTags.LOGS)
                .pattern(" r ")
                .pattern("r r")
                .pattern(" r ")
                .group("block_in_wooden_grate_block")
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ItemRegistry.spring_greenhouse_core_item.get())
                .requires(ItemRegistry.spring_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("spring_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ItemRegistry.summer_greenhouse_core_item.get())
                .requires(ItemRegistry.summer_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("summer_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ItemRegistry.autumn_greenhouse_core_item.get())
                .requires(ItemRegistry.autumn_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("autumn_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ItemRegistry.winter_greenhouse_core_item.get())
                .requires(ItemRegistry.winter_greenhouse_essence_item.get())
                .requires(ItemRegistry.greenhouse_core_container_item.get())
                .group("winter_greenhouse_core")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);


        if (ModList.get().isLoaded("patchouli")) {
            ItemStack defaultInstance = BuiltInRegistries.ITEM.get(ResourceLocation.parse("patchouli:guide_book")).getDefaultInstance();
            defaultInstance.set((DataComponentType) BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse("patchouli:book")), (Object) ResourceLocation.parse("eclipticseasons:seasons_chronicle"));
            RecipeOutput conditionalRecipeOutput = consumer.withConditions(new ModLoadedCondition("patchouli"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, defaultInstance)
                    .requires(Items.BOOK)
                    .requires(Tags.Items.SEEDS)
                    .group("seasons_chronicle")
                    .unlockedBy("has_seeds", has(Tags.Items.SEEDS))
                    .save(conditionalRecipeOutput, EclipticSeasons.rl("seasons_chronicle"));
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.humidity_tank.get())
                .pattern("SBS")
                .pattern("BCB")
                .pattern("SIS")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('B', ItemTags.PLANKS)
                .define('C', Items.WATER_BUCKET)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .save(consumer);
    }

}

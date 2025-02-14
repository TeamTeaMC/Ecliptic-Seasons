package com.teamtea.eclipticseasons.data.recipe;


import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.data.internal.NeoForgeRecipeProvider;

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
                .define('z', Items.FEATHER)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_blue.value())
                .define('x', Items.PAPER)
                .define('y', Items.BLUE_DYE)
                .define('z', Items.STICK)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_lime.value())
                .define('x', Items.PAPER)
                .define('y', Items.LIME_DYE)
                .define('z', Items.STICK)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.pinwheel_orange.value())
                .define('x', Items.PAPER)
                .define('y', Items.ORANGE_DYE)
                .define('z', Items.STICK)
                .pattern("xy")
                .pattern("z ")
                .group("pinwheel")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.wind_chimes.value())
                .define('x', Items.STRING)
                .define('z', Items.BAMBOO)
                .pattern(" x ")
                .pattern("zzz")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Items.STRING))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.bamboo_wind_chimes.value())
                .define('x', Items.STRING)
                .define('z', Items.BAMBOO_BLOCK)
                .define('y', Items.PAPER)
                .pattern(" x ")
                .pattern(" z ")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_string", has(Items.STRING))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.paper_wind_chimes.value())
                .define('x', Items.STRING)
                .define('y', Items.PAPER)
                .define('i', Items.BLUE_DYE)
                .define('j', Items.YELLOW_DYE)
                .pattern("xyi")
                .pattern(" yj")
                .pattern(" y ")
                .group("wind_chimes")
                .unlockedBy("has_paper", has(Items.STRING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.broom.get())
                .define('h', Items.HAY_BLOCK)
                .define('r', Items.STICK)
                .pattern(" h")
                .pattern("r ")
                .group("broom")
                .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hyetometer.get())
                .define('x', Items.REDSTONE)
                .define('y', Items.GLASS_BOTTLE)
                .define('z', Items.COPPER_INGOT)
                .pattern("xz")
                .pattern(" y")
                .group("hyetometer")
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.thermometer.get())
                .define('x', Items.REDSTONE)
                .define('y', DataComponentIngredient.of(false,()-> DataComponents.POTION_CONTENTS,new PotionContents(Potions.WATER),Items.POTION))
                .pattern(" x")
                .pattern("y ")
                .group("thermometer")
                .unlockedBy("has_glass", has(Items.GLASS_BOTTLE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hygrometer.get())
                .define('x', Items.AMETHYST_SHARD)
                .define('y', ItemRegistry.thermometer.get())
                .define('z', ItemRegistry.hyetometer.get())
                .pattern(" x ")
                .pattern("xyx")
                .pattern(" z ")
                .group("hygrometer")
                .unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD))
                .save(consumer);
    }

}

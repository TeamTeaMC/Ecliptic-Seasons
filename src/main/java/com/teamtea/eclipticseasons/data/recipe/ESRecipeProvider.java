package com.teamtea.eclipticseasons.data.recipe;


import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public final class ESRecipeProvider extends RecipeProvider {

    public ESRecipeProvider(PackOutput generator) {
        super(generator);
    }


    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ItemRegistry.calendar_item.get())
                .define('x', Items.PAPER)
                .define('y', Items.BOOK)
                .define('z', Items.FEATHER)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.pinwheel_blue.value())
        //         .define('x', Items.PAPER)
        //         .define('y', Items.BLUE_DYE)
        //         .define('z', Items.STICK)
        //         .pattern("xy")
        //         .pattern("z ")
        //         .group("pinwheel")
        //         .unlockedBy("has_paper", has(Items.PAPER))
        //         .save(consumer);
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.pinwheel_lime.value())
        //         .define('x', Items.PAPER)
        //         .define('y', Items.LIME_DYE)
        //         .define('z', Items.STICK)
        //         .pattern("xy")
        //         .pattern("z ")
        //         .group("pinwheel")
        //         .unlockedBy("has_paper", has(Items.PAPER))
        //         .save(consumer);
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.pinwheel_orange.value())
        //         .define('x', Items.PAPER)
        //         .define('y', Items.ORANGE_DYE)
        //         .define('z', Items.STICK)
        //         .pattern("xy")
        //         .pattern("z ")
        //         .group("pinwheel")
        //         .unlockedBy("has_paper", has(Items.PAPER))
        //         .save(consumer);
        //
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.wind_chimes.value())
        //         .define('x', Items.STRING)
        //         .define('z', Items.BAMBOO)
        //         .pattern(" x ")
        //         .pattern("zzz")
        //         .group("wind_chimes")
        //         .unlockedBy("has_string", has(Items.STRING))
        //         .save(consumer);
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.bamboo_wind_chimes.value())
        //         .define('x', Items.STRING)
        //         .define('z', Items.BAMBOO_BLOCK)
        //         .define('y', Items.PAPER)
        //         .pattern(" x ")
        //         .pattern(" z ")
        //         .pattern(" y ")
        //         .group("wind_chimes")
        //         .unlockedBy("has_string", has(Items.STRING))
        //         .save(consumer);
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.paper_wind_chimes.value())
        //         .define('x', Items.STRING)
        //         .define('y', Items.PAPER)
        //         .define('i', Items.BLUE_DYE)
        //         .define('j', Items.YELLOW_DYE)
        //         .pattern("xyi")
        //         .pattern(" yj")
        //         .pattern(" y ")
        //         .group("wind_chimes")
        //         .unlockedBy("has_paper", has(Items.STRING))
        //         .save(consumer);
    }


}

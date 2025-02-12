package com.teamtea.eclipticseasons.data.recipe;


import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;

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
                .define('y', PartialNBTIngredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), Potions.WATER).getOrCreateTag(), Items.POTION))
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

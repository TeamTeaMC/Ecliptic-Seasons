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
import net.minecraftforge.common.Tags;
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
                .define('z', Tags.Items.FEATHERS)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hyetometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', Items.GLASS_BOTTLE)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("xz")
                .pattern(" y")
                .group("hyetometer")
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.thermometer.get())
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('y', PartialNBTIngredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), Potions.WATER).getOrCreateTag(), Items.POTION))
                .pattern(" x")
                .pattern("y ")
                .group("thermometer")
                .unlockedBy("has_glass", has(Items.GLASS_BOTTLE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.hygrometer.get())
                .define('x', Tags.Items.GEMS_AMETHYST)
                .define('y', ItemRegistry.thermometer.get())
                .define('z', ItemRegistry.hyetometer.get())
                .pattern(" x ")
                .pattern("xyx")
                .pattern(" z ")
                .group("hygrometer")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.broom.get())
                .define('h', Items.HAY_BLOCK)
                .define('r', Tags.Items.RODS_WOODEN)
                .pattern(" h")
                .pattern("r ")
                .group("broom")
                .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.growth_detector.get())
                .define('x', Tags.Items.GLASS_PANES)
                .define('y', Tags.Items.GLASS)
                .define('z', Tags.Items.RODS_WOODEN)
                .pattern("  x")
                .pattern(" y ")
                .pattern("z  ")
                .group("growth_detector")
                .unlockedBy("has_glass", has(Tags.Items.GLASS))
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
                .define('x', Tags.Items.GLASS_TINTED)
                .define('z', Tags.Items.INGOTS_COPPER)
                .pattern("zxz")
                .pattern("x x")
                .pattern("zxz")
                .group("greenhouse_core_frame")
                .unlockedBy("has_amethyst", has(Tags.Items.GEMS_AMETHYST))
                .save(consumer);
    }


}

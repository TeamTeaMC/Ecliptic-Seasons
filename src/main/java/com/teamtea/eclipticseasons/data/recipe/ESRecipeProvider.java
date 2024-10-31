package com.teamtea.eclipticseasons.data.recipe;


import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EclipticSeasons.ModContents.calendar_item.value())
                .define('x', Items.PAPER)
                .define('y', Items.BOOK)
                .define('z', Items.REDSTONE)
                .pattern("xx")
                .pattern("yz")
                .group("calendar")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
    }

}

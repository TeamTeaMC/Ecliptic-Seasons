package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record CauldronRecipe(
        BlockState start,
        boolean needRain,
        BlockState end,
        TagKey<Item> tool,
        ItemStack endItem
) {
    public static Supplier<List<CauldronRecipe>> caldronRecipeList = (() -> {
        ArrayList<CauldronRecipe> objects = new ArrayList<>();
        objects.add(new CauldronRecipe(
                Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL),
                false,
                BlockRegistry.ice_cauldron.get().defaultBlockState(),
                ItemTags.PICKAXES,
                Items.ICE.getDefaultInstance()
        ));
        objects.add(new CauldronRecipe(
                Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL),
                false,
                BlockRegistry.snow_cauldron.get().defaultBlockState(),
                ItemTags.SHOVELS,
                Items.SNOW_BLOCK.getDefaultInstance()
        ));
        return objects;
    });
}

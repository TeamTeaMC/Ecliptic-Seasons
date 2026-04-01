package com.teamtea.eclipticseasons.compat.jei.fake;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record GreenHouseCoreRecipe(
        ItemStack input,
        Season season,
        Block end
) {
    public static Supplier<List<GreenHouseCoreRecipe>> Recipes = () -> {
        ArrayList<GreenHouseCoreRecipe> objects = new ArrayList<>();

        objects.add(new GreenHouseCoreRecipe(
                ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
                Season.SPRING,
                BlockRegistry.spring_greenhouse_core.get()
        ));

        objects.add(new GreenHouseCoreRecipe(
                ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
                Season.SUMMER,
                BlockRegistry.summer_greenhouse_core.get()
        ));

        objects.add(new GreenHouseCoreRecipe(
                ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
                Season.AUTUMN,
                BlockRegistry.autumn_greenhouse_core.get()
        ));

        objects.add(new GreenHouseCoreRecipe(
                ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance(),
                Season.WINTER,
                BlockRegistry.winter_greenhouse_core.get()
        ));

        return objects;
    };
}

package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class HumidityControlRegistry {
    public static final ResourceKey<HumidityControl> sponge = createKey("sponge");
    public static final ResourceKey<HumidityControl> wet_sponge = createKey("wet_sponge");

    private static ResourceKey<HumidityControl> createKey(String name) {
        return ResourceKey.create(ESRegistries.HUMIDITY_CONTROL, EclipticSeasons.rl(name));
    }


    public static void bootstrap(BootstrapContext<HumidityControl> context) {
        var blockHolderGetter = context.lookup(Registries.BLOCK);

        context.register(sponge, new HumidityControl(
                new SizedIngredient(Ingredient.of(Items.SPONGE), 1), new ItemStackTemplate(Items.WET_SPONGE), 5, -1, 20 * 300, List.of()
                , true));

        context.register(wet_sponge, new HumidityControl(
                new SizedIngredient(Ingredient.of(Items.WET_SPONGE), 1), new ItemStackTemplate(Items.SPONGE), 5, 1, 20 * 300, List.of(
                new PosAndBlockStateCheck(new Vec3i(0, -1, 0), BlockPredicate.Builder.block().of(blockHolderGetter, EclipticBlockTags.SOFT_HEAT_SOURCES).build())
        ), true
        ));
    }
}

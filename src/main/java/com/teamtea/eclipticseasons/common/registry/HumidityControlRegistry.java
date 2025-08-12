package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WrapSizeIngredient;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

import java.util.List;

public class HumidityControlRegistry {
    public static final ResourceKey<HumidityControl> sponge = createKey("sponge");
    public static final ResourceKey<HumidityControl> wet_sponge = createKey("wet_sponge");

    private static ResourceKey<HumidityControl> createKey(String name) {
        return ResourceKey.create(ESRegistries.HUMIDITY_CONTROL, EclipticSeasons.rl(name));
    }


    public static void bootstrap(BootstapContext<HumidityControl> context) {
        context.register(sponge, new HumidityControl(
                new WrapSizeIngredient(HolderSet.direct(Items.SPONGE.builtInRegistryHolder()), 1), Items.WET_SPONGE.getDefaultInstance(), 5, -1, 20 * 300, List.of()
                , true));

        context.register(wet_sponge, new HumidityControl(
                new WrapSizeIngredient(HolderSet.direct(Items.WET_SPONGE.builtInRegistryHolder()), 1), Items.SPONGE.getDefaultInstance(), 5, 1, 20 * 300, List.of(
                new PosAndBlockStateCheck(new Vec3i(0, -1, 0), new FakeBlockPredicate(context.lookup(Registries.BLOCK).getOrThrow(EclipticBlockTags.SOFT_HEAT_SOURCES)))
        ), true
        ));
    }
}

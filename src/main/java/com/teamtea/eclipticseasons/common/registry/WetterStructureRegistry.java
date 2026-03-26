package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class WetterStructureRegistry {
    public static final ResourceKey<WetterStructure> BUBBLE_COLUMN_ON_MAGMA = createKey("bubble_column_on_magma");

    private static ResourceKey<WetterStructure> createKey(String name) {
        return ResourceKey.create(ESRegistries.WETTER, EclipticSeasons.rl(name));
    }

    private static ResourceKey<Block> createBlockKey(Identifier Identifier) {
        return ResourceKey.create(Registries.BLOCK, Identifier);
    }

    public static void bootstrap(BootstrapContext<WetterStructure> context) {
        var blockHolderGetter = context.lookup(Registries.BLOCK);
        context.register(BUBBLE_COLUMN_ON_MAGMA, new WetterStructure(0.75f, 4, 600, true, Optional.of(new BlockPredicate(Optional.of(HolderSet.direct(Blocks.BUBBLE_COLUMN.builtInRegistryHolder())), Optional.empty(), Optional.empty(), DataComponentMatchers.ANY)), List.of(
                new PosAndBlockStateCheck(Vec3i.ZERO.below(),
                        BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.MAGMA_BLOCK).build())
        )));
    }
}

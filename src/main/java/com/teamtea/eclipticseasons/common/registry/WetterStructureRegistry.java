package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.BlockStatePropertyCondition;
import com.teamtea.eclipticseasons.api.data.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.data.WetterStructure;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class WetterStructureRegistry {
    public static final ResourceKey<WetterStructure> BUBBLE_COLUMN_ON_MAGMA = createKey("bubble_column_on_magma");

    private static ResourceKey<WetterStructure> createKey(String name) {
        return ResourceKey.create(ESRegistries.WETTER, EclipticSeasons.rl(name));
    }

    private static ResourceKey<Block> createBlockKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registries.BLOCK, resourceLocation);
    }

    public static void bootstrap(BootstrapContext<WetterStructure> context) {
        var blockHolderGetter = context.lookup(Registries.BLOCK);
        context.register(BUBBLE_COLUMN_ON_MAGMA, new WetterStructure(1, 4,true,Optional.of(new BlockStatePropertyCondition(Optional.empty(), Optional.of(Blocks.BUBBLE_COLUMN.builtInRegistryHolder()), Optional.empty())),  List.of(
                new PosAndBlockStateCheck(Vec3i.ZERO.below(), new BlockStatePropertyCondition(Optional.empty(), Optional.of(blockHolderGetter.getOrThrow(BuiltInRegistries.BLOCK.getResourceKey(Blocks.MAGMA_BLOCK).get())), Optional.empty()))
        )));
    }
}

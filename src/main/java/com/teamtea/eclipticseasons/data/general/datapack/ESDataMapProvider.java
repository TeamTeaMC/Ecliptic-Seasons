package com.teamtea.eclipticseasons.data.general.datapack;


import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ESDataMapProvider extends DataMapProvider {

    public ESDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        Builder<Waxable, Block> waxableBlockBuilder = builder(NeoForgeDataMaps.WAXABLES);
        waxableBlockBuilder.add(BlockRegistry.block_in_copper_grate_block, new Waxable(BlockRegistry.block_in_waxed_copper_grate_block.get()), false);
        waxableBlockBuilder.add(BlockRegistry.block_in_exposed_copper_grate_block, new Waxable(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get()), false);
        waxableBlockBuilder.add(BlockRegistry.block_in_weathered_copper_grate_block, new Waxable(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get()), false);
        waxableBlockBuilder.add(BlockRegistry.block_in_oxidized_copper_grate_block, new Waxable(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get()), false);

        Builder<Oxidizable, Block> oxidizableBlockBuilder = builder(NeoForgeDataMaps.OXIDIZABLES);
        oxidizableBlockBuilder.add(BlockRegistry.block_in_copper_grate_block,new Oxidizable(BlockRegistry.block_in_exposed_copper_grate_block.get()),false);
        oxidizableBlockBuilder.add(BlockRegistry.block_in_exposed_copper_grate_block,new Oxidizable(BlockRegistry.block_in_weathered_copper_grate_block.get()),false);
        oxidizableBlockBuilder.add(BlockRegistry.block_in_weathered_copper_grate_block,new Oxidizable(BlockRegistry.block_in_oxidized_copper_grate_block.get()),false);
    }
}

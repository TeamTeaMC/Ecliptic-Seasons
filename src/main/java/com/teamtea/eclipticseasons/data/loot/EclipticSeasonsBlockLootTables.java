package com.teamtea.eclipticseasons.data.loot;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;


import java.util.Set;
import java.util.stream.Collectors;

public class EclipticSeasonsBlockLootTables extends BlockLootSubProvider {

    public EclipticSeasonsBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return map.entrySet()
                .stream()
                .map(e -> BuiltInRegistries.BLOCK.stream()
                        .filter(block -> block.getLootTable().equals(e.getKey()))
                        .findFirst()
                        .get())
                .toList();
    }


    protected void dropSelfWithContents(Set<Block> blocks) {
        for (Block block : blocks) {
            // if (skipBlocks.contains(block)) {
            //     continue;
            // }
            add(block, createSingleItemTable(block));
        }
    }

    @Override
    protected void generate() {
        Set<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(block -> EclipticSeasonsApi.MODID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                .filter(block -> ! block.getLootTable().equals( BuiltInLootTables.EMPTY))
                .filter(block -> block.asItem()!= Items.AIR)
                .collect(Collectors.toSet())
                ;

        dropSelfWithContents(blocks);

    }




}

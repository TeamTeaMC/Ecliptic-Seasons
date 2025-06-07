package com.teamtea.eclipticseasons.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class EclipticSeasonsLootTableProvider extends LootTableProvider {

    private final PackOutput generator;

    public EclipticSeasonsLootTableProvider(PackOutput generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator, Set.of(), List.of(new SubProviderEntry(
                EclipticSeasonsBlockLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.BLOCK
        ),new SubProviderEntry(
                EclipticSeasonsGiftLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.GIFT
        )),lookupProvider);
        this.generator = generator;

    }

}

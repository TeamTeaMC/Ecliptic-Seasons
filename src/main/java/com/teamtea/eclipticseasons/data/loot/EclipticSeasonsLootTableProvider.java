package com.teamtea.eclipticseasons.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;


public class EclipticSeasonsLootTableProvider extends LootTableProvider {

    private final PackOutput generator;

    public EclipticSeasonsLootTableProvider(PackOutput generator) {
        super(generator,Set.of(), List.of(new LootTableProvider.SubProviderEntry(
                EclipticSeasonsBlockLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.BLOCK
        ),new LootTableProvider.SubProviderEntry(
                EclipticSeasonsGiftLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.GIFT
        )));
        this.generator = generator;

    }

}

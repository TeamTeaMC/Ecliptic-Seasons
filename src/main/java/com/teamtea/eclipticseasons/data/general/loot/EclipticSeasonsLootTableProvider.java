package com.teamtea.eclipticseasons.data.general.loot;

import com.teamtea.eclipticseasons.data.general.advancement.Advancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class EclipticSeasonsLootTableProvider extends LootTableProvider {


    // public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
    //         .add(Registries.LOOT_TABLE, new EclipticSeasonsLootTableProvider());

    public EclipticSeasonsLootTableProvider() {
        super(BuiltInLootTables.all(), List.of(new LootTableProvider.SubProviderEntry(
                EclipticSeasonsBlockLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.BLOCK
        ),new LootTableProvider.SubProviderEntry(
                EclipticSeasonsGiftLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.GIFT
        )));

    }

}

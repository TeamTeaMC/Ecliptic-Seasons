package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.data.general.loot.EclipticSeasonsLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class ExampleLootTableProvider extends LootTableProvider {

    // public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
    //         .add(Registries.LOOT_TABLE, new ExampleLootTableProvider());


    public ExampleLootTableProvider() {
        super(BuiltInLootTables.all(), List.of(new SubProviderEntry(
                ExampleGiftLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.GIFT
        )));

    }
}

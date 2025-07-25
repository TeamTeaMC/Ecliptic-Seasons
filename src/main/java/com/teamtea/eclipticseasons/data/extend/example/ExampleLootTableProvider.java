package com.teamtea.eclipticseasons.data.extend.example;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class ExampleLootTableProvider extends LootTableProvider {

    private final PackOutput generator;

    public ExampleLootTableProvider(PackOutput generator) {
        super(generator,Set.of(), List.of(new SubProviderEntry(
                ExampleGiftLootTables::new,
                // Loot table generator for the 'empty' param set
                LootContextParamSets.GIFT
        )));
        this.generator = generator;

    }

    @Override
    public @NotNull String getName() {
        return "Example " + super.getName();
    }
}

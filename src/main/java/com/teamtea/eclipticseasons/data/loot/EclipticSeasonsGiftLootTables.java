package com.teamtea.eclipticseasons.data.loot;

import com.teamtea.eclipticseasons.common.registry.ESLootTables;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public record EclipticSeasonsGiftLootTables (
        HolderLookup.Provider registries) implements LootTableSubProvider{

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                ESLootTables.spring_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.spring_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

        output.accept(
                ESLootTables.summer_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.summer_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

        output.accept(
                ESLootTables.autumn_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.autumn_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

        output.accept(
                ESLootTables.winter_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.winter_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

    }

}

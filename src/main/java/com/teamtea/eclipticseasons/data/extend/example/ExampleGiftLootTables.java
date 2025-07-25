package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import com.teamtea.eclipticseasons.common.registry.ESLootTables;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class ExampleGiftLootTables implements LootTableSubProvider{


    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        output.accept(
                ESLootTables.spring_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .when(SeasonCondition.builder(SeasonCondition.Slice.builder().solarTerm(SolarTerm.BEGINNING_OF_SPRING).build()))
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.spring_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

        output.accept(
                ESLootTables.autumn_greenhouse_essence,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .when(SeasonCondition.builder(SeasonCondition.Slice.builder().start(SolarTerm.AUTUMNAL_EQUINOX).end(SolarTerm.BEGINNING_OF_WINTER).build()))
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(ItemRegistry.spring_greenhouse_essence_item.get()).setWeight(10))
                        )
        );

    }


}

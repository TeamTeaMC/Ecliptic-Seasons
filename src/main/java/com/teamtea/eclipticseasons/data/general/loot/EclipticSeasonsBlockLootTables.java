package com.teamtea.eclipticseasons.data.general.loot;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;


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
                .filter(block -> !block.getLootTable().equals(BuiltInLootTables.EMPTY))
                .filter(block -> block.asItem() != Items.AIR)
                .collect(Collectors.toSet());

        dropSelfWithContents(blocks);

        dropWhenSilkTouch(BlockRegistry.greenhouse_core_container.get());

        createCoreDrop(BlockRegistry.spring_greenhouse_core.get(), ItemRegistry.spring_greenhouse_essence_item.get());
        createCoreDrop(BlockRegistry.summer_greenhouse_core.get(), ItemRegistry.summer_greenhouse_essence_item.get());
        createCoreDrop(BlockRegistry.autumn_greenhouse_core.get(), ItemRegistry.autumn_greenhouse_essence_item.get());
        createCoreDrop(BlockRegistry.winter_greenhouse_core.get(), ItemRegistry.winter_greenhouse_essence_item.get());

        dropOther(BlockRegistry.block_in_copper_grate_block.get(), Blocks.COPPER_GRATE);
        dropOther(BlockRegistry.block_in_exposed_copper_grate_block.get(), Blocks.EXPOSED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_weathered_copper_grate_block.get(), Blocks.WEATHERED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_oxidized_copper_grate_block.get(), Blocks.OXIDIZED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_waxed_copper_grate_block.get(), Blocks.WAXED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get(), Blocks.WAXED_EXPOSED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get(), Blocks.WAXED_WEATHERED_COPPER_GRATE);
        dropOther(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get(), Blocks.WAXED_OXIDIZED_COPPER_GRATE);

        dropOther(BlockRegistry.snow_cauldron.get(), Blocks.CAULDRON);
        dropOther(BlockRegistry.ice_cauldron.get(), Blocks.CAULDRON);

    }


    protected void createCoreDrop(Block pBlock, Item pItem) {
        add(pBlock,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pBlock)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GreenHouseCoreBlock.AGE,GreenHouseCoreBlock.MAX_STAGE)))
                                .add(LootItem.lootTableItem(pBlock)
                                        .when(this.hasSilkTouch()).otherwise(LootItem.lootTableItem(pItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))))
        );
    }

}

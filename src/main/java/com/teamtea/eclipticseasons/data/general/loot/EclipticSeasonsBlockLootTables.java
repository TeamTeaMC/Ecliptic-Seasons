package com.teamtea.eclipticseasons.data.general.loot;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.stream.Collectors;

public class EclipticSeasonsBlockLootTables extends BlockLootSubProvider {

    public EclipticSeasonsBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
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

        dropOther(BlockRegistry.snow_cauldron.get(), Blocks.CAULDRON);
        dropOther(BlockRegistry.ice_cauldron.get(), Blocks.CAULDRON);
    }

    protected void createCoreDrop(Block pBlock, Item pItem) {
        add(pBlock, createSilkTouchDispatchTable(pBlock,
                // HAS_SILK_TOUCH,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(pItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
        );
    }


}

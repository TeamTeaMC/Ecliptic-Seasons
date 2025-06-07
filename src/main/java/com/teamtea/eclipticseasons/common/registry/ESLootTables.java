package com.teamtea.eclipticseasons.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class ESLootTables {
    public static final ResourceKey<LootTable> spring_greenhouse_essence = ResourceKey.create(Registries.LOOT_TABLE, ItemRegistry.spring_greenhouse_essence_item.getId().withPrefix("gifts/"));
    public static final ResourceKey<LootTable> summer_greenhouse_essence = ResourceKey.create(Registries.LOOT_TABLE, ItemRegistry.summer_greenhouse_essence_item.getId().withPrefix("gifts/"));
    public static final ResourceKey<LootTable> autumn_greenhouse_essence = ResourceKey.create(Registries.LOOT_TABLE, ItemRegistry.autumn_greenhouse_essence_item.getId().withPrefix("gifts/"));
    public static final ResourceKey<LootTable> winter_greenhouse_essence = ResourceKey.create(Registries.LOOT_TABLE, ItemRegistry.winter_greenhouse_essence_item.getId().withPrefix("gifts/"));
}

package com.teamtea.eclipticseasons.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class ESLootTables {
    public static final ResourceLocation spring_greenhouse_essence =  ItemRegistry.spring_greenhouse_essence_item.getId().withPrefix("gifts/");
    public static final ResourceLocation summer_greenhouse_essence =  ItemRegistry.summer_greenhouse_essence_item.getId().withPrefix("gifts/");
    public static final ResourceLocation autumn_greenhouse_essence =  ItemRegistry.autumn_greenhouse_essence_item.getId().withPrefix("gifts/");
    public static final ResourceLocation winter_greenhouse_essence =  ItemRegistry.winter_greenhouse_essence_item.getId().withPrefix("gifts/");
}

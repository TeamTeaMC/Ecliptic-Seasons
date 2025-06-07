package com.teamtea.eclipticseasons.common.loot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class NewFunLoot implements LootItemFunction {
    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return null;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return null;
    }
}

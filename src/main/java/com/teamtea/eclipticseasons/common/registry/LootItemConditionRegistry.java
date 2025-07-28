package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemConditionRegistry {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> SEASON = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("season", () ->new LootItemConditionType(SeasonCondition.CODEC));

}

package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.loot.SeasonalSimulationLevelLootCondition;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

public class LootItemConditionRegistry {
    public static final DeferredRegister<@NonNull MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<@NonNull MapCodec<? extends LootItemCondition>, @NonNull MapCodec<SeasonCondition>> SEASON = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("season", () ->SeasonCondition.CODEC);

    public static final DeferredHolder<@NonNull MapCodec<? extends LootItemCondition>, @NonNull MapCodec<SeasonalSimulationLevelLootCondition>> SEASONAL_SIMULATION_LEVEL = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("seasonal_simulation_level", () -> SeasonalSimulationLevelLootCondition.CODEC);

}

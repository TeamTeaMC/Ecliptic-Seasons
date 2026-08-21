package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import com.teamtea.eclipticseasons.common.loot.SeasonalSimulationLevelLootCondition;
import com.teamtea.eclipticseasons.common.loot.LootCodecSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LootItemConditionRegistry {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EclipticSeasonsApi.MODID);

    public static final RegistryObject<LootItemConditionType> SEASON = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("season", () -> new LootItemConditionType(new LootCodecSerializer<>(SeasonCondition.CONDITION_CODEC)));

    public static final RegistryObject<LootItemConditionType> SEASONAL_SIMULATION_LEVEL = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("seasonal_simulation_level", () -> new LootItemConditionType(new LootCodecSerializer<>(SeasonalSimulationLevelLootCondition.CODEC.codec())));

}

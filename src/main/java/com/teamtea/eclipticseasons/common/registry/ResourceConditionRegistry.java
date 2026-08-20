package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ResourceConditionRegistry {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_DEFERRED_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<? extends ICondition>> SEASONAL_SIMULATION_LEVEL = CONDITION_DEFERRED_REGISTER.register("seasonal_simulation_level", () -> SeasonalSimulationLevelCondition.CODEC);


}

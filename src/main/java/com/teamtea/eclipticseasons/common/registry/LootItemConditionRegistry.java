package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.loot.SeasonCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class LootItemConditionRegistry {
    public static final DeferredRegister<@NotNull MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<@NotNull MapCodec<? extends LootItemCondition>, @NotNull MapCodec<SeasonCondition>> SEASON = LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register("season", () ->SeasonCondition.CODEC);

}

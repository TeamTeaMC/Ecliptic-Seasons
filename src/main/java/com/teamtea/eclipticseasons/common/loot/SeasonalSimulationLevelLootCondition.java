package com.teamtea.eclipticseasons.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.common.registry.LootItemConditionRegistry;
import lombok.Data;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record SeasonalSimulationLevelLootCondition(SeasonalSimulationLevel level) implements LootItemCondition {


    public static final MapCodec<SeasonalSimulationLevelLootCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(SeasonalSimulationLevel.CODEC.fieldOf("level").forGetter(SeasonalSimulationLevelLootCondition::level))
                    .apply(instance, SeasonalSimulationLevelLootCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditionRegistry.SEASONAL_SIMULATION_LEVEL.get();
    }

    @Override
    public boolean test(LootContext context) {
        return EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel().enable(level);
    }

    public static Builder instance(SeasonalSimulationLevel level) {
        return new Builder(level);
    }

    @Data
    public static class Builder implements LootItemCondition.Builder {

        final SeasonalSimulationLevel level;

        @Override
        public @NotNull LootItemCondition build() {
            return new SeasonalSimulationLevelLootCondition(level);
        }
    }
}
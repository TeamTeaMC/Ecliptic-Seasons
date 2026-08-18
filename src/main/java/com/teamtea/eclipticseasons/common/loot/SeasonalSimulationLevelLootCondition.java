package com.teamtea.eclipticseasons.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import lombok.Data;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

public record SeasonalSimulationLevelLootCondition(SeasonalSimulationLevel level) implements LootItemCondition {


    public static final MapCodec<SeasonalSimulationLevelLootCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(SeasonalSimulationLevel.CODEC.fieldOf("level").forGetter(SeasonalSimulationLevelLootCondition::level))
                    .apply(instance, SeasonalSimulationLevelLootCondition::new)
    );

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        return EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel().enable(level);
    }

    public static SeasonalSimulationLevelLootCondition.Builder instance(SeasonalSimulationLevel level) {
        return new SeasonalSimulationLevelLootCondition.Builder(level);
    }

    @Data
    public static class Builder implements LootItemCondition.Builder {

        final SeasonalSimulationLevel level;

        @Override
        public @NonNull LootItemCondition build() {
            return new SeasonalSimulationLevelLootCondition(level);
        }
    }
}
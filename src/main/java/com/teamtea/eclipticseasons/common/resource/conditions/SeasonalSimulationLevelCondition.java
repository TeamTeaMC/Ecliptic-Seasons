package com.teamtea.eclipticseasons.common.resource.conditions;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import lombok.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;

@Builder
public record SeasonalSimulationLevelCondition(
        SeasonalSimulationLevel level
) implements ICondition {

    public static final MapCodec<SeasonalSimulationLevelCondition> CODEC =
            SeasonalSimulationLevel.CODEC
                    .fieldOf("level")
                    .xmap(SeasonalSimulationLevelCondition::new,
                            SeasonalSimulationLevelCondition::level);

    @Override
    public boolean test(IContext context) {
        return EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel().enable(level);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
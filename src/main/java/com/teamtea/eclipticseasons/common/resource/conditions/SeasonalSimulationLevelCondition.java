package com.teamtea.eclipticseasons.common.resource.conditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import lombok.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.Locale;

@Builder
public record SeasonalSimulationLevelCondition(
        SeasonalSimulationLevel level
) implements ICondition {
    private static final ResourceLocation NAME = EclipticSeasons.rl("seasonal_simulation_level");

    public static final MapCodec<SeasonalSimulationLevelCondition> CODEC =
            SeasonalSimulationLevel.CODEC
                    .fieldOf("level")
                    .xmap(SeasonalSimulationLevelCondition::new,
                            SeasonalSimulationLevelCondition::level);

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel().enable(level);
    }

    public static class Serializer implements IConditionSerializer<SeasonalSimulationLevelCondition> {
        public static Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, SeasonalSimulationLevelCondition value) {
            json.addProperty("level", value.level.getSerializedName());
        }

        @Override
        public SeasonalSimulationLevelCondition read(JsonObject json) {
            return new SeasonalSimulationLevelCondition(SeasonalSimulationLevel.valueOf(GsonHelper.getAsString(json, "level").toUpperCase(Locale.ROOT)));
        }

        @Override
        public ResourceLocation getID() {
            return SeasonalSimulationLevelCondition.NAME;
        }
    }
}
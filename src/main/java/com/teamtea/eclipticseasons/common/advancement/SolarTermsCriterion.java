package com.teamtea.eclipticseasons.common.advancement;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SolarTermsCriterion extends SimpleCriterionTrigger<SolarTermsCriterion.TriggerInstance> {



    public void trigger(ServerPlayer player) {
        this.trigger(player, (TriggerInstance::test));
    }

    final ResourceLocation id;

    public SolarTermsCriterion(ResourceLocation pId) {
        this.id = pId;
    }

    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected @NotNull TriggerInstance createInstance(@NotNull JsonObject json, ContextAwarePredicate player, DeserializationContext conditionsParser) {
        return new TriggerInstance(id, player);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance
    {
        public TriggerInstance(ResourceLocation id,ContextAwarePredicate player) {
            super(id, player);
        }

        public static TriggerInstance simple() {
            return new TriggerInstance(EclipticSeasons.rl("solar_terms"),
                    ContextAwarePredicate.ANY);
        }

        public static TriggerInstance simple2() {
            return new TriggerInstance(EclipticSeasons.rl("heat_stroke"),
                    ContextAwarePredicate.ANY);
        }

        public boolean test() {
            return true;
        }
    }
}

package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class SolarTermsCriterion extends SimpleCriterionTrigger<SolarTermsCriterion.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return SolarTermsCriterion.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (TriggerInstance::test));
    }


    public record TriggerInstance(
            Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SolarTermsCriterion.TriggerInstance::player))
                        .apply(builder, SolarTermsCriterion.TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> simple() {
            return EclipticSeasons.ModContents.SOLAR_TERMS.get().createCriterion(
                    new SolarTermsCriterion.TriggerInstance(Optional.empty())
            );
        }

        public static Criterion<TriggerInstance> simple2() {
            return EclipticSeasons.ModContents.heatStroke.get().createCriterion(
                    new SolarTermsCriterion.TriggerInstance(Optional.empty())
            );
        }


        public boolean test() {
            return true;
        }
    }
}

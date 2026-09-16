package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class SolarTermsCriterion extends SimpleCriterionTrigger<SolarTermsCriterion.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (TriggerInstance::test));
    }


    public record TriggerInstance(
            Optional<Holder<LootItemCondition>> player) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                                LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player))
                        .apply(builder, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> simple() {
            return ModAdvancements.SOLAR_TERMS.get().createCriterion(
                    new TriggerInstance(Optional.empty())
            );
        }

        public static Criterion<TriggerInstance> simple2() {
            return ModAdvancements.SOLAR_TERMS.get().createCriterion(
                    new TriggerInstance(Optional.empty())
            );
        }

        public static Criterion<TriggerInstance> simple3() {
            return ModAdvancements.SOLAR_TERMS.get().createCriterion(
                    new TriggerInstance(Optional.empty())
            );
        }


        public boolean test() {
            return true;
        }
    }
}

package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.function.Consumer;

@FunctionalInterface
public interface ConditionalAdvancementOutput
        extends Consumer<AdvancementHolder> {

    void accept(
            AdvancementHolder advancement,
            ICondition... conditions
    );

    @Override
    default void accept(AdvancementHolder advancement) {
        accept(advancement, new ICondition[0]);
    }

    default Consumer<AdvancementHolder> withConditions(
            ICondition... conditions
    ) {
        return advancement -> accept(advancement, conditions);
    }
}
package com.teamtea.eclipticseasons.client.model.block.unbake.multipart;


import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.List;
import java.util.function.Predicate;

public class AndConditionLike implements ConditionLike {

    private final List<ConditionLike> conditions;

    public AndConditionLike(List<? extends ConditionLike> conditions) {
        this.conditions = List.copyOf(conditions);
    }


    public List<ConditionLike> getConditions() {
        return conditions;
    }

    @Override
    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> definition) {
        List<Predicate<S>> list = conditions.stream().map(c -> c.instantiate(definition)).toList();
        return s -> {
            for (var condition : list) {
                if (!condition.test(s)) return false;
            }
            return true;
        };
    }
}

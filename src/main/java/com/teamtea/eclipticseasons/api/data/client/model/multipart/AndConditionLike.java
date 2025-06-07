package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import net.minecraft.client.renderer.block.model.multipart.AndCondition;

import java.util.List;

public class AndConditionLike extends AndCondition implements ConditionLike {

    private final List<ConditionLike> conditions;

    public AndConditionLike(List<? extends ConditionLike> conditions) {
        super(conditions);
        this.conditions = List.copyOf(conditions);
    }

    @Override
    public String getTypeKey() {
        return TOKEN;
    }

    public List<ConditionLike> getConditions() {
        return conditions;
    }
}

package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import net.minecraft.client.renderer.block.model.multipart.OrCondition;

import java.util.List;

public class OrConditionLike extends OrCondition implements ConditionLike {

    private final List<ConditionLike> conditions;

    public OrConditionLike(List<ConditionLike> conditions) {
        super(conditions);
        this.conditions = conditions;
    }

    @Override
    public String getTypeKey() {
        return TOKEN;
    }

    public List<ConditionLike> getConditions() {
        return conditions;
    }
}

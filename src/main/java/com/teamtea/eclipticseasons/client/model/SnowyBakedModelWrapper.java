package com.teamtea.eclipticseasons.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class SnowyBakedModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> {
    public SnowyBakedModelWrapper(T originalModel) {
        super(originalModel);
    }
}

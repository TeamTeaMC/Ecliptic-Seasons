package com.teamtea.eclipticseasons.client.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class TempReplaceModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> implements IESReplaceModel{


    @Getter
    private final boolean replace=true;

    public TempReplaceModelWrapper(T originalModel) {
        super(originalModel);
    }


}

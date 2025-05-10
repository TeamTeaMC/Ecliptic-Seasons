package com.teamtea.eclipticseasons.client.model;

import lombok.Getter;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

public class TempReplaceModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> implements IReplaceModel{


    @Getter
    private final boolean replace=true;

    public TempReplaceModelWrapper(T originalModel) {
        super(originalModel);
    }


}

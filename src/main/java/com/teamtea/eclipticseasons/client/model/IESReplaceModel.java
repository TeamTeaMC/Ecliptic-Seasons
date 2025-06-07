package com.teamtea.eclipticseasons.client.model;

import net.minecraft.client.resources.model.BakedModel;

public interface IESReplaceModel {
    static boolean isInvalid(BakedModel bakedModel) {
        return bakedModel instanceof IESReplaceModel;
    }

    boolean isReplace();

    // void setReplace(boolean replace);
}

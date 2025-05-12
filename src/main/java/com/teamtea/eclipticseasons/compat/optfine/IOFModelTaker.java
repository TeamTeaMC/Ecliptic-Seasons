package com.teamtea.eclipticseasons.compat.optfine;

import net.minecraft.client.resources.model.BakedModel;

public interface IOFModelTaker {
    BakedModel eclipticseasons$getSnowModel();

    void eclipticseasons$setSnowModel(BakedModel bakedModel);

    BakedModel eclipticseasons$hasCache(BakedModel bakedModel, boolean special);

    void eclipticseasons$setCache(BakedModel bakedModel,BakedModel bakedModel2, boolean special);
}

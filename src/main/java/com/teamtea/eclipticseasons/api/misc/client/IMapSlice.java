package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.client.resources.model.BakedModel;

public interface IMapSlice extends IMapSliceProvider{
    default void forceMapSliceUpdate(int[][] SOLID_HEIGHT_MAP, int SIZE_X, int SIZE_Z){};

    BakedModel eclipticseasons$getSnowModel();

    void eclipticseasons$setSnowModel(BakedModel bakedModel);
}

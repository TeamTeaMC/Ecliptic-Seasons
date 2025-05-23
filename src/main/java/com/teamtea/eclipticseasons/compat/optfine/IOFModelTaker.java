package com.teamtea.eclipticseasons.compat.optfine;

import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import net.minecraft.client.resources.model.BakedModel;

public interface IOFModelTaker extends IMapSlice {

    BakedModel eclipticseasons$hasCache(BakedModel bakedModel, boolean special);

    void eclipticseasons$setCache(BakedModel bakedModel, BakedModel bakedModel2, boolean special);
}

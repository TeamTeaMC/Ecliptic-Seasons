package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.client.resources.model.BakedModel;

public interface ISnowyBlockState {

    @Deprecated
    BakedModel getSnowyModel(int loadVersion);

    @Deprecated
    void setSnowyModel(BakedModel bakedModel, int loadVersion);

    @Deprecated
    BakedModel getSnowyModel2(int loadVersion2);

    @Deprecated
    void setSnowyModel2(BakedModel bakedModel, int loadVersion2);

}

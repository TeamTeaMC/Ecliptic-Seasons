package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.client.resources.model.BakedModel;

public interface ISnowyBlockState {
    BakedModel getSnowyModel(int loadVersion);

    void setSnowyModel(BakedModel bakedModel,int loadVersion);
}

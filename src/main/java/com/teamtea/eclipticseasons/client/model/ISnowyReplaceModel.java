package com.teamtea.eclipticseasons.client.model;

import net.minecraft.client.resources.model.BakedModel;

public interface ISnowyReplaceModel extends IESReplaceModel {

    static boolean isInvalid(BakedModel bakedModel) {
        return bakedModel instanceof ISnowyReplaceModel iSnowyReplaceModel
                && iSnowyReplaceModel.getBindBlockType() < 0;
    }

    boolean isLowLayer();

    void setLowLayer(boolean lowLayer);


    void updateBlockType(int bindBlockType);

    int getBindBlockType();

}

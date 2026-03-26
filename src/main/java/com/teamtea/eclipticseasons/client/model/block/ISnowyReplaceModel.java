package com.teamtea.eclipticseasons.client.model.block;


import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;

public interface ISnowyReplaceModel extends IESReplaceModel {

    static boolean isInvalid(IESReplaceModel bakedModel) {
        return bakedModel instanceof ISnowyReplaceModel iSnowyReplaceModel
                && iSnowyReplaceModel.getBindBlockType() < 0;
    }

    static boolean isInvalid(BlockStateModelPart bakedModel) {
        return bakedModel instanceof IESReplaceModel;
    }

    static boolean isInvalid(BlockStateModel bakedModel) {
        return bakedModel instanceof IESReplaceModel;
    }

    boolean isLowLayer();

    void setLowLayer(boolean lowLayer);


    void updateBlockType(int bindBlockType);

    int getBindBlockType();

}

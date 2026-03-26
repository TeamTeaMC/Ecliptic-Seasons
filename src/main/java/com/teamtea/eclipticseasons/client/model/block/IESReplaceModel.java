package com.teamtea.eclipticseasons.client.model.block;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;

public interface IESReplaceModel {
    static boolean isInvalid(BlockStateModelPart bakedModel) {
        return bakedModel instanceof IESReplaceModel;
    }

    boolean isReplace();

    // void setReplace(boolean replace);
}

package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.client.core.ESRendererHolderImpl;
import com.teamtea.eclipticseasons.client.model.IESReplaceModel;
import net.minecraft.client.resources.model.BakedModel;

public interface IESRendererHolder {
    ESRendererHolderImpl eclipticseasons$getContext();

    static ESRendererHolderImpl of(Object o) {
        if (o instanceof IESRendererHolder rendererHolder)
            return rendererHolder.eclipticseasons$getContext();
        return ESRendererHolderImpl.EMPTY;
    }

    static BakedModel getOriginalModel(Object context, BakedModel bakedModel) {
        ESRendererHolderImpl rendererHolder = of(context);
        return !IESReplaceModel.isInvalid(rendererHolder.getExtraModel())
                || rendererHolder.getOriginalModel() == null ? bakedModel :
                rendererHolder.getOriginalModel();
    }

}

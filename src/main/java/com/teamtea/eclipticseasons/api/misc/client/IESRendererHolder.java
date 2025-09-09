package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.client.core.ESRendererHolderImpl;
import com.teamtea.eclipticseasons.client.model.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

public interface IESRendererHolder {
    ESRendererHolderImpl eclipticseasons$getContext();

    static ESRendererHolderImpl of(Object o) {
        if (o instanceof IESRendererHolder rendererHolder)
            return rendererHolder.eclipticseasons$getContext();
        return ESRendererHolderImpl.EMPTY;
    }

    static Boolean getModelForAmbientOcclusion(Object context, BlockState state, RenderType renderType) {
        ESRendererHolderImpl rendererHolder = of(context);
        if (rendererHolder.getExtraModel() instanceof ISnowyReplaceModel snowyBakedModelWrapper) {
            if (snowyBakedModelWrapper.getBindBlockType() == MapChecker.FLAG_CUSTOM_AO) {
                return true;
            }
        }
        return null;
    }

}

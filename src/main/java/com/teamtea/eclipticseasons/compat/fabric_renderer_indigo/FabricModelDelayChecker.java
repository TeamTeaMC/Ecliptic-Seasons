package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import com.teamtea.eclipticseasons.api.misc.client.IESRendererHolder;
import com.teamtea.eclipticseasons.client.core.ESRendererHolderImpl;
import com.teamtea.eclipticseasons.client.model.IESReplaceModel;
import net.minecraft.client.resources.model.BakedModel;
import org.embeddedt.embeddium.render.frapi.FRAPIModelUtils;

public interface FabricModelDelayChecker {
    BakedModel isLastFabric();

    void updateIsLastFabric(BakedModel is);

    static boolean asFabricMode(Object o) {
        ESRendererHolderImpl rendererHolder = IESRendererHolder.of(o);
        return IESReplaceModel.isInvalid(rendererHolder.getExtraModel())
                && (rendererHolder.getOriginalModel() != null
                && FRAPIModelUtils.isFRAPIModel(rendererHolder.getOriginalModel()));
    }
}

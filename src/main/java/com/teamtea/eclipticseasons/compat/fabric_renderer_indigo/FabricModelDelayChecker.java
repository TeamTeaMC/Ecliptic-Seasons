package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.model.IESReplaceModel;
import org.embeddedt.embeddium.impl.render.frapi.FRAPIModelUtils;

public class FabricModelDelayChecker {

    public static boolean asFabricMode(Object o) {
        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(o);
        return IESReplaceModel.isInvalid(rendererHolder.getExtraModel())
                && (rendererHolder.getOriginalModel() != null
                && FRAPIModelUtils.isFRAPIModel(rendererHolder.getOriginalModel()));
    }
}

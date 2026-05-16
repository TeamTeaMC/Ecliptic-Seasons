package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.client.core.context.ExtraRendererContext;

public interface IExtraRendererContextOwner {
    ExtraRendererContext eclipticseasons$getContext();

    static ExtraRendererContext of(Object o) {
        if (o instanceof IExtraRendererContextOwner rendererHolder)
            return rendererHolder.eclipticseasons$getContext();
        return ExtraRendererContext.EMPTY;
    }

}

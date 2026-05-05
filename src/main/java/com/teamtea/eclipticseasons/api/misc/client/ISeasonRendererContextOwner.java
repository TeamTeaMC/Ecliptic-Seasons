package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.client.core.context.SeasonRendererContext;

public interface ISeasonRendererContextOwner {
    SeasonRendererContext eclipticseasons$getContext();

    static SeasonRendererContext of(Object o) {
        if (o instanceof ISeasonRendererContextOwner rendererHolder)
            return rendererHolder.eclipticseasons$getContext();
        return SeasonRendererContext.EMPTY;
    }

}

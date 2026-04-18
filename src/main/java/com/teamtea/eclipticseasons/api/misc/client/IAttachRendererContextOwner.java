package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.client.core.context.AttachRendererContext;

public interface IAttachRendererContextOwner {
    AttachRendererContext eclipticseasons$getContext();

    static AttachRendererContext of(Object o) {
        if (o instanceof IAttachRendererContextOwner rendererHolder)
            return rendererHolder.eclipticseasons$getContext();
        return AttachRendererContext.EMPTY;
    }

}

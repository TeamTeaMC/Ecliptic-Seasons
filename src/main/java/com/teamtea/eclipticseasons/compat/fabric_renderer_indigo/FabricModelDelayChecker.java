package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import net.minecraft.client.resources.model.BakedModel;

public interface FabricModelDelayChecker {
    BakedModel isLastFabric();

    void updateIsLastFabric(BakedModel is);
}

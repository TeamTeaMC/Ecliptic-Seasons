package com.teamtea.eclipticseasons.compat.sodium;


import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;

import java.util.List;

public interface SodiumStatus {
    void eclipticSeasons$bindCounter(SodiumBoard sodiumBoard);

    List<BakedQuad> getCacheBakeQuad();

    BakedModel getSnowModel();
}

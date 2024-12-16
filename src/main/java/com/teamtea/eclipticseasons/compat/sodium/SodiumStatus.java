package com.teamtea.eclipticseasons.compat.sodium;


import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public interface SodiumStatus {
    void eclipticSeasons$bindCounter(SodiumBoard sodiumBoard);

    List<BakedQuad> getCacheBakeQuad();
}

package com.teamtea.eclipticseasons.compat.vanilla;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;

import java.util.List;


public interface ExtendBlockView extends IExtendBlockView {

    void setCacheBakeQuad();

    void resetCacheBakeQuad();

    List<BakedQuad> getCacheBakeQuad();

    void clearCacheBakeQuad();

    void addCacheBakeQuad(BakedQuad bakedQuad);

    void setShouldCollectBakeQuads(boolean shouldCollectBakeQuads);

    boolean getShouldCollectBakeQuads();


    default void cleanAfterRender() {
        clearCacheBakeQuad();
        setShouldCollectBakeQuads(false);
    }

    default void finishChunkRender() {
        resetCacheBakeQuad();
        setShouldCollectBakeQuads(false);
    }

    default void startChunkRender() {
        setCacheBakeQuad();
        setShouldCollectBakeQuads(false);
    }
}

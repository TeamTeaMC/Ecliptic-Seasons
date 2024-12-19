package com.teamtea.eclipticseasons.compat.vanilla;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;

import java.util.List;


public interface ExtendBlockView {

    void setCacheBakeQuad();

    void resetCacheBakeQuad();

    List<BakedQuad> getCacheBakeQuad();

    void clearCacheBakeQuad();

    void addCacheBakeQuad(BakedQuad bakedQuad);

    void setSnowModel(BakedModel bakedModel);

    void resetSnowModel();

    BakedModel getSnowModel();

    void setCurrentModelReplaceable(boolean isReplaceable);

    boolean isCurrentModelReplaceable();

    void setShouldCollectBakeQuads(boolean shouldCollectBakeQuads);

    boolean getShouldCollectBakeQuads();

    BlockPos.MutableBlockPos getModelCheckPos();

    default void cleanAfterRender() {
        clearCacheBakeQuad();
        resetSnowModel();
        setCurrentModelReplaceable(false);
        setShouldCollectBakeQuads(false);
    }

    default void finishChunkRender() {
        resetCacheBakeQuad();
        resetSnowModel();
        setCurrentModelReplaceable(false);
        setShouldCollectBakeQuads(false);
    }

    default void startChunkRender() {
        setCacheBakeQuad();
        resetSnowModel();
        setCurrentModelReplaceable(false);
        setShouldCollectBakeQuads(false);
    }
}

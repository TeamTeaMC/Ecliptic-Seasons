package com.teamtea.eclipticseasons.mixin.client.chunk;

import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;


// 这里基本靠RenderChunkRegion传递信息了
@Mixin(RenderChunkRegion.class)
public abstract class MixinChunkRendererRegion_ExtendBlockView implements ExtendBlockView {
    @Unique
    private List<BakedQuad> eclipticseasons$bakedQuads = null;

    @Unique
    private BakedModel eclipticseasons$snowModel = null;

    @Unique
    private boolean eclipticseasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticseasons$shouldReplaceOriginalGrassModel = false;

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$mutableBlockPos =new BlockPos.MutableBlockPos();

    @Override
    public List<BakedQuad> getCacheBakeQuad() {
        return eclipticseasons$bakedQuads;
    }

    @Override
    public void clearCacheBakeQuad() {
        if (eclipticseasons$bakedQuads != null)
            eclipticseasons$bakedQuads.clear();
    }

    @Override
    public void addCacheBakeQuad(BakedQuad bakedQuad) {
        if (eclipticseasons$bakedQuads != null
                && eclipticseasons$shouldCollectBakeQuads)
            eclipticseasons$bakedQuads.add(bakedQuad);
    }

    @Override
    public void setCacheBakeQuad() {
        eclipticseasons$bakedQuads = new ArrayList<>();
    }

    @Override
    public void resetCacheBakeQuad() {
        eclipticseasons$bakedQuads = null;
    }

    @Override
    public void setSnowModel(BakedModel bakedModel) {
        eclipticseasons$snowModel = bakedModel;
    }

    @Override
    public void resetSnowModel() {
        eclipticseasons$snowModel = null;
    }

    @Override
    public BakedModel getSnowModel() {
        return eclipticseasons$snowModel;
    }


    @Override
    public void setCurrentModelReplaceable(boolean isReplaceable) {
        this.eclipticseasons$shouldReplaceOriginalGrassModel = isReplaceable;
    }

    @Override
    public void setShouldCollectBakeQuads(boolean shouldCollectBakeQuads) {
        this.eclipticseasons$shouldCollectBakeQuads = shouldCollectBakeQuads;
    }

    @Override
    public boolean isCurrentModelReplaceable() {
        return this.eclipticseasons$shouldReplaceOriginalGrassModel;
    }

    @Override
    public boolean getShouldCollectBakeQuads() {
        return this.eclipticseasons$shouldCollectBakeQuads;
    }

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$mutableBlockPos;
    }
}

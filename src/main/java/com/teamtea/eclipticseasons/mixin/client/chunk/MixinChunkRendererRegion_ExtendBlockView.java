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
    private List<BakedQuad> eclipticSeasons$bakedQuads = null;

    @Unique
    private BakedModel eclipticSeasons$snowModel = null;

    @Unique
    private boolean eclipticSeasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticSeasons$shouldReplaceOriginalGrassModel = false;

    @Unique
    private BlockPos.MutableBlockPos eclipticSeasons$mutableBlockPos =new BlockPos.MutableBlockPos();

    @Override
    public List<BakedQuad> getCacheBakeQuad() {
        return eclipticSeasons$bakedQuads;
    }

    @Override
    public void clearCacheBakeQuad() {
        if (eclipticSeasons$bakedQuads != null)
            eclipticSeasons$bakedQuads.clear();
    }

    @Override
    public void addCacheBakeQuad(BakedQuad bakedQuad) {
        if (eclipticSeasons$bakedQuads != null
                && eclipticSeasons$shouldCollectBakeQuads)
            eclipticSeasons$bakedQuads.add(bakedQuad);
    }

    @Override
    public void setCacheBakeQuad() {
        eclipticSeasons$bakedQuads = new ArrayList<>();
    }

    @Override
    public void resetCacheBakeQuad() {
        eclipticSeasons$bakedQuads = null;
    }

    @Override
    public void setSnowModel(BakedModel bakedModel) {
        eclipticSeasons$snowModel = bakedModel;
    }

    @Override
    public void resetSnowModel() {
        eclipticSeasons$snowModel = null;
    }

    @Override
    public BakedModel getSnowModel() {
        return eclipticSeasons$snowModel;
    }


    @Override
    public void setCurrentModelReplaceable(boolean isReplaceable) {
        this.eclipticSeasons$shouldReplaceOriginalGrassModel = isReplaceable;
    }

    @Override
    public void setShouldCollectBakeQuads(boolean shouldCollectBakeQuads) {
        this.eclipticSeasons$shouldCollectBakeQuads = shouldCollectBakeQuads;
    }

    @Override
    public boolean isCurrentModelReplaceable() {
        return this.eclipticSeasons$shouldReplaceOriginalGrassModel;
    }

    @Override
    public boolean getShouldCollectBakeQuads() {
        return this.eclipticSeasons$shouldCollectBakeQuads;
    }

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticSeasons$mutableBlockPos;
    }
}

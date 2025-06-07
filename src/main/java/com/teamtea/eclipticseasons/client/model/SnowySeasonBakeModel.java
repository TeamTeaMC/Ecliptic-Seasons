package com.teamtea.eclipticseasons.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SnowySeasonBakeModel<T extends BakedModel> extends BakedModelWrapper<T> implements ISnowyReplaceModel {

    private final T snowModel;
    private final ChunkRenderTypeSet snowRenderTypes;
    private boolean lowlayer = false;
    private boolean replace = false;
    private int flag = -1;

    public SnowySeasonBakeModel(T seasonModel, T snowModel, RenderType snowChunkRenderType) {
        super(seasonModel);
        this.snowModel = snowModel;
        this.snowRenderTypes = ChunkRenderTypeSet.of(snowChunkRenderType);
    }


    @SuppressWarnings("deprecated")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return combineBakedQuads(originalModel.getQuads(state, side, rand),snowModel.getQuads(state, side, rand));
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return combineBakedQuads(this.originalModel.getQuads(state, side, rand, extraData, renderType),this.snowModel.getQuads(state, side, rand, extraData, renderType));
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        ChunkRenderTypeSet renderTypes = snowModel.getRenderTypes(state, rand, data);
        return ChunkRenderTypeSet.union(renderTypes, snowRenderTypes);
    }

    protected @NotNull List<BakedQuad> combineBakedQuads(List<BakedQuad> quads, List<BakedQuad> snowModelQuads_Ori) {
        if (snowModelQuads_Ori.isEmpty()) return quads;
        if (quads.isEmpty()) return snowModelQuads_Ori;

        List<BakedQuad> snowModelQuads = new ArrayList<>(quads.size() + snowModelQuads_Ori.size());
        snowModelQuads.addAll(quads);
        snowModelQuads.addAll(snowModelQuads_Ori);
        return snowModelQuads;
    }

    @Override
    public boolean isLowLayer() {
        return this.lowlayer;
    }

    @Override
    public void setLowLayer(boolean lowLayer) {
        this.lowlayer = lowLayer;
    }

    @Override
    public void updateBlockType(int bindBlockType) {
        this.flag = bindBlockType;
    }

    @Override
    public int getBindBlockType() {
        return this.flag;
    }

    @Override
    public boolean isReplace() {
        return this.replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }
}

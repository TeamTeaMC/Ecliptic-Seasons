package com.teamtea.eclipticseasons.client.model;

import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MulBakeModel<T extends BakedModel> extends BakedModelWrapper<T> {
    public static final ModelData ES_DATA = ModelData.builder().with(new ModelProperty<>(), null).build();

    public static final List<BakedQuad> EMPTY_LIST = new ArrayList<>();
    private final T esModel;
    private final boolean customRuntime;
    private final boolean replace;
    private final boolean valid;
    private final RenderType snowChunkRenderType;
    private final ChunkRenderTypeSet snowRenderTypes;
    private final int flag;
    private final boolean snowy;

    public MulBakeModel(T originalModel, T esModel, boolean replace, RenderType snowChunkRenderType) {
        super(originalModel);
        this.flag = esModel instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper ?
                snowyBakedModelWrapper.getBindBlockType() : 0;
        this.customRuntime = this.flag == MapChecker.FLAG_CUSTOM;
        this.esModel = esModel;
        this.valid = esModel != null;
        this.replace = replace;
        this.snowChunkRenderType = snowChunkRenderType;
        this.snowRenderTypes = ChunkRenderTypeSet.of(this.snowChunkRenderType);
        this.snowy = esModel instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper && !ISnowyReplaceModel.isInvalid(snowyBakedModelWrapper);

    }


    protected List<BakedQuad> getCustomBakedQuads(@Nullable BlockState state, List<BakedQuad> quads, @Nullable Direction side) {
        if (ModelManager.shouldMakeSnowyBakedQuads(flag, side)) {
            List<BakedQuad> snowModelQuads = new ArrayList<>(quads.size());
            // for (BakedQuad quad : quads) {
            //     if (quad.getDirection() != Direction.UP) {
            //         snowModelQuads.add(quad);
            //     }
            // }
            snowModelQuads.addAll(ModelManager.makeSnowyBakedQuads(state, quads, new ArrayList<>(quads)));
            return snowModelQuads;
        }
        return EMPTY_LIST;
    }


    protected List<BakedQuad> cancelTop(List<BakedQuad> quads) {
        if (snowy) {
            List<BakedQuad> subQuads = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                if (quad.getDirection() != Direction.UP) {
                    subQuads.add(quad);
                }
            }
            return subQuads.size() == quads.size() ? quads : subQuads;
        } else return quads;
    }

    protected @NotNull List<BakedQuad> combineBakedQuads(List<BakedQuad> quads, List<BakedQuad> snowModelQuads_Ori) {
        if (snowModelQuads_Ori.isEmpty()) return quads;
        if (quads.isEmpty()) return snowModelQuads_Ori;

        List<BakedQuad> snowModelQuads = new ArrayList<>(quads.size() + snowModelQuads_Ori.size());
        snowModelQuads.addAll(quads);
        snowModelQuads.addAll(snowModelQuads_Ori);
        return snowModelQuads;
    }

    @SuppressWarnings("deprecated")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (this.replace) {
            return this.esModel.getQuads(state, side, rand);
        }
        List<BakedQuad> quads = this.originalModel.getQuads(state, side, rand);
        if (this.valid) {
            quads = cancelTop(quads);
            quads = customRuntime ?
                    combineBakedQuads(quads, getCustomBakedQuads(state, quads, side)) :
                    combineBakedQuads(quads, esModel.getQuads(state, side, rand));
        }
        return quads;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        if (this.replace) {
            return this.esModel.getQuads(state, side, rand, extraData, renderType);
        }
        boolean buildExtraQuad = shouldBuildExtraQuad(extraData);
        List<BakedQuad> quads = !buildExtraQuad ?
                this.originalModel.getQuads(state, side, rand, extraData, renderType) :
                EMPTY_LIST;
        if (this.valid) {
            quads = cancelTop(quads);
            if (isRenderTypeUse(renderType)) {
                if (buildExtraQuad) {
                    if (customRuntime) {
                        if (state != null) {
                            quads = new ArrayList<>();
                            for (RenderType runGet : getRenderTypes(state, rand, extraData)) {
                                quads.addAll(this.originalModel.getQuads(state, side, rand, extraData, runGet));
                            }
                        }
                        quads = getCustomBakedQuads(state, quads, side);
                    } else {
                        quads = esModel.getQuads(state, side, rand, extraData, renderType);
                    }
                }
            }
        }
        return quads;
    }

    public boolean isRenderTypeUse(@Nullable RenderType renderType) {
        return snowRenderTypes.contains(renderType);
    }

    public boolean isSnowy() {
        return snowy;
    }

    protected boolean shouldBuildExtraQuad(ModelData modelData) {
        return modelData == ES_DATA;
    }


    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        if (this.replace) {
            return this.snowRenderTypes;
        }
        ChunkRenderTypeSet renderTypes = originalModel.getRenderTypes(state, rand, data);
        if (valid) {
            if (!renderTypes.contains(snowChunkRenderType)) {
                renderTypes = ChunkRenderTypeSet.union(renderTypes, snowRenderTypes);
            }
        }
        return renderTypes;
    }
}

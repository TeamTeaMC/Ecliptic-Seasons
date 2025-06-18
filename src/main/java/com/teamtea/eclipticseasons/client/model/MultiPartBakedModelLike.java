package com.teamtea.eclipticseasons.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MultiPartBakedModelLike extends MultiPartBakedModel {
    protected final List<Pair<Predicate<BlockState>, BakedModel>> selectors;

    public MultiPartBakedModelLike(List<Pair<Predicate<BlockState>, BakedModel>> pSelectors) {
        super(pSelectors);
        this.selectors = pSelectors;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, @NotNull RandomSource pRandom, net.minecraftforge.client.model.data.@NotNull ModelData modelData, @Nullable net.minecraft.client.renderer.RenderType renderType) {
        // return super.getQuads(pState, pDirection, pRandom, modelData, renderType);
        if (pState == null) {
            return Collections.emptyList();
        } else {
            BitSet bitset = getSelectors(pState);
            List<List<BakedQuad>> list = Lists.newArrayList();
            long k = pRandom.nextLong();

            for(int j = 0; j < bitset.length(); ++j) {
                if (bitset.get(j)) {
                    var model = this.selectors.get(j).getRight();
                    // if (renderType == null || model.getRenderTypes(pState, pRandom, modelData).contains(renderType)) // FORGE: Only put quad data if the model is using the render type passed
                        list.add(model.getQuads(pState, pDirection, RandomSource.create(k), net.minecraftforge.client.model.data.MultipartModelData.resolve(modelData, model), renderType));
                }
            }
            return net.minecraftforge.common.util.ConcatenatedListView.of(list);
        }
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.all();
    }

    public static class Builder {
        protected final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

        public void add(Predicate<BlockState> pPredicate, BakedModel pModel) {
            this.selectors.add(Pair.of(pPredicate, pModel));
        }

        public BakedModel build() {
            return new MultiPartBakedModelLike(this.selectors);
        }
    }
}

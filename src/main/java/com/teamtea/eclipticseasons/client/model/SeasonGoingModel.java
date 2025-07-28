package com.teamtea.eclipticseasons.client.model;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SeasonGoingModel<T extends BakedModel> extends BakedModelWrapper<T> {
    private final Map<SolarTerm, List<Pair<T, T>>> models;

    public SeasonGoingModel(T originalModel, Map<SolarTerm, List<Pair<T, T>>> models) {
        super(originalModel);
        this.models = models;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (state != null) {
            List<Pair<T, T>> bakedModels = models.get(ClientCon.nowSolarTerm);
            if (bakedModels != null && !bakedModels.isEmpty()) {
                Pair<T, T> pair = bakedModels.size() == 1 ? bakedModels.get(0) : bakedModels.get(rand.nextInt(bakedModels.size()));
                if (pair.getFirst() == pair.getSecond())
                    return pair.getFirst().getQuads(state, side, rand, extraData, renderType);
                return (rand.nextInt(100) >= ClientCon.progress ? pair.getFirst() : pair.getSecond())
                        .getQuads(state, side, rand, extraData, renderType);
            }
        }
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, BlockState state, ModelData modelData) {
        return super.getModelData(level, pos, state, modelData);
    }
}

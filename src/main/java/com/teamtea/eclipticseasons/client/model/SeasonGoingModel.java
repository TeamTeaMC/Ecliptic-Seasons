package com.teamtea.eclipticseasons.client.model;

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
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SeasonGoingModel<T extends BakedModel> extends BakedModelWrapper<T> {
    private final Map<SolarTerm, T> models;

    public SeasonGoingModel(T originalModel, Map<SolarTerm, T> models) {
        super(originalModel);
        this.models = models;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (state != null) {
            T bakedModel = models.get(ClientCon.nowSolarTerm);
            if (bakedModel != null) {
                return bakedModel.getQuads(state, side, rand, extraData, renderType);
            }
        }
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, BlockState state, ModelData modelData) {
        return super.getModelData(level, pos, state, modelData);
    }
}

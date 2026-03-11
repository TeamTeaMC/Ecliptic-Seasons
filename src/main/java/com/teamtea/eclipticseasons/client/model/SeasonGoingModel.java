package com.teamtea.eclipticseasons.client.model;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.data.MultipartModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SeasonGoingModel<T extends BakedModel> extends BakedModelWrapper<T> {
    public static final ModelProperty<Boolean> SNOW_PROPERTY = new ModelProperty<>();

    private final Map<SolarTerm, List<Pair<T, T>>> models;
    private final Map<SolarTerm, List<Pair<T, T>>> snowModels;

    public SeasonGoingModel(T originalModel, Map<SolarTerm, List<Pair<T, T>>> models, Map<SolarTerm, List<Pair<T, T>>> snowModels) {
        super(originalModel);
        this.models = models;
        this.snowModels = snowModels;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        Level useLevel = ClientCon.getUseLevel();
        BakedModel useModel = null;
        if (useLevel != null) {
            useModel = getUsedModel(null, ModelData.EMPTY);
        }
        return useModel != null ? useModel.getQuads(state, side, rand) : super.getQuads(state, side, rand);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (state != null) {
            extraData = MultipartModelData.resolve(extraData, this);
            BakedModel useModel = getUsedModel(rand, extraData);
            if (useModel != null)
                return useModel.getQuads(state, side, rand, extraData, renderType);
        }
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData extraData) {
        extraData = MultipartModelData.resolve(extraData, this);
        BakedModel useModel = getUsedModel(null, extraData);
        return useModel != null ?
                useModel.getParticleIcon(extraData) :
                super.getParticleIcon(extraData);
    }

    public BakedModel getUsedModel(@Nullable RandomSource rand, ModelData extraData) {
        BakedModel useModel = null;
        boolean snowy_block = extraData.has(SNOW_PROPERTY);
        List<Pair<T, T>> bakedModels = (snowy_block ? snowModels : models).get(ClientCon.nowSolarTerm);
        if (snowy_block && (bakedModels == null || bakedModels.isEmpty())) {
            bakedModels = models.get(ClientCon.nowSolarTerm);
        }
        // bakedModels =snowModels.get(ClientCon.nowSolarTerm);
        if (bakedModels != null && !bakedModels.isEmpty()) {
            Pair<T, T> pair = bakedModels.size() == 1 || rand == null ? bakedModels.get(0) : bakedModels.get(rand.nextInt(bakedModels.size()));
            if (pair.getFirst() == pair.getSecond())
                useModel = pair.getFirst();
            else return (rand == null || rand.nextInt(100) >= ClientCon.progress ? pair.getFirst() : pair.getSecond());
        }
        return useModel;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, BlockState state, ModelData modelData) {
        ModelData modelData1 = super.getModelData(level, pos, state, modelData);
        if (ExtraModelManager.canSnowy(level, pos, state, state.getSeed(pos), level instanceof IExtendBlockView extendBlockView ? extendBlockView.getModelCheckPos() : null))
            modelData1 = modelData1
                    .derive().with(SNOW_PROPERTY, true).build();
        return modelData1;
    }


}

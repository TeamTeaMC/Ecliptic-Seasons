package com.teamtea.eclipticseasons.client.model;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.BiomeHolderPredicate;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import lombok.Getter;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.data.MultipartModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeasonBiomeGoingModel<T extends BakedModel> extends BakedModelWrapper<T> {
    public static final ModelProperty<Holder<Biome>> BIOME_PROPERTY = new ModelProperty<>();
    @Getter
    private final List<Pair<BiomeHolderPredicate, SeasonGoingModel<T>>> models;

    public SeasonBiomeGoingModel(T originalModel, List<Pair<BiomeHolderPredicate, SeasonGoingModel<T>>> models) {
        super(originalModel);
        this.models = models;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        Level useLevel = ClientCon.getUseLevel();
        BakedModel useModel = null;
        if (useLevel != null) {
            try {
                useModel = getUsedModel(ModelData.builder().with(BIOME_PROPERTY, useLevel.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS)).build());
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }
        }
        return useModel != null ? useModel.getQuads(state, side, rand) : super.getQuads(state, side, rand);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        extraData = MultipartModelData.resolve(extraData, this);
        BakedModel useModel = getUsedModel(extraData);
        return useModel != null ?
                useModel.getQuads(state, side, rand, extraData, renderType) :
                super.getQuads(state, side, rand, extraData, renderType);
    }


    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(ModelData extraData) {
        extraData = MultipartModelData.resolve(extraData, this);
        BakedModel useModel = getUsedModel(extraData);
        return useModel != null ?
                useModel.getParticleIcon(extraData) :
                super.getParticleIcon(extraData);
    }

    public BakedModel getUsedModel(ModelData extraData) {
        BakedModel useModel = null;
        if (extraData.has(BIOME_PROPERTY)) {
            Holder<Biome> biomeHolder = extraData.get(BIOME_PROPERTY);
            for (Pair<BiomeHolderPredicate, SeasonGoingModel<T>> model : models) {
                if (model.getFirst().test(biomeHolder)) {
                    useModel = model.getSecond();
                }
            }
        }
        return useModel;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, BlockState state, ModelData modelData) {
        ModelData.Builder modelData1 = super.getModelData(level, pos, state, modelData).derive()
                .with(
                        BIOME_PROPERTY,
                        MapChecker.getSurfaceBiome(ClientCon.getUseLevel(), pos)
                );
        if (ExtraModelManager.canSnowy(level, pos, state, state.getSeed(pos), level instanceof IExtendBlockView extendBlockView ? extendBlockView.getModelCheckPos() : null))
            modelData1.with(SeasonGoingModel.SNOW_PROPERTY, true);
        return modelData1.build();
    }

}

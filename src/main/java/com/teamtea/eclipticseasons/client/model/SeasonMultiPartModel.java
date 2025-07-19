package com.teamtea.eclipticseasons.client.model;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.MultipartModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SeasonMultiPartModel<T extends MultiPartBakedModel> extends BakedModelWrapper<T> {

    public SeasonMultiPartModel(T originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState pState, @NotNull ModelData modelData) {
        modelData = super.getModelData(level, pos, pState, modelData);
        Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(ClientCon.getUseLevel(), pos);
        ModelData.Builder derive = modelData.derive();
        derive.with(SeasonBiomeGoingModel.BIOME_PROPERTY, surfaceBiome);
        for (Pair<Predicate<BlockState>, BakedModel> selector : originalModel.selectors) {
            if (selector.getLeft().test(pState)) {
                derive.with(
                        MultipartModelData.PROPERTY, MultipartModelData.builder()
                                .with(selector.getRight(), derive.build())
                                .build()
                );
            }
        }
        return derive.build();
    }
}

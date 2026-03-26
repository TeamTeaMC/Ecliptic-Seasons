package com.teamtea.eclipticseasons.client.core.context;

import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.model.block.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

import javax.annotation.Nonnull;


@Data
@Accessors(chain = true)
public class ExtraRendererContext {

    BlockStateModel originalModel = null;
    BlockStateModel extraModel = null;
    @Nonnull
    ModelData modelData = ModelData.EMPTY;
    boolean replace = false;


    public void resetAll() {
        setExtraModel(null);
        setOriginalModel(null);
        setModelData(ModelData.EMPTY);
        setReplace(false);
    }

    public static final ExtraRendererContext EMPTY = new ExtraRendererContext();


    public static TriState getModelForAmbientOcclusion(Object context, BlockState state, ModelData modelData, ChunkSectionLayer renderType) {
        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(context);
        if (rendererHolder.getExtraModel() instanceof ISnowyReplaceModel snowyBakedModelWrapper) {
            if (snowyBakedModelWrapper.getBindBlockType() == MapChecker.FLAG_CUSTOM_AO) {
                return TriState.TRUE;
            }
        }
        return null;
    }
}

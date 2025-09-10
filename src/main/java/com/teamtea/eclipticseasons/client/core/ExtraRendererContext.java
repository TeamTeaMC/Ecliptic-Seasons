package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.model.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;


@Data
@Accessors(chain = true)
public class ExtraRendererContext {

    BakedModel originalModel = null;
    BakedModel extraModel = null;
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


    public static Boolean getModelForAmbientOcclusion(Object context, BlockState state, RenderType renderType) {
        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(context);
        if (rendererHolder.getExtraModel() instanceof ISnowyReplaceModel snowyBakedModelWrapper) {
            if (snowyBakedModelWrapper.getBindBlockType() == MapChecker.FLAG_CUSTOM_AO) {
                return true;
            }
        }
        return null;
    }
}

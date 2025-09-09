package com.teamtea.eclipticseasons.client.core;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;


@Data
@Accessors(chain = true)
public class ESRendererHolderImpl {

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

    public static final ESRendererHolderImpl EMPTY = new ESRendererHolderImpl();
}

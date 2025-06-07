package com.teamtea.eclipticseasons.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class ItemRenderModel<T extends BakedModel> extends BakedModelWrapper<T> {
    public ItemRenderModel(T originalModel) {
        super(originalModel);
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        // if (transformType == ItemDisplayContext.NONE ||
        //         transformType ==ItemDisplayContext.FIXED)
        //     return this.originalModel.applyTransform(transformType, poseStack,applyLeftHandTransform);
        this.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }
}

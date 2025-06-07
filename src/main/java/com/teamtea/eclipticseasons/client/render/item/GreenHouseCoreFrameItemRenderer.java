package com.teamtea.eclipticseasons.client.render.item;

import com.teamtea.eclipticseasons.client.render.TryModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


public class GreenHouseCoreFrameItemRenderer extends GreenHouseCoreItemRenderer {

    public GreenHouseCoreFrameItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }


    protected int getLightFromItem(ItemStack stack) {
        return LightTexture.FULL_SKY;
    }

    protected Material getMaterialFromItem(ItemStack stack) {
        return TryModel.greenhouse_core_container;
    }

    protected void doAnimate(ModelPart modelPart1, long renderTicks, ItemDisplayContext context) {
    }
}

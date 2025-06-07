package com.teamtea.eclipticseasons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.client.render.TryModel;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;


public class GreenHouseCoreCoreItemRenderer extends GreenHouseCoreItemRenderer {


    public GreenHouseCoreCoreItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        modelPart = TryModel.createCoreLayer().bakeRoot().getChild("All");
    }

    protected PoseStack rotateMatrix(PoseStack matrixStackIn, ItemDisplayContext transformType) {

        if (transformType == ItemDisplayContext.GUI) {
            matrixStackIn.translate(0, 0.375F, 0F);
        } else if (transformType == ItemDisplayContext.GROUND) {
            matrixStackIn.translate(0, 0.375F, 0F);
        } else if (transformType == ItemDisplayContext.FIXED) {
            matrixStackIn.translate(0, 0.5F, -0.25f);
        } else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            matrixStackIn.translate(0, .75, 0);
        } else if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            matrixStackIn.translate(0, .75, 0);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            matrixStackIn.translate(0.75, 0, -.75);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.translate(-0.75, 0, -.75);
        }
        return matrixStackIn;
    }

    @Override
    protected void doAnimate(ModelPart modelPart1, long renderTicks, ItemDisplayContext context) {
        // super.doAnimate(modelPart1, renderTicks, context);

        if (context == ItemDisplayContext.GROUND) {
            modelPart1.xScale = 1;
            modelPart1.yScale = 1;
            modelPart1.zScale = 1;
        } else {
            modelPart1.offsetScale(new Vector3f(.5f, .5f, .5f));
        }
        modelPart1.y += .25f;
        modelPart1.x += .75f;
    }

    @Override
    protected int getLightFromItem(ItemStack stack) {
        return TryModel.getLightFromBlock(getGreenHouseCoreBlockFromItem(stack));
    }

    @Override
    protected Material getMaterialFromItem(ItemStack stack) {
        return TryModel.getMaterialFromBlock(getGreenHouseCoreBlockFromItem(stack));
    }

    private GreenHouseCoreBlock getGreenHouseCoreBlockFromItem(ItemStack stack) {
        if (stack.getItem() == ItemRegistry.summer_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.summer_greenhouse_core.get();
        } else if (stack.getItem() == ItemRegistry.winter_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.winter_greenhouse_core.get();
        } else if (stack.getItem() == ItemRegistry.autumn_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.autumn_greenhouse_core.get();
        }
        return (GreenHouseCoreBlock) BlockRegistry.spring_greenhouse_core.get();
    }
}

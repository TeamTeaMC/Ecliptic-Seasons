package com.teamtea.eclipticseasons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.client.render.TryKeyframe;
import com.teamtea.eclipticseasons.client.render.TryModel;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;


public class GreenHouseCoreItemRenderer extends BlockEntityWithoutLevelRenderer {

    protected ModelPart modelPart;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public GreenHouseCoreItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        this.blockEntityRenderDispatcher = renderDispatcher;
        modelPart = TryModel.createBodyLayer().bakeRoot().getChild("All");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn = rotateMatrix(matrixStackIn, transformType);
        renderModel(stack, matrixStackIn, bufferIn, combinedLightIn, transformType);
        matrixStackIn.popPose();
    }

    //    Not Smart Method ,but have tested
//    translate need n/16 better ,etc 2.5/16,3/16
    protected PoseStack rotateMatrix(PoseStack matrixStackIn, ItemDisplayContext transformType) {

        if (transformType == ItemDisplayContext.GUI) {
            matrixStackIn.translate(0, 0.375F, 0F);
        } else if (transformType == ItemDisplayContext.GROUND) {
        } else if (transformType == ItemDisplayContext.FIXED) {
            matrixStackIn.translate(0, 0.5F, -0.25f);
        } else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
        } else if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            matrixStackIn.translate(0.75, 0, 0);
        } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.translate(0.75, 0, 0);
        }
        return matrixStackIn;
    }

    protected void renderModel(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, ItemDisplayContext context) {

        poseStack.pushPose();

        modelPart.getAllParts().forEach(ModelPart::resetPose);

        long seed = 0;
        long time = (Minecraft.getInstance().level.getGameTime()) % 50L;
        //
        long renderTicks = (long) ((time + 0) * 40);

        renderTicks = renderTicks * 3000 / 2000;
        combinedLight = getLightFromItem(stack);
        VertexConsumer vertexconsumer2 = getMaterialFromItem(stack).buffer(bufferIn, RenderType::itemEntityTranslucentCull);

        doAnimate(modelPart, renderTicks,context);
        // poseStack.translate(0,0.5,0);
        modelPart.render(poseStack, vertexconsumer2, combinedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

    }

    protected int getLightFromItem(ItemStack stack) {
        GreenHouseCoreBlock block = (GreenHouseCoreBlock) ((BlockItem) stack.getItem()).getBlock();
        return TryModel.getLightFromBlock(block);
    }

    protected Material getMaterialFromItem(ItemStack stack) {
        GreenHouseCoreBlock block = (GreenHouseCoreBlock) ((BlockItem) stack.getItem()).getBlock();
        return TryModel.getMaterialFromBlock(block);
    }

    protected void doAnimate(ModelPart modelPart1, long renderTicks, ItemDisplayContext context) {
        TryKeyframe.animate(modelPart1, TryModel.animation, renderTicks, 1, ANIMATION_VECTOR_CACHE);
    }
}

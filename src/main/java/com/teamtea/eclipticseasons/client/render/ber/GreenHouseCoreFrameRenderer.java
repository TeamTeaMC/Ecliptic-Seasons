package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.client.render.TryModel;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreFrameBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class GreenHouseCoreFrameRenderer implements BlockEntityRenderer<GreenHouseCoreFrameBlockEntity> {

    private ModelPart modelPart;


    public GreenHouseCoreFrameRenderer(BlockEntityRendererProvider.Context pContext) {
        modelPart = TryModel.createBodyLayer().bakeRoot().getChild("All");
    }


    @Override
    public void render(GreenHouseCoreFrameBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        ModelPart useModel = modelPart;
        useModel.getAllParts().forEach(ModelPart::resetPose);

        poseStack.pushPose();
        VertexConsumer vertexconsumer2 = TryModel.greenhouse_core_container.buffer(bufferIn, RenderType::itemEntityTranslucentCull);
        poseStack.translate(0, 0.5, 0);
        useModel.x+=1;
        useModel.render(poseStack, vertexconsumer2, combinedLight, combinedOverlay);
        poseStack.popPose();
    }
}

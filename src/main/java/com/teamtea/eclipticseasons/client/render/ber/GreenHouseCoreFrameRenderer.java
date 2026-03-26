package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.client.model.entity.TryModel;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreFrameBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;


public class GreenHouseCoreFrameRenderer implements BlockEntityRenderer<GreenHouseCoreFrameBlockEntity, BlockEntityRenderState> {

    private ModelPart modelPart;


    public GreenHouseCoreFrameRenderer(BlockEntityRendererProvider.Context pContext) {
        modelPart = TryModel.createBodyLayer().bakeRoot().getChild("All");
    }


    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        ModelPart useModel = modelPart;
        useModel.getAllParts().forEach(ModelPart::resetPose);

        poseStack.pushPose();
        MultiBufferSource.BufferSource bufferIn = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderTypes.entityTranslucentCullItemTarget(TryModel.greenhouse_core_container.sprite()   .withPrefix("textures/").withSuffix(".png")));
        poseStack.translate(0, 0.5, 0);
        useModel.x += 1;
        useModel.render(poseStack, vertexconsumer2, state.lightCoords, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }


}

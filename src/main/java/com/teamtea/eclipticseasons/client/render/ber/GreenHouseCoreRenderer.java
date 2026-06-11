package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.client.model.entity.GreenHouseCoreModel;
import com.teamtea.eclipticseasons.client.model.entity.TryKeyframe;
import com.teamtea.eclipticseasons.client.model.entity.TryModel;
import com.teamtea.eclipticseasons.client.render.ber.state.GreenHouseCoreState;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;


public class GreenHouseCoreRenderer implements BlockEntityRenderer<GreenHouseCoreBlockEntity, GreenHouseCoreState> {

    private ModelPart modelPart;
    private ModelPart modelPart_Age1;

    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public GreenHouseCoreRenderer(BlockEntityRendererProvider.Context pContext) {
        modelPart = TryModel.createBodyLayer().bakeRoot().getChild("All");
        modelPart_Age1 = TryModel.createBodyLayer_age1().bakeRoot().getChild("All");
    }


    @Override
    public GreenHouseCoreState createRenderState() {
        return new GreenHouseCoreState();
    }

    @Override
    public void extractRenderState(GreenHouseCoreBlockEntity blockEntity, GreenHouseCoreState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.setStage(blockEntity.getBlockState().getValue(GreenHouseCoreBlock.AGE));
        long seed = blockEntity.getBlockState().getSeed(blockEntity.getBlockPos());
        long time = (blockEntity.getLevel().getGameTime() + Math.abs(seed)) % 50L;
        long renderTicks = (long) ((time + partialTicks) * 40);
        state.setRenderTicks(renderTicks * 3000 / 2000);
        state.setBlock((GreenHouseCoreBlock) blockEntity.getBlockState().getBlock());
    }

    @Override
    public void submit(GreenHouseCoreState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        int stage = state.getStage();

        ModelPart useModel = stage == 1 ? modelPart_Age1 : modelPart;
        float size = 1f;


        poseStack.pushPose();

        useModel.getAllParts().forEach(ModelPart::resetPose);


        GreenHouseCoreBlock block = state.getBlock();
        int combinedLight = TryModel.getLightFromBlock(block, stage);

        // MultiBufferSource.BufferSource bufferIn = Minecraft.getInstance().renderBuffers().crumblingBufferSource();
        Identifier identifier = TryModel.getMaterialFromBlock(block, stage).sprite()
                .withPrefix("textures/")
                .withSuffix(".png");
        // VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderTypes.itemTranslucent(identifier));

        TryKeyframe.animate(useModel, TryModel.animation, state.getRenderTicks(), size, ANIMATION_VECTOR_CACHE);
        poseStack.translate(0, 0.5, 0);
        useModel.x += 1;


        submitNodeCollector.submitModelPart(useModel, poseStack, RenderTypes.itemTranslucent(identifier), combinedLight, OverlayTexture.NO_OVERLAY, null);
        // submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.itemTranslucent(identifier),
        //         (pose, buffer) ->
        //                 useModel.render(poseStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY));
        // GreenHouseCoreModel greenHouseCoreModel = new GreenHouseCoreModel(useModel, identifier);
        // greenHouseCoreModel.setupAnim(state);
        // submitNodeCollector.submitModel(greenHouseCoreModel,state,poseStack,identifier, LightCoordsUtil.block(15),OverlayTexture.NO_OVERLAY,-1,null);

        poseStack.popPose();
    }


}

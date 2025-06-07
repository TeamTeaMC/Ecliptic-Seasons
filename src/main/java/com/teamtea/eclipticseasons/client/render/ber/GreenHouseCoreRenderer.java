package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.client.render.TryKeyframe;
import com.teamtea.eclipticseasons.client.render.TryModel;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.WeatheringCopperGrateBlock;
import org.joml.Vector3f;


public class GreenHouseCoreRenderer implements BlockEntityRenderer<GreenHouseCoreBlockEntity> {

    private ModelPart modelPart;

    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public GreenHouseCoreRenderer(BlockEntityRendererProvider.Context pContext) {
        modelPart = TryModel.createBodyLayer().bakeRoot().getChild("All");
    }


    @Override
    public void render(GreenHouseCoreBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {

        ModelPart useModel = modelPart;
        float size = 1f;


        poseStack.pushPose();

        useModel.getAllParts().forEach(ModelPart::resetPose);

        long seed = blockEntity.getBlockState().getSeed(blockEntity.getBlockPos());
        long time = (blockEntity.getLevel().getGameTime() + Math.abs(seed)) % 50L;
        //
        long renderTicks = (long) ((time + partialTicks) * 40);

        renderTicks = renderTicks * 3000 / 2000;
        GreenHouseCoreBlock block = (GreenHouseCoreBlock) blockEntity.getBlockState().getBlock();
        // Integer value = blockEntity.getBlockState().getValue(GreenHouseCoreBlock.POWER);
        // combinedLight = value>0?LightTexture.pack(value,15):TryModel.getLightFromBlock(block);
        combinedLight = TryModel.getLightFromBlock(block);
        VertexConsumer vertexconsumer2 = TryModel.getMaterialFromBlock(block).buffer(bufferIn, RenderType::itemEntityTranslucentCull);
        TryKeyframe.animate(useModel, TryModel.animation, renderTicks, size, ANIMATION_VECTOR_CACHE);
        poseStack.translate(0, 0.5, 0);
        useModel.x+=1;
        useModel.render(poseStack, vertexconsumer2, combinedLight, combinedOverlay);


        poseStack.popPose();
    }
}

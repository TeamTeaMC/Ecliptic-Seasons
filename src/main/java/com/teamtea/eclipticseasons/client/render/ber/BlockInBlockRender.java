package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BlockInBlockRender implements BlockEntityRenderer<BlockInCopperGrateBlockEntity> {
    public BlockInBlockRender(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(BlockInCopperGrateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.125f,.125f,.125f);
        poseStack.scale(.75f,.75f,.75f);
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(blockEntity.getInnerBlock().defaultBlockState(),poseStack,bufferSource,packedLight,packedOverlay);

        poseStack.popPose();
           }
}

package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class BlockInBlockRender implements BlockEntityRenderer<BlockInCopperGrateBlockEntity> {
    public BlockInBlockRender(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockInCopperGrateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.125f, .125f, .125f);
        poseStack.scale(.75f, .75f, .75f);
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(blockEntity.getInnerBlock().defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        // poseStack.pushPose();
        //
        // poseStack.translate(.375f, 1.375f, .5f);
        //
        // poseStack.scale(.75f, .75f, 5f);
        // // Minecraft.getInstance().getItemRenderer()
        // //         .renderStatic(
        // //                 ItemRegistry.hyetometer.get().getDefaultInstance(),
        // //                 ItemDisplayContext.FIXED,packedLight,packedOverlay,poseStack,bufferSource,blockEntity.getLevel(),0);
        // int i = blockEntity.getBlockPos().getX() % 3;
        // Minecraft.getInstance().getItemRenderer()
        //         .renderStatic(Minecraft.getInstance().player,
        //                 (i == 0 ? ItemRegistry.hyetometer : i == 1 ? ItemRegistry.hygrometer : ItemRegistry.thermometer)
        //                         .get().getDefaultInstance(),
        //                 ItemDisplayContext.FIXED, false, poseStack, bufferSource, blockEntity.getLevel(), packedLight, packedOverlay, 0);
        // poseStack.popPose();

    }
}

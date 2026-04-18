package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.client.core.AttachModelManager;
import com.teamtea.eclipticseasons.client.render.ber.state.BlockContainerState;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class BlockInBlockRender implements BlockEntityRenderer<@NonNull BlockInCopperGrateBlockEntity, @NonNull BlockContainerState> {
    public BlockInBlockRender(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public BlockContainerState createRenderState() {
        return new BlockContainerState();
    }

    @Override
    public void extractRenderState(@NonNull BlockInCopperGrateBlockEntity blockEntity, @NonNull BlockContainerState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.innerBlock = blockEntity.getInnerBlock();
    }


    @Override
    public void submit(BlockContainerState state, PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.125f, .125f, .125f);
        poseStack.scale(.75f, .75f, .75f);
        // Minecraft.getInstance().getBlockRenderer()
        //        .renderSingleBlock(state.getInnerBlock().defaultBlockState(), poseStack, submitNodeCollector, state.lightCoords, packedOverlay);
        BlockStateModel blockStateModel = AttachModelManager.models.blockStateModels()
                .get(state.getInnerBlock().defaultBlockState());
        if (blockStateModel != null) {
            ArrayList<BlockStateModelPart> objects = new ArrayList<>();
            blockStateModel.collectParts(Minecraft.getInstance().level.getRandom(), objects);
            submitNodeCollector.submitBlockModel(poseStack,
                    RenderTypes.solidMovingBlock(),
                    objects, new int[0], state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }


}

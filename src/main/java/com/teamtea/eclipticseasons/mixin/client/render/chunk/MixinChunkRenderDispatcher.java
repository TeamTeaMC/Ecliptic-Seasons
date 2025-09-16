package com.teamtea.eclipticseasons.mixin.client.render.chunk;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.vertex.*;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Set;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class MixinChunkRenderDispatcher {

    @ModifyExpressionValue(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSeed(Lnet/minecraft/core/BlockPos;)J")
    )
    private long eclipticseasons$compile_findModel(
            long original,
            @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack,
            @Local(ordinal = 2) BlockPos blockpos2,
            @Local(ordinal = 1) BlockState blockstate,
            @Local PoseStack posestack,
            @Local RenderChunkRegion renderchunkregion,
            @Local RandomSource randomsource,
            @Local Set<RenderType> renderTypeSet,
            @Local BakedModel bakedModel,
            @Local ModelData modelData
    ) {
        randomsource.setSeed(original);
        BakedModel model = ExtraModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource, original,
                renderchunkregion instanceof IExtendBlockView view ? view.getModelCheckPos() : null);
        IExtraRendererContextOwner.of(renderchunkregion)
                .setModelData(modelData)
                .setOriginalModel(bakedModel)
                .setExtraModel(model)
                .setReplace(model != null
                        && ExtraModelManager.isModelReplaceable(blockstate, renderchunkregion, blockpos2, model));
        return original;
    }

    @ModifyExpressionValue(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    ordinal = 1,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$compile_extraSnowyModel23(
            boolean original,
            @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack,
            @Local(ordinal = 2) BlockPos blockpos2,
            @Local(ordinal = 1) BlockState blockstate,
            @Local PoseStack posestack,
            @Local RenderChunkRegion renderchunkregion,
            @Local RandomSource randomsource,
            @Local Set<RenderType> renderTypeSet
    ) {

        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(renderchunkregion);

        BakedModel snowModel = null;
        if (rendererHolder.isReplace()) {
            original = false;
        }

        if (!original) {
            snowModel = rendererHolder.getExtraModel();
            // snowModelRef.set(null);
        }

        if (snowModel != null) {
            original = false;
            eclipticseasons$renderModel(snowModel, pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, renderTypeSet);
        }

        if (!original) {
            IExtraRendererContextOwner.of(renderchunkregion).resetAll();
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z")
    )
    private boolean eclipticseasons$renderFrozenWaterIce(
            boolean original,
            @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack,
            @Local(ordinal = 2) BlockPos blockpos2,
            @Local(ordinal = 1) BlockState blockstate,
            @Local PoseStack posestack,
            @Local RenderChunkRegion renderchunkregion,
            @Local RandomSource randomsource,
            @Local Set<RenderType> renderTypeSet,
            @Local FluidState fluidState
    ) {

        if (!original
                && !IceKeeper.notFrozen(renderchunkregion, blockpos2, blockstate, fluidState)) {
            eclipticseasons$renderModel(IceKeeper.getIceModel(blockstate, fluidState), pChunkBufferBuilderPack, blockpos2, IceKeeper.getFakeState(blockstate, fluidState), posestack, renderchunkregion, randomsource, renderTypeSet);
        }
        return original;
    }

    @Unique
    private static void eclipticseasons$renderModel(BakedModel bakedModel, ChunkBufferBuilderPack pChunkBufferBuilderPack, BlockPos pos, BlockState state, PoseStack posestack, RenderChunkRegion renderchunkregion, RandomSource random, Set<RenderType> renderTypeSet) {
        RenderType renderType = ExtraModelManager.getRenderType(state);
        BufferBuilder bufferbuilder2 = pChunkBufferBuilderPack.builder(renderType);
        // this$0.beginLayer(bufferbuilder2);
        if (renderTypeSet.add(renderType))
            bufferbuilder2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        long seed = state.getSeed(pos);
        posestack.pushPose();
        posestack.translate((float) (pos.getX() & 15), (float) (pos.getY() & 15), (float) (pos.getZ() & 15));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .tesselateBlock(renderchunkregion,
                        bakedModel,
                        state,
                        pos,
                        posestack,
                        bufferbuilder2,
                        true,
                        random,
                        seed,
                        OverlayTexture.NO_OVERLAY,
                        IExtraRendererContextOwner.of(renderchunkregion).getModelData(),
                        renderType);
        posestack.popPose();
    }
}

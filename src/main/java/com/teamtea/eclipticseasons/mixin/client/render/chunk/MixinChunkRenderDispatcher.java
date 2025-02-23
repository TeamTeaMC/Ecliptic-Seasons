package com.teamtea.eclipticseasons.mixin.client.render.chunk;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class MixinChunkRenderDispatcher {


    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "compile",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;")
    // )
    // private ChunkRenderTypeSet eclipticseasons$compile_extraSnowyModel2(
    //         ChunkRenderTypeSet original,
    //         @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack,
    //         @Local(ordinal = 2) BlockPos blockpos2,
    //         @Local(ordinal = 1) BlockState blockstate,
    //         @Local PoseStack posestack,
    //         @Local RenderChunkRegion renderchunkregion,
    //         @Local RandomSource randomsource,
    //         @Local Set<RenderType> renderTypeSet
    // ) {
    //
    //     // boolean replace = ModelManager.appendModel(pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, renderTypeSet);
    //     //
    //     // return replace ? ChunkRenderTypeSet.none() : original;
    //     //
    //     BakedModel snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource);
    //     if (snowModel != null) if (!original.contains(RenderType.cutoutMipped())) {
    //         return ChunkRenderTypeSet.union(original, ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
    //
    //     }
    //     return original;
    // }


    @ModifyExpressionValue(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    ordinal = 1,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$compile_extraSnowyModel23(
            boolean original,  @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack, @Local(ordinal = 2) BlockPos blockpos2, @Local(ordinal = 1) BlockState blockstate, @Local PoseStack posestack, @Local RenderChunkRegion renderchunkregion, @Local RandomSource randomsource, @Local Set<RenderType> renderTypeSet
    ) {

        BakedModel snowModel = null;
        if (!original) {
            snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource);
        } else {
            if (ModelManager.isModelReplaced(blockstate)) {
                snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource);
            }
        }

        if (snowModel != null) {
            original = false;
            eclipticseasons$renderModel(snowModel,pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, renderTypeSet);
        }

        return original;
    }


    @Unique
    private static void eclipticseasons$renderModel(BakedModel bakedModel, ChunkBufferBuilderPack pChunkBufferBuilderPack, BlockPos pos, BlockState state, PoseStack posestack, RenderChunkRegion renderchunkregion, RandomSource random, Set<RenderType> renderTypeSet) {
        RenderType renderType =  ModelManager.getRenderType(state);
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
                        ModelData.EMPTY,
                        renderType);
        posestack.popPose();
    }
}

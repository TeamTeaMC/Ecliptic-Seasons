package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.*;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.render.SnowRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(SectionCompiler.class)
public abstract class MixinChunkRenderDispatcher {

    @Shadow
    private BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> pBufferLayers, SectionBufferBuilderPack pSectionBufferBuilderPack, RenderType pRenderType) {
        BufferBuilder bufferbuilder = pBufferLayers.get(pRenderType);
        if (bufferbuilder == null) {
            ByteBufferBuilder bytebufferbuilder = pSectionBufferBuilderPack.buffer(pRenderType);
            bufferbuilder = new BufferBuilder(bytebufferbuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            pBufferLayers.put(pRenderType, bufferbuilder);
        }
        return bufferbuilder;
    }

    @ModifyExpressionValue(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    ordinal = 1,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$compile_extraSnowyModel23(
            boolean original,
            @Local(argsOnly = true) SectionBufferBuilderPack pChunkBufferBuilderPack,
            @Local(ordinal = 2) BlockPos blockpos2,
            @Local BlockState blockstate,
            @Local PoseStack posestack,
            @Local(argsOnly = true) RenderChunkRegion renderchunkregion,
            @Local RandomSource randomsource,
            @Local Map<RenderType, BufferBuilder> renderTypeBufferBuilderMap
    ) {

        BakedModel snowModel=null;
        if (!original) {
            snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource);
        } else {
            if (ModelManager.isModelReplaced(blockstate)) {
                snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource);
            }
        }
        if (snowModel != null) {
            original = false;
            eclipticSeasons$renderModel(snowModel, pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, renderTypeBufferBuilderMap);
        }

        return original;
    }


    @Unique
    private void eclipticSeasons$renderModel(BakedModel bakedModel, SectionBufferBuilderPack pChunkBufferBuilderPack, BlockPos pos, BlockState state, PoseStack posestack, RenderChunkRegion renderchunkregion, RandomSource random, Map<RenderType, BufferBuilder> renderTypeSet) {
        RenderType renderType = ModelManager.getRenderType();
        BufferBuilder bufferbuilder2 = getOrBeginLayer(renderTypeSet, pChunkBufferBuilderPack, renderType);
        SnowRenderer.renderSnowyBlock(bakedModel, bufferbuilder2, pos, state, posestack, renderchunkregion, random, renderType);
    }


}

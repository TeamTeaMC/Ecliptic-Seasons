package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.*;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISeedProvider;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.render.SnowRenderer;
import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(SectionCompiler.class)
public abstract class MixinChunkRenderDispatcher {

    @Shadow
    private BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> pBufferLayers, SectionBufferBuilderPack pSectionBufferBuilderPack, RenderType pRenderType) {
        // BufferBuilder bufferbuilder = pBufferLayers.get(pRenderType);
        // if (bufferbuilder == null) {
        //     ByteBufferBuilder bytebufferbuilder = pSectionBufferBuilderPack.buffer(pRenderType);
        //     bufferbuilder = new BufferBuilder(bytebufferbuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        //     pBufferLayers.put(pRenderType, bufferbuilder);
        // }
        throw new UnsupportedOperationException();
    }

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "HEAD"
            )
    )
    private void eclipticseasons$compile_init_chunk(SectionPos sectionPos, RenderChunkRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        ((IMapSlice) region).forceMapSliceUpdate();
    }

    @ModifyExpressionValue(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSeed(Lnet/minecraft/core/BlockPos;)J")
    )
    private long eclipticseasons$compile_cache_getSeeds(long original,
                                                        @Local(ordinal = 2) BlockPos blockpos2,
                                                        @Local BlockState blockstate,
                                                        @Local RandomSource randomsource,
                                                        @Local(argsOnly = true) RenderChunkRegion renderChunkRegion) {
        ((ISeedProvider) renderChunkRegion).setCacheSeed(original);
        if (renderChunkRegion instanceof ExtendBlockView extendBlockView) {
            randomsource.setSeed(original);
            extendBlockView.setSnowModel(ExtraModelManager.findModel(renderChunkRegion, blockpos2, blockstate, randomsource, original, extendBlockView.getModelCheckPos()));
            if (extendBlockView.getSnowModel() != null) {
                extendBlockView.setCurrentModelReplaceable(ExtraModelManager.isModelReplaceable(blockstate, renderChunkRegion, blockpos2, extendBlockView.getSnowModel()));
                extendBlockView.setShouldCollectBakeQuads(ExtraModelManager.isSpecialCTMBlock(blockstate));
            } else {
                extendBlockView.setCurrentModelReplaceable(false);
                extendBlockView.setShouldCollectBakeQuads(false);
            }
        }

        return original;
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

        BakedModel snowModel = null;
        // if (!original) {
        //     snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource, ((ISeedProvider) renderchunkregion).getCacheSeed());
        //     eclipticseasons$countModel++;
        // } else {
        //     // if (ModelManager.isModelReplaceable(blockstate))
        //     if (ModelManager.isModelReplaceable(((IBlockStateFlagger) blockstate).getBlockTypeFlag(renderchunkregion, blockpos2))) {
        //         snowModel = ModelManager.findModel(renderchunkregion, blockpos2, blockstate, randomsource, ((ISeedProvider) renderchunkregion).getCacheSeed());
        //         eclipticseasons$countModel++;
        //     }
        // }
        if (renderchunkregion instanceof ExtendBlockView extendBlockView) {
            snowModel = extendBlockView.getSnowModel();
            if (snowModel != null && extendBlockView.isCurrentModelReplaceable()) {
                original = false;
            }
        }

        if (!original && snowModel != null) {
            original = false;
            ((ExtendBlockView) renderchunkregion).setShouldCollectBakeQuads(false);
            eclipticseasons$renderModel(snowModel, pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, ((ISeedProvider) renderchunkregion).getCacheSeed(), renderTypeBufferBuilderMap);
            ((ExtendBlockView) renderchunkregion).cleanAfterRender();

            eclipticseasons$countModel++;
        }

        return original;
    }


    @Unique
    private void eclipticseasons$renderModel(BakedModel bakedModel, SectionBufferBuilderPack pChunkBufferBuilderPack, BlockPos pos, BlockState state, PoseStack posestack, RenderChunkRegion renderchunkregion, RandomSource random, long seed, Map<RenderType, BufferBuilder> renderTypeSet) {
        RenderType renderType = ExtraModelManager.getRenderType(state);
        BufferBuilder bufferbuilder2 = getOrBeginLayer(renderTypeSet, pChunkBufferBuilderPack, renderType);
        SnowRenderer.renderSnowyBlock(bakedModel, bufferbuilder2, pos, state, posestack, renderchunkregion, random, seed, renderType);
    }


    // time record


    @Unique
    private long eclipticseasons$time = 0;
    @Unique
    private long eclipticseasons$countModel = 0;

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$compile_checkb(SectionPos pSectionPos, RenderChunkRegion pRegion, VertexSorting pVertexSorting, SectionBufferBuilderPack pSectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        ((ExtendBlockView) pRegion).finishChunkRender();
        long l = System.currentTimeMillis() - eclipticseasons$time;
        if (l > ClientConfig.Debug.minChunkCompileWaringTime.getAsInt())
            EclipticSeasons.logger("WARNING",
                    Thread.currentThread().toString(),
                    pSectionPos,
                    "Rebuild time: " + l,
                    "Model check count: " + eclipticseasons$countModel);
    }

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$compile_check(SectionPos pSectionPos, RenderChunkRegion pRegion, VertexSorting pVertexSorting, SectionBufferBuilderPack pSectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        eclipticseasons$time = System.currentTimeMillis();
        eclipticseasons$countModel = 0;

        ((ExtendBlockView) pRegion).startChunkRender();
    }
}

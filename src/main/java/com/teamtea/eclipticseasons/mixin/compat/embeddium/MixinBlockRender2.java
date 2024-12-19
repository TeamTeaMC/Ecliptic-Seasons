package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRender2 {


    @Shadow(remap = false) @Final private RandomSource random;
    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getGeometry",
    //         at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> ecliptic$tesselateWithAO_getQuads(List<BakedQuad> original, @Local(ordinal = 0) BlockRenderContext ctx, @Local(ordinal = 0)Direction face) {
    //     return ModelManager.appendOverlay(ctx.world(),ctx.state(),ctx.pos(),face,random,ctx.seed(),original);
    // }

    @ModifyExpressionValue(
            remap = false,
            method = "getGeometry",
            at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> ecliptic$getGeometry_getQuads(List<BakedQuad> original, @Local(ordinal = 0) BlockRenderContext ctx, @Local(ordinal = 0)Direction face) {
        return ModelManager.cancelTop(ctx.model(),ctx.world(),ctx.state(),ctx.pos(),face,random,ctx.seed(),original,ModelManager.EMPTY_BAKED_QUAD_LIST, null);
    }

    // @WrapOperation(
    //         remap = false,
    //         method = "renderModel",
    //         at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;isFaceVisible(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lnet/minecraft/core/Direction;)Z")
    // )
    // private boolean mixin$renderModel_isFaceVisible(BlockRenderer blockRenderer, BlockRenderContext ctx, Direction face, Operation<Boolean> original) {
    //     return ModelManager.shouldisFaceVisible(blockRenderer,ctx,face,original);
    // }
}

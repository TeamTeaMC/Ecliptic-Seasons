package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRender2 {


    @Shadow(remap = false)
    @Final
    private RandomSource random;
    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getGeometry",
    //         at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads(List<BakedQuad> original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx, @Local(ordinal = 0, argsOnly = true) Direction face) {
    //   return   ModelManager.appendOverlay(ctx.world(), ctx.state(), ctx.pos(), face, random, ctx.seed(), original,ctx.model(),ctx.renderLayer());
    // }


    @ModifyExpressionValue(
            remap = false,
            method = "getGeometry",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$getGeometry_getQuads(List<BakedQuad> original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx, @Local(ordinal = 0, argsOnly = true) Direction face) {
        return ModelManager.cancelTop(ctx.model(), ctx.world(), ctx.state(), ctx.pos(), face, random, ctx.seed(), original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/render/frapi/FRAPIModelUtils;isFRAPIModel(Lnet/minecraft/client/resources/model/BakedModel;)Z")
    )
    private boolean eclipticseasons$renderModel_isFRAPIModel(boolean original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx) {
        // TODO:这里解决一下如果仅仅引入连接器，不使用连接纹理的话
        return (ctx.model() instanceof SnowyBakedModelWrapper<?> && ((FabricModelDelayChecker) ctx).isLastFabric()) || original;
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

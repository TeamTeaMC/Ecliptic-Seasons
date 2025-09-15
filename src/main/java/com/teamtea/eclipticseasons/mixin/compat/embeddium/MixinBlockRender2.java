package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;

import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRender2 {


    @Shadow(remap = false) @Final private RandomSource random;


    @ModifyExpressionValue(
            remap = false,
            method = "getGeometry",
            at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$getGeometry_getQuads(List<BakedQuad> original, @Local(ordinal = 0) BlockRenderContext ctx, @Local(ordinal = 0)Direction face) {
        return ExtraModelManager.cancelTop(ctx.model(),ctx.world(),ctx.state(),ctx.pos(),face,random,ctx.seed(),original, ExtraModelManager.EMPTY_BAKED_QUAD_LIST);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/render/frapi/FRAPIModelUtils;isFRAPIModel(Lnet/minecraft/client/resources/model/BakedModel;)Z")
    )
    private boolean eclipticseasons$renderModel_isFRAPIModel(boolean original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx) {
        // TODO:这里解决一下如果仅仅引入连接器，不使用连接纹理的话
        return (
                FabricModelDelayChecker.asFabricMode(ctx.world())
        ) || original;
    }

    @ModifyExpressionValue(
            remap = false,
            method = "getLightingMode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;useAmbientOcclusion(Lnet/minecraft/world/level/block/state/BlockState;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Lnet/neoforged/neoforge/common/util/TriState;")
    )
    private TriState eclipticseasons$getLightingMode_useAmbientOcclusion(TriState original, @Local(ordinal = 0) BlockRenderContext context, @Local(ordinal = 0) BlockState state) {
        TriState modelForAmbientOcclusion = ExtraRendererContext.getModelForAmbientOcclusion(context.world(), state, context.modelData(),context.renderLayer());
        if (modelForAmbientOcclusion != null) return TriState.TRUE;
        return original;
    }
}

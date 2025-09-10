package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
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

    @ModifyExpressionValue(
            remap = false,
            method = "getGeometry",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$getGeometry_getQuads(List<BakedQuad> original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx, @Local(ordinal = 0, argsOnly = true) Direction face) {
        return ExtraModelManager.cancelTop(ctx.model(), ctx.world(), ctx.state(), ctx.pos(), face, random, ctx.seed(), original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/render/frapi/FRAPIModelUtils;isFRAPIModel(Lnet/minecraft/client/resources/model/BakedModel;)Z")
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;useAmbientOcclusion(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z")
    )
    private boolean eclipticseasons$getLightingMode_useAmbientOcclusion(boolean original, @Local(ordinal = 0, argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0, argsOnly = true) BlockState state, @Local(ordinal = 0, argsOnly = true) RenderType renderType) {
        Boolean modelForAmbientOcclusion = ExtraRendererContext.getModelForAmbientOcclusion(blockAndTintGetter, state, renderType);
        if (modelForAmbientOcclusion != null) return modelForAmbientOcclusion;
        return original;
    }

}

package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.client.IESRendererHolder;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.IESReplaceModel;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.embeddedt.embeddium.render.frapi.FRAPIModelUtils;
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

    @WrapOperation(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;getLightingMode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/RenderType;)Lme/jellysquid/mods/sodium/client/model/light/LightMode;")
    )
    private LightMode eclipticseasons$renderModel_getLightingMode(BlockRenderer instance, BlockState state, BakedModel model, BlockAndTintGetter world, BlockPos pos, RenderType renderLayer, Operation<LightMode> original, @Local(ordinal = 0, argsOnly = true) BlockRenderContext ctx) {
        return original.call(instance, state,
                IESRendererHolder.getOriginalModel(ctx.world(), model)
                , world, pos, renderLayer);
    }

}

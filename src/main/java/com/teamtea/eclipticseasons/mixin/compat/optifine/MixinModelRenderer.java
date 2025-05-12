package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.model.MulBakeModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.render.RenderEnv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin({ModelBlockRenderer.class})
public abstract class MixinModelRenderer {

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelSmooth",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/optifine/model/BlockModelCustomizer;getRenderQuads(Ljava/util/List;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/RenderType;JLnet/optifine/render/RenderEnv;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$renderModelSmooth_getQuads(List<BakedQuad> original, @Local(ordinal = 0) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Direction direction, @Local(ordinal = 0) RandomSource randomSource, @Local(ordinal = 0) long seed,
                                                                       @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) RenderType renderType, @Local RenderEnv renderEnv) {
        return eclipticseasons$addAfterOptOverlays(original, blockAndTintGetter, pos, state, direction, randomSource, seed, bakedModel, renderType, renderEnv);
    }


    @ModifyExpressionValue(
            remap = false,
            method = "renderModelSmooth",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/optifine/model/BlockModelCustomizer;getRenderQuads(Ljava/util/List;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/RenderType;JLnet/optifine/render/RenderEnv;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$renderModelSmooth_getQuads2(List<BakedQuad> original, @Local(ordinal = 0) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) RandomSource randomSource, @Local(ordinal = 0) long seed,
                                                                        @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) RenderType renderType, @Local RenderEnv renderEnv) {
        return eclipticseasons$addAfterOptOverlays(original, blockAndTintGetter, pos, state, null, randomSource, seed, bakedModel, renderType, renderEnv);
    }

    @ModifyExpressionValue(
            remap = false,
            method = {"renderModelSmooth", "renderModelFlat"},
            at = @At(value = "INVOKE", ordinal = 1, target = "Ljava/util/List;isEmpty()Z")
    )
    private boolean eclipticseasons$renderModel_skipEmpty(boolean original, @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) RenderType renderType) {
        if (eclipticseasons$checkModelAndRenderType(bakedModel, renderType)) {
            original = false;
        }
        return original;
    }


    @ModifyExpressionValue(
            remap = false,
            method = "renderModelFlat",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/optifine/model/BlockModelCustomizer;getRenderQuads(Ljava/util/List;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/RenderType;JLnet/optifine/render/RenderEnv;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads(List<BakedQuad> original, @Local(ordinal = 0) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Direction direction, @Local(ordinal = 0) RandomSource randomSource, @Local(ordinal = 0) long seed,
                                                                        @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) RenderType renderType, @Local RenderEnv renderEnv) {
        return eclipticseasons$addAfterOptOverlays(original, blockAndTintGetter, pos, state, direction, randomSource, seed, bakedModel, renderType, renderEnv);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelFlat",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/optifine/model/BlockModelCustomizer;getRenderQuads(Ljava/util/List;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/RenderType;JLnet/optifine/render/RenderEnv;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads2(List<BakedQuad> original, @Local(ordinal = 0) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) RandomSource randomSource, @Local(ordinal = 0) long seed,
                                                                         @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) RenderType renderType, @Local RenderEnv renderEnv) {
        return eclipticseasons$addAfterOptOverlays(original, blockAndTintGetter, pos, state, null, randomSource, seed, bakedModel, renderType, renderEnv);
    }


    @Unique
    private static boolean eclipticseasons$checkModelAndRenderType(BakedModel bakedModel, RenderType renderType) {
        return bakedModel instanceof MulBakeModel<?> mulBakeModel
                && mulBakeModel.isRenderTypeUse(renderType);
    }

    @Unique
    private static List<BakedQuad> eclipticseasons$addAfterOptOverlays(List<BakedQuad> original, BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, Direction direction, RandomSource randomSource, long seed, BakedModel bakedModel, RenderType renderType, RenderEnv renderEnv) {
        if (bakedModel instanceof MulBakeModel<?> mulBakeModel
                && mulBakeModel.isRenderTypeUse(renderType)) {
            List<BakedQuad> mulBakeModelQuads = mulBakeModel.getQuads(state, direction, randomSource, MulBakeModel.ES_DATA, renderType);

            // List<BakedQuad> newOut = new ArrayList<>(original.size() * 2);
            // newOut.addAll(original);
            // newOut.addAll(mulBakeModelQuads);
            // original = newOut;

            // or
            // 由于Optfine会加检查，所以我们无法手动启用
            if (mulBakeModel.isSnowy() && renderEnv.isOverlaysRendered()) {
                ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(renderType);
                BlockState blockState = Blocks.SNOW_BLOCK.defaultBlockState();
                for (BakedQuad bakedQuad : mulBakeModelQuads) {
                    listQuadsOverlay.addQuad(bakedQuad, blockState);
                }
                // renderEnv.setOverlaysRendered(true);
            } else {
                List<BakedQuad> newOut = new ArrayList<>(original.size() * 2);
                newOut.addAll(original);
                newOut.addAll(mulBakeModelQuads);
                original = newOut;
            }

            // original=newOut;
        }
        return original;
    }
}

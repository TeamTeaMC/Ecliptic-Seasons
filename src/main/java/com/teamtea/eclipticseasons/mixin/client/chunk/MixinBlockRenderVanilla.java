package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.List;

@Mixin({ModelBlockRenderer.class})
public abstract class MixinBlockRenderVanilla {


    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
    //         at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads(List<BakedQuad> original,@Local(argsOnly = true)BlockAndTintGetter blockAndTintGetter,@Local(argsOnly = true)BlockPos pos,@Local(argsOnly = true)BlockState state, @Local(argsOnly = true)Direction direction, @Local(argsOnly = true)RandomSource randomSource, @Local(argsOnly = true) long seed) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    // }
    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
    //         at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads2(List<BakedQuad> original,@Local(argsOnly = true)BlockAndTintGetter blockAndTintGetter,@Local(argsOnly = true)BlockPos pos,@Local(argsOnly = true)BlockState state, @Local(argsOnly = true)RandomSource randomSource, @Local(argsOnly = true) long seed) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    // }
    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
    //         at = @At(value = "INVOKE",  ordinal =0,  target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads(List<BakedQuad> original,@Local(argsOnly = true)BlockAndTintGetter blockAndTintGetter,@Local(argsOnly = true)BlockPos pos,@Local(argsOnly = true)BlockState state, @Local(argsOnly = true)Direction direction, @Local(argsOnly = true)RandomSource randomSource, @Local(argsOnly = true) long seed) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    // }
    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
    //         at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads2(List<BakedQuad> original,@Local(argsOnly = true)BlockAndTintGetter blockAndTintGetter,@Local(argsOnly = true)BlockPos pos,@Local(argsOnly = true)BlockState state, @Local(argsOnly = true)RandomSource randomSource, @Local(argsOnly = true) long seed) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    // }


    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads_cancel_top(List<BakedQuad> original, @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(ordinal = 0, argsOnly = true) BlockPos pos, @Local(ordinal = 0, argsOnly = true) BlockState state, @Local(ordinal = 0) Direction direction, @Local(argsOnly = true) RandomSource randomSource, @Local(argsOnly = true) long seed) {
        if (blockAndTintGetter instanceof ExtendBlockView extendBlockView) {
            if (extendBlockView.getSnowModel() != null)
                return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, direction, randomSource, seed, original, extendBlockView.getCacheBakeQuad(), extendBlockView.getSnowModel());
        }
        return original;
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads_cancel_top(List<BakedQuad> original, @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Local(ordinal = 0) Direction direction, @Local(argsOnly = true) RandomSource randomSource, @Local(argsOnly = true) long seed) {
        if (blockAndTintGetter instanceof ExtendBlockView extendBlockView) {
            if (extendBlockView.getSnowModel() != null)
                return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, direction, randomSource, seed, original, extendBlockView.getCacheBakeQuad(), extendBlockView.getSnowModel());
        }
        return original;
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads2(List<BakedQuad> original, @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) RandomSource randomSource, @Local(argsOnly = true) long seed) {
        if (blockAndTintGetter instanceof ExtendBlockView extendBlockView) {

            if (extendBlockView.getSnowModel() != null)
                return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, null, randomSource, seed, original, extendBlockView.getCacheBakeQuad(), extendBlockView.getSnowModel());
        }
        return original;
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads2(List<BakedQuad> original, @Local(argsOnly = true) BakedModel bakedModel, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) RandomSource randomSource, @Local(argsOnly = true) long seed) {
        if (blockAndTintGetter instanceof ExtendBlockView extendBlockView) {
            if (extendBlockView.getSnowModel() != null)
                return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, null, randomSource, seed, original, extendBlockView.getCacheBakeQuad(), extendBlockView.getSnowModel());
        }
        return original;
    }
}

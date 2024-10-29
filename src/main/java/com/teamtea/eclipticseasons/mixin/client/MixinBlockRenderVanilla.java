package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.BitSet;
import java.util.List;

@Mixin({ModelBlockRenderer.class})
public abstract class MixinBlockRenderVanilla {

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> ecliptic$tesselateWithAO_getQuads(List<BakedQuad> original,@Local(ordinal = 0)BlockAndTintGetter blockAndTintGetter,@Local(ordinal = 0)BlockPos pos,@Local(ordinal = 0)BlockState state, @Local(ordinal = 0)Direction direction, @Local(ordinal = 0)RandomSource randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> ecliptic$tesselateWithAO_getQuads2(List<BakedQuad> original,@Local(ordinal = 0)BlockAndTintGetter blockAndTintGetter,@Local(ordinal = 0)BlockPos pos,@Local(ordinal = 0)BlockState state, @Local(ordinal = 0)RandomSource randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> ecliptic$tesselateWithoutAO_getQuads(List<BakedQuad> original,@Local(ordinal = 0)BlockAndTintGetter blockAndTintGetter,@Local(ordinal = 0)BlockPos pos,@Local(ordinal = 0)BlockState state, @Local(ordinal = 0)Direction direction, @Local(ordinal = 0)RandomSource randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tesselateWithoutAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private List<BakedQuad> ecliptic$tesselateWithoutAO_getQuads2(List<BakedQuad> original,@Local(ordinal = 0)BlockAndTintGetter blockAndTintGetter,@Local(ordinal = 0)BlockPos pos,@Local(ordinal = 0)BlockState state, @Local(ordinal = 0)RandomSource randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    }
}

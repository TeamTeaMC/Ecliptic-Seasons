package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Mixin({BlockModelRenderer.class})
public abstract class MixinBlockRenderVanilla {

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelSmooth",
            at = @At(value = "INVOKE",
                    ordinal = 0,
                    target = "Lnet/minecraft/client/renderer/model/IBakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads(List<BakedQuad> original, @Local(ordinal = 0) IBlockDisplayReader blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Direction direction, @Local(ordinal = 0) Random randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelSmooth",
            at = @At(value = "INVOKE",  ordinal = 1,
                    target = "Lnet/minecraft/client/renderer/model/IBakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithAO_getQuads2(List<BakedQuad> original, @Local(ordinal = 0) IBlockDisplayReader blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Random randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelFlat",
            at = @At(value = "INVOKE",ordinal = 0,
                    target = "Lnet/minecraft/client/renderer/model/IBakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads(List<BakedQuad> original, @Local(ordinal = 0) IBlockDisplayReader blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Direction direction, @Local(ordinal = 0) Random randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "renderModelFlat",
            at = @At(value = "INVOKE",ordinal = 1,
                    target = "Lnet/minecraft/client/renderer/model/IBakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$tesselateWithoutAO_getQuads2(List<BakedQuad> original, @Local(ordinal = 0) IBlockDisplayReader blockAndTintGetter, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) Random randomSource, @Local(ordinal = 0) long seed) {
        return ModelManager.appendOverlay(blockAndTintGetter, state, pos, (Direction) null, randomSource, seed, original);
    }
}

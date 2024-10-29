package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class MixinChunkRenderDispatcher {

    //
    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "compile",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;")
    // )
    // private ChunkRenderTypeSet mixin_compile_extraSnowyModel2(
    //         ChunkRenderTypeSet original,
    //         @Local(argsOnly = true) ChunkBufferBuilderPack pChunkBufferBuilderPack,
    //         @Local(ordinal = 2) BlockPos blockpos2,
    //         @Local(ordinal = 1) BlockState blockstate,
    //         @Local PoseStack posestack,
    //         @Local RenderChunkRegion renderchunkregion,
    //         @Local RandomSource randomsource,
    //         @Local Set<RenderType> renderTypeSet
    // ) {
    //
    //     boolean replace = ModelManager.appendModel(pChunkBufferBuilderPack, blockpos2, blockstate, posestack, renderchunkregion, randomsource, renderTypeSet);
    //
    //     return replace ? ChunkRenderTypeSet.none() : original;
    // }
}

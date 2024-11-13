package com.teamtea.eclipticseasons.mixin.compat.iris_test;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({DefaultFluidRenderer.class})
public abstract class MixinDefaultFluidRenderer {


    @WrapOperation(
            remap = false,
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;isSideExposed(Lnet/minecraft/world/level/BlockAndTintGetter;IIILnet/minecraft/core/Direction;F)Z")
    )
    private boolean ecliptic$getQuads_getQuads(DefaultFluidRenderer instance, BlockAndTintGetter threshold, int i, int world, int x, Direction y, float z, Operation<Boolean> original, @Local BlockState state) {
        return true;
    }

    @WrapOperation(
            remap = false,
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;fluidHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)F")
    )
    private float ecliptic$getQuads_getQuads(DefaultFluidRenderer instance, BlockAndTintGetter blockAndTintGetter, Fluid world, BlockPos fluid, Direction blockPos, Operation<Float> original, @Local BlockState state) {
        return 0.1f;
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

package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.ExtraRenderDispatcher;
import com.teamtea.eclipticseasons.client.core.context.ExtraRendererContext;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.ESFabricRender;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.EmptyStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.Predicate;

@Mixin(AltModelBlockRendererImpl.class)
public abstract class MixinModelBlockRenderer {


    @Shadow
    @Final
    private RandomSource random;


    @Shadow
    @Final
    private Predicate<@Nullable Direction> cullTest;
    @Shadow
    @Final
    private boolean cull;
    @Unique
    ArrayList<BlockStateModelPart> eclipticseasons$parts = new ArrayList<>();

    @Inject(
            method = "tesselateBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;pushTransform(Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadTransform;)V")
    )
    private void eclipticseasons$tesselateBlock_before(
            QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed, CallbackInfo ci,
            @Local(argsOnly = true) LocalRef<BlockStateModel> modelLocalRef) {
        if (level instanceof IMapSlice mapSlice) {
            eclipticseasons$parts = eclipticseasons$parts == null ? new ArrayList<>() : eclipticseasons$parts;
            ExtraRendererContext context = IExtraRendererContextOwner.of(mapSlice);
            ExtraRenderDispatcher.findModel(context, mapSlice,
                            pos,
                            blockState,
                            this.random,
                            seed,
                            mapSlice.getModelCheckPos(),
                            eclipticseasons$parts)
                    .apply(level, pos, blockState, random, eclipticseasons$parts);
            random.setSeed(seed);
            if (context.isReplace() && context.shouldApply()) {
                modelLocalRef.set(EmptyStateModel.EMPTY);
            }
            context.resetAll();
        }
    }

    @Inject(
            method = "tesselateBlock",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;emitQuads(Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Ljava/util/function/Predicate;)V")
    )
    private void eclipticseasons$tesselateBlock_extra(
            QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed, CallbackInfo ci) {
        if (!eclipticseasons$parts.isEmpty()) {
            ESFabricRender.emitQuads(eclipticseasons$parts, output, level, pos, blockState, this.random, cull ? cullTest : _ -> false);
            eclipticseasons$parts.clear();
        }
    }


}

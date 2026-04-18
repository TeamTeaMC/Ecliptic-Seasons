package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.google.common.annotations.Beta;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.AttachModelManager;
import com.teamtea.eclipticseasons.client.core.AttachRenderDispatcher;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.compat.iris.IrisAttachSnowyShader;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Beta

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput> implements SodiumBoard {
    public MixinBlockRenderTask(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

    @Shadow
    @Final
    private ChunkRenderContext renderContext;
    @Unique
    private long eclipticseasons$time = 0;
    @Unique
    private long eclipticseasons$countModel = 0;

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$compile_checkb(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        long l = System.currentTimeMillis() - eclipticseasons$time;
        if (l > ClientConfig.Debug.minChunkCompileWarningTime.getAsInt())
            EclipticSeasons.logger("WARNING",
                    Thread.currentThread().toString(),
                    render.getPosition(),
                    render.getPosition().center(),
                    render.getOriginX(), render.getOriginY(), render.getOriginZ(),
                    "Rebuild time: " + l,
                    "Model check count: " + eclipticseasons$countModel);

        eclipticseasons$time = 0;
        eclipticseasons$countModel = 0;
        ((SodiumStatus) buildContext.cache.getBlockRenderer()).eclipticseasons$bindCounter(null);
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$compile_check(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        eclipticseasons$time = System.currentTimeMillis();
        eclipticseasons$countModel = 0;
        ((SodiumStatus) buildContext.cache.getBlockRenderer()).eclipticseasons$bindCounter(this);
    }

    @Override
    public void eclipticseasons$addCount() {
        eclipticseasons$countModel++;
    }


    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V")
    )
    private void eclipticseasons$renderFrozenWaterIce(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local(name = "fluidState") FluidState fluidState,
            @Local(name = "blockState") BlockState blockState,
            @Local(name = "blockPos") BlockPos.MutableBlockPos blockPos,
            @Local(name = "modelOffset") BlockPos.MutableBlockPos modelOffset) {
        if (IceKeeper.notFrozen(buildContext.cache.getWorldSlice(), blockPos, blockState, fluidState))
            return;
        var model = IceKeeper.getIceModel(blockState, fluidState);
        if (model != null) {
            buildContext.cache.getBlockRenderer().renderModel(model, IceKeeper.getFakeState(blockState, fluidState), blockPos, modelOffset);
        }
    }

    @Inject(
            remap = false,
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderCache;getBlockModels()Lnet/minecraft/client/renderer/block/BlockStateModelSet;")
    )
    private void eclipticseasons$renderSnowLayerIn_below(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local LocalRef<BlockState> stateLocalRef
    ) {
        var state = AttachRenderDispatcher.shouldBlockAsSnowyState(stateLocalRef.get(), buildContext.cache.getWorldSlice(), mutableBlockPos);
        if (state != stateLocalRef.get())
            stateLocalRef.set(state);
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private void eclipticseasons$renderSnowLayerIn(ChunkBuildContext buildContext,
                                                   CancellationToken cancellationToken,
                                                   CallbackInfoReturnable<ChunkBuildOutput> cir,
                                                   @Local BlockState blockState,
                                                   @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos,
                                                   @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset) {
        if (blockState.getRenderShape() == RenderShape.INVISIBLE) return;
        BlockStateModel bm = AttachRenderDispatcher.shouldRenderedWithSnowInside(buildContext.cache.getWorldSlice(), blockPos, blockState, null);
        if (bm != null) {
            if (buildContext.cache.getBlockRenderer() instanceof IrisAttachSnowyShader shader)
                shader.es$setSnowyBlockState(Blocks.SNOW.defaultBlockState());
            buildContext.cache.getBlockRenderer().renderModel(bm, Blocks.SNOW.defaultBlockState(), blockPos, modelOffset);
        }
        if (buildContext.cache.getBlockRenderer() instanceof SodiumStatus sodiumStatus) {
            int y = blockPos.getY();
            int layer = AttachRenderDispatcher.getLayer(buildContext.cache.getWorldSlice(), blockPos, blockState, null, blockState.getSeed(blockPos));
            if (layer > 0) {
                if (buildContext.cache.getBlockRenderer() instanceof IrisAttachSnowyShader shader)
                    shader.es$setSnowyBlockState(Blocks.SNOW.defaultBlockState());
                buildContext.cache.getBlockRenderer().renderModel(AttachModelManager.getSnowLayerModel(layer), Blocks.SNOW.defaultBlockState()
                        .setValue(SnowLayerBlock.LAYERS, layer), blockPos, modelOffset.offset(0, 1, 0));
                modelOffset.offset(0, -1, 0);
                blockPos.setY(y);
            }
        }
    }

}

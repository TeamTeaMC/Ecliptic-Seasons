package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;

import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
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

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput> implements SodiumBoard {

    @Shadow
    @Final
    private ChunkRenderContext renderContext;
    @Unique
    private long eclipticseasons$time = 0;
    @Unique
    private long eclipticseasons$countModel = 0;

    public MixinBlockRenderTask(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

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
    private void eclipticseasons$renderFrozenWaterIce(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
                                             @Local FluidState fluidState,
                                             @Local BlockState blockState,
                                             @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos,
                                             @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset) {

        if (IceKeeper.notFrozen(buildContext.cache.getWorldSlice(), blockPos, blockState, fluidState)) return;
        BakedModel model = IceKeeper.getIceModel(blockState, fluidState);
        if (model != null) {
            buildContext.cache.getBlockRenderer().renderModel(model, IceKeeper.getFakeState(blockState, fluidState), blockPos, modelOffset);
        }
    }

}

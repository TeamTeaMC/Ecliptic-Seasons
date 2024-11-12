package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.EclipticSeasons;

import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput> implements SodiumBoard {

    // @Unique
    // protected RandomSource eclipticSeasons$random = new SingleThreadedRandomSource(42L);
    //
    // @WrapOperation(
    //         remap = false,
    //         method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V")
    // )
    // private void eclipticseasons$execute(
    //         BlockRenderer instance,
    //         BakedModel blockModel,
    //         BlockState state,
    //         BlockPos mutableBlockPos,
    //         BlockPos modelOffset,
    //         Operation<Void> original,
    //         @Local LevelSlice levelSlice,
    //         @Local BlockRenderer blockRenderer
    // ) {
    //
    //     eclipticSeasons$random.setSeed(state.getSeed(mutableBlockPos));
    //     boolean keep = true;
    //     BakedModel snowModel;
    //     snowModel = ModelManager.findModel(levelSlice, mutableBlockPos, state, eclipticSeasons$random);
    //     if (ModelManager.isModelReplaced(state, snowModel)) {
    //         keep = false;
    //     }
    //     if(keep){
    //         original.call(instance, blockModel, state, mutableBlockPos, modelOffset);
    //     }
    //     if (snowModel != null) {
    //         blockRenderer.renderModel(snowModel, state, mutableBlockPos, modelOffset);
    //     }
    //
    // }
    @Unique
    private long eclipticSeasons$time = 0;
    @Unique
    private long eclipticSeasons$countModel = 0;

    public MixinBlockRenderTask(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$compile_checkb(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        long l = System.currentTimeMillis() - eclipticSeasons$time;
        if (l > 100)
            EclipticSeasons.logger("WARNING",
                    Thread.currentThread().toString(),
                    render.getPosition(),
                    render.getPosition().center(),
                    render.getOriginX(), render.getOriginY(), render.getOriginZ(),
                    "Rebuild time: " + l,
                    "Model check count: " + eclipticSeasons$countModel);
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$compile_check(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        eclipticSeasons$time = System.currentTimeMillis();
        eclipticSeasons$countModel = 0;

        ((SodiumStatus) buildContext.cache.getBlockRenderer()).eclipticSeasons$bindCounter(this);
    }

    @Override
    public void eclipticSeasons$addCount() {
        eclipticSeasons$countModel++;
    }
}

package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;

import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput> implements SodiumBoard {

    // @Inject(
    //         remap = false,
    //         method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    //         at = @At(value = "INVOKE",
    //                 target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V")
    // )
    // private void eclipticseasons$execute(
    //         ChunkBuildContext buildContext,
    //         CancellationToken cancellationToken,
    //         CallbackInfoReturnable<ChunkBuildOutput> cir,
    //         @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos,
    //         @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset
    // ) {
    //     BlockState blockState = Blocks.SHORT_GRASS.defaultBlockState();
    //     BakedModel model = buildContext.cache.getBlockModels().getBlockModel(blockState);
    //     buildContext.cache.getBlockRenderer().renderModel(model, blockState, blockPos, modelOffset);
    // }

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
        if (l > ClientConfig.Debug.minChunkCompileWaringTime.getAsInt())
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

    // @Inject(
    //         method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    //         at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V")
    // )
    // private void eclipticseasons$compile_tess(ChunkBuildContext buildContext,
    //                                           CancellationToken cancellationToken,
    //                                           CallbackInfoReturnable<ChunkBuildOutput> cir,
    //                                           @Local(ordinal = 0) BlockPos.MutableBlockPos pos,
    //                                           @Local BlockState state) {
    //     BlockPos.MutableBlockPos mutableBlockPos = ModelManager.posToMutable(pos);
    //     RandomSource random = RandomSource.create();
    //     long seed = state.getSeed(pos);
    //     BakedModel model = null;
    //     for (int zzz = 0; zzz < 100; zzz++) {
    //          model = ModelManager.findModel(buildContext.cache.getWorldSlice(),
    //                 pos, state, random, seed, mutableBlockPos);
    //     }
    //     try {
    //         TextureAtlasSprite particleIcon = model.getParticleIcon();
    //     } catch (Exception e) {
    //     } finally {
    //     }
    //
    // }
}

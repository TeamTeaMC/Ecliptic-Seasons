package com.teamtea.eclipticseasons.mixin.compat.iris_test;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput>  {

    public MixinBlockRenderTask(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

    @Inject(
            remap = false,
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V")
    )
    private void eclipticseasons$execute(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos,
            @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset,
            @Local TranslucentGeometryCollector collector
            ) {
        // refer to MixinChunkMeshBuildTask
        BlockPos abovePos=blockPos.above();
        BlockPos aboveModelPos=modelOffset.above();
        if (((BlockAndTintGetter) (buildContext.cache.getWorldSlice())).getBlockState(abovePos).isEmpty()) {
            BlockState blockState = Blocks.SHORT_GRASS.defaultBlockState();

            // beginBlock to inform here we need
            if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
                {
                    ((BlockSensitiveBufferBuilder) (buildContext.buffers))
                            .beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SHORT_GRASS.defaultBlockState()), (byte) 0, (byte) blockState.getLightEmission(), abovePos.getX(), abovePos.getY(), abovePos.getZ());
                }
            }

            BakedModel model = buildContext.cache.getBlockModels().getBlockModel(blockState);
            buildContext.cache.getBlockRenderer().renderModel(model, blockState, abovePos, aboveModelPos);

            FluidState fluidState = Fluids.WATER.getSource().defaultFluidState();
            if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
                ((BlockSensitiveBufferBuilder)(buildContext.buffers)).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(fluidState.createLegacyBlock()), (byte)1, (byte)blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
            buildContext.cache.getFluidRenderer().render(buildContext.cache.getWorldSlice(), blockState, fluidState, abovePos, aboveModelPos, collector, buildContext.buffers);
            // Minecraft.getInstance().getBlockRenderer().renderLiquid(abovePos, buildContext.cache.getWorldSlice(), buildContext.buffers., fluidState.createLegacyBlock(), fluidState);
            ((BlockSensitiveBufferBuilder) (buildContext.buffers)).endBlock();
        }
    }

    // @Inject(
    //         method = {"execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"},
    //         at = {@At(
    //                 value = "INVOKE",
    //                 target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V"
    //         )}
    // )
    // private void eclipticSeasons$onRenderModel(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir, @Local ChunkBuildBuffers buffers, @Local BlockState blockState, @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos, @Local LevelSlice levelSlice) {
    //     if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
    //         {
    //             if (((BlockAndTintGetter) levelSlice).getBlockState(blockPos.above()).isEmpty())
    //                 ((BlockSensitiveBufferBuilder) buffers)
    //                         .beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SHORT_GRASS.defaultBlockState()), (byte) 0, (byte) blockState.getLightEmission(), blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
    //         }
    //     }
    // }
    //
    // @Inject(
    //         method = {"execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"},
    //         at = {@At(
    //                 value = "INVOKE",
    //                 target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
    //         )}
    // )
    // private void eclipticSeasons$onEnd(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir, @Local ChunkBuildBuffers buffers, @Local BlockState blockState) {
    //     ((BlockSensitiveBufferBuilder) buffers).endBlock();
    // }

    // @Inject(
    //         method = {"execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"},
    //         at = {@At(
    //                 value = "INVOKE",
    //                 target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
    //         )}
    // )
    // private void eclipticSeasons$onRenderLiquid(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir, @Local ChunkBuildBuffers buffers, @Local BlockState blockState, @Local FluidState fluidState, @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos) {
    //     if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
    //         ((BlockSensitiveBufferBuilder)buffers).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(fluidState.createLegacyBlock()), (byte)1, (byte)blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    //     }
    // }
}

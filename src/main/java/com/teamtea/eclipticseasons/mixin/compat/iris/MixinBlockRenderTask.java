package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ChunkBuilderMeshingTask.class}, priority = 1100)
public abstract class MixinBlockRenderTask extends ChunkBuilderTask<ChunkBuildOutput> {

    public MixinBlockRenderTask(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }


    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V")
    )
    private void eclipticseasons$renderFrozenWaterIce_cancelWater(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local(name = "fluidState") FluidState fluidState,
            @Local(name = "blockState") BlockState blockState,
            @Local(name = "blockPos") BlockPos.MutableBlockPos blockPos,
            @Local(name = "modelOffset") BlockPos.MutableBlockPos modelOffset,
            @Local(name = "buffers") ChunkBuildBuffers buffers,
            @Local(name = "blockRenderer") BlockRenderer blockRenderer) {
        if (!CompatModule.ClientConfig.unifiedFrozenWater.get() ||
                IceKeeper.notFrozen(buildContext.cache.getWorldSlice(), blockPos, blockState, fluidState))
            return;
        if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null
                && buffers instanceof VertexEncoderInterface bufferBuilder) {
            bufferBuilder.beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.ICE.defaultBlockState()), (byte) 0, (byte) blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            //((BlockSensitiveBufferBuilder)buffers).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(fluidState.createLegacyBlock()), (byte)1, (byte)blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }

}

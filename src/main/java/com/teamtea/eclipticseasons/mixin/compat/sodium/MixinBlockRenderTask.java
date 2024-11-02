package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask {

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

}

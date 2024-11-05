package com.teamtea.eclipticseasons.mixin.compat.rubidium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask {

    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;")
    // )
    // private ChunkRenderTypeSet eclipticseasons$tesselateWithAO_getQuads(
    //         ChunkRenderTypeSet original,
    //         @Local BlockRenderContext ctx,
    //         @Local ChunkBuildBuffers buffers,
    //         @Local BlockRenderCache cache,
    //         @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
    //         @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
    //         @Local(ordinal = 0) BlockState state
    // ) {
    //
    //     BakedModel snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
    //     if (snowModel != null) {
    //         ctx.update(mutableBlockPos,
    //                 mutableBlockPos2,
    //                 state,
    //                 snowModel,
    //                 state.getSeed(mutableBlockPos),
    //                 null,
    //                 RenderType.cutoutMipped());
    //         cache.getBlockRenderer().renderModel(ctx, buffers);
    //         if (ModelManager.isModelReplaced(state, snowModel))
    //             return ChunkRenderTypeSet.none();
    //     }
    //     return original;
    // }

    @Shadow(remap = false)
    @Final
    private RandomSource random;

    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;")
    // )
    // private ChunkRenderTypeSet eclipticseasons$tesselateWithAO_getQuads(
    //         ChunkRenderTypeSet original,
    //         @Local BlockRenderContext ctx,
    //         @Local ChunkBuildBuffers buffers,
    //         @Local BlockRenderCache cache,
    //         @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
    //         @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
    //         @Local(ordinal = 0) BlockState state
    // ) {
    //
    //     BakedModel snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
    //     if (snowModel != null) {
    //         // ctx.update(mutableBlockPos,
    //         //         mutableBlockPos2,
    //         //         state,
    //         //         snowModel,
    //         //         state.getSeed(mutableBlockPos),
    //         //         null,
    //         //         RenderType.cutoutMipped());
    //         // cache.getBlockRenderer().renderModel(ctx, buffers);
    //         // if (ModelManager.isModelReplaced(state, snowModel))
    //         //     return ChunkRenderTypeSet.none();
    //         if (!original.contains(RenderType.cutoutMipped())){
    //            return ChunkRenderTypeSet.union(original,ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
    //
    //            // return ChunkRenderTypeSet.union(original,ChunkRenderTypeSet.of(ModelManager.CUTOUT_MIPPED));
    //         }
    //     }
    //     return original;
    // }

    @ModifyExpressionValue(
            remap = false,
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$tesselateWithAO_getQuads(
            boolean original, @Local BlockRenderContext ctx, @Local ChunkBuildBuffers buffers, @Local BlockRenderCache cache, @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos, @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2, @Local(ordinal = 0) BlockState state
    ) {

        BakedModel snowModel = null;
        if (!original) {
            snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
        } else {
            if (ModelManager.isModelReplaced(state)) {
                snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
            }
        }

        if (snowModel != null) {
            original = false;
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    state.getSeed(mutableBlockPos),
                    null,
                    ModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);
        }
        return original;
    }

}

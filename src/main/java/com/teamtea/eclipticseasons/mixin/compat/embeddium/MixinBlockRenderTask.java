package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import com.teamtea.eclipticseasons.compat.iris.IIrisShaderAccesor;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.embeddedt.embeddium.render.frapi.FRAPIModelUtils;
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
    private boolean eclipticseasons$execute_tryRender(
            boolean original,
            @Local(argsOnly = true) ChunkBuildContext buildContext,
            @Local BakedModel bakedModel,
            @Local BlockRenderContext ctx, @Local ChunkBuildBuffers buffers, @Local BlockRenderCache cache, @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos, @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2, @Local(ordinal = 0) BlockState state
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
            if (this instanceof IIrisShaderAccesor iIrisShaderAccesor) {
                int blockType = MapChecker.getBlockType(state, ctx.world(), mutableBlockPos);
                switch (blockType) {
                    case MapChecker.FLAG_BLOCK,
                         MapChecker.FLAG_SLAB,
                         MapChecker.FLAG_STAIRS,
                         MapChecker.FLAG_STAIRS_TOP,
                         MapChecker.FLAG_FARMLAND,
                         MapChecker.FLAG_CUSTOM ->
                            iIrisShaderAccesor.eclipticseasons$setSnowy(buildContext, Blocks.SNOW.defaultBlockState());
                }
            }
            original = false;
            ((FabricModelDelayChecker) ctx).updateIsLastFabric(
                    bakedModel != null && FRAPIModelUtils.isFRAPIModel(bakedModel));
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    state.getSeed(mutableBlockPos),
                    ctx.modelData(),
                    ModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);

            ((FabricModelDelayChecker) ctx).updateIsLastFabric(false);
        }
        return original;
    }

}

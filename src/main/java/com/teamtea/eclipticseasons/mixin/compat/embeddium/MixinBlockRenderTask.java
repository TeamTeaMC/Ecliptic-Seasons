package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import com.teamtea.eclipticseasons.compat.iris.IIrisShaderAccesor;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.embeddedt.embeddium.render.frapi.FRAPIModelUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(
            // remap = false,
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    ordinal = 0,
                    target = "Lnet/minecraft/util/RandomSource;setSeed(J)V")
    )
    private void eclipticseasons$execute_findModel(
            ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local BakedModel bakedModel,
            @Local BlockRenderContext ctx,
            @Local ChunkBuildBuffers buffers,
            @Local BlockRenderCache cache,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
            @Local(ordinal = 0) BlockState state,
            @Local long seed,
            @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            @Share("shouldReplace") LocalBooleanRef replace
    ) {
        random.setSeed(seed);
        BlockAndTintGetter level = ctx.world();
        BakedModel model = ExtraModelManager.findModel(level, mutableBlockPos, state, random, seed, level instanceof IExtendBlockView extendBlockView ? extendBlockView.getModelCheckPos() : null);
        snowModelRef.set(model);
        replace.set(model != null
                && ExtraModelManager.isModelReplaceable(state, ctx.world(), mutableBlockPos, model));
    }

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
            @Local BlockRenderContext ctx,
            @Local ChunkBuildBuffers buffers,
            @Local BlockRenderCache cache,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
            @Local(ordinal = 0) BlockState state,
            @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            @Share("shouldReplace") LocalBooleanRef replace,
            @Local ModelData modelData
    ) {

        // BakedModel snowModel = null;
        // if (!original) {
        //     snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
        // } else {
        //     if (ModelManager.isModelReplaced(state)) {
        //         snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random);
        //     }
        // }
        BakedModel snowModel = null;
        if (replace.get()) {
            original = false;
        }

        if (!original) {
            snowModel = snowModelRef.get();
            snowModelRef.set(null);
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
                         MapChecker.FLAG_CUSTOM,
                         MapChecker.FLAG_CUSTOM_JSON,
                         MapChecker.FLAG_CUSTOM_JSON_WITH_TOP ->
                            iIrisShaderAccesor.eclipticseasons$setSnowy(buildContext, Blocks.SNOW.defaultBlockState());
                }
            }
            original = false;
            ((FabricModelDelayChecker) ctx).updateIsLastFabric(
                    bakedModel);
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    state.getSeed(mutableBlockPos),
                    modelData,
                    ExtraModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);

            ((FabricModelDelayChecker) ctx).updateIsLastFabric(null);
        }
        return original;
    }

}

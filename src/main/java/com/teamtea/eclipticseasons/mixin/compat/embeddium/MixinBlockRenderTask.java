package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildBuffers;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildOutput;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderCache;
import org.embeddedt.embeddium.impl.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import org.embeddedt.embeddium.impl.util.task.CancellationToken;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask {

    @Shadow(remap = false)
    @Final
    private RandomSource random;

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$checkPos = new BlockPos.MutableBlockPos();

    @Inject(
            remap = false,
            method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
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
            @Local ModelData modelData
    ) {
        random.setSeed(seed);
        eclipticseasons$checkPos.set(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());
        BakedModel model = ExtraModelManager.findModel(ctx.world(), mutableBlockPos, state, random, seed, eclipticseasons$checkPos);

        IExtraRendererContextOwner.of(ctx.world())
                .setModelData(modelData)
                .setOriginalModel(bakedModel)
                .setExtraModel(model)
                .setReplace(model != null
                        && ExtraModelManager.isModelReplaceable(state, ctx.world(), mutableBlockPos, model));
    }


    @ModifyExpressionValue(
            remap = false,
            method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$execute(
            boolean original,
            @Local BlockRenderContext ctx,
            @Local ChunkBuildBuffers buffers,
            @Local BlockRenderCache cache,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
            @Local(ordinal = 0) BlockState state,
            @Local long seed,
            // @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            // @Share("shouldReplace") LocalBooleanRef replace,
            @Local ModelData modelData
    ) {

        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(ctx.world());

        BakedModel snowModel = null;
        if (rendererHolder.isReplace()) {
            original = false;
        }

        if (!original) {
            snowModel = rendererHolder.getExtraModel();
            // snowModelRef.set(null);
        }

        if (snowModel != null) {
            original = false;
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    state.getSeed(mutableBlockPos),
                    rendererHolder.getModelData(),
                    ExtraModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);
        }

        if (!original) {
            rendererHolder.resetAll();
        }
        return original;
    }


    @Inject(
            remap = false,
            method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    )
    private void eclipticseasons$renderSnowLayerIn(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local BlockRenderContext ctx,
            @Local ChunkBuildBuffers buffers,
            @Local BlockRenderCache cache,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local(ordinal = 1) BlockPos.MutableBlockPos mutableBlockPos2,
            @Local(ordinal = 0) BlockState state
    ) {
        BakedModel bm = ExtraModelManager.shouldRenderedWithSnowInside(ctx.world(), mutableBlockPos, state, null);
        if (bm != null) {
            //if (this instanceof IIrisShaderAccesor iIrisShaderAccesor) {
            //    iIrisShaderAccesor.eclipticseasons$reset(buildContext);
            //}
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    Blocks.SNOW.defaultBlockState(),
                    bm,
                    state.getSeed(mutableBlockPos),
                    ModelData.EMPTY,
                    RenderType.solid());
            cache.getBlockRenderer().renderModel(ctx, buffers);
        }
    }

    @Inject(
            method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
            remap = false,
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/render/chunk/compile/pipeline/FluidRenderer;render(Lorg/embeddedt/embeddium/impl/world/WorldSlice;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildBuffers;)V")
    )
    private void eclipticseasons$renderFrozenWaterIce(ChunkBuildContext buildContext,
                                                      CancellationToken cancellationToken,
                                                      CallbackInfoReturnable<ChunkBuildOutput> cir,
                                                      @Local BlockRenderContext ctx,
                                                      @Local ChunkBuildBuffers buffers,
                                                      @Local FluidState fluidState,
                                                      @Local BlockState blockState,
                                                      @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos,
                                                      @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset) {


        if (IceKeeper.notFrozen(buildContext.cache.getWorldSlice(), blockPos, blockState, fluidState)) return;
        BakedModel model = IceKeeper.getIceModel(blockState, fluidState);
        if (model != null) {
            BlockState fakeState = IceKeeper.getFakeState(blockState, fluidState);
            ctx.update(blockPos,
                    modelOffset,
                    fakeState,
                    model,
                    blockState.getSeed(blockPos),
                    ModelData.EMPTY,
                    ExtraModelManager.getRenderType(fakeState));
            buildContext.cache.getBlockRenderer().renderModel(ctx, buffers);
        }
    }

}

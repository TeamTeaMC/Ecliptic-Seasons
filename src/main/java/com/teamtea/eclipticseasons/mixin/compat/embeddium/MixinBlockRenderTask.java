package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.iris.IIrisShaderAccesor;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import it.unimi.dsi.fastutil.HashCommon;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask {

    @Shadow(remap = false)
    @Final
    private RandomSource random;


    @Inject(
            // remap = false,
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    ordinal = 1,
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
        BlockAndTintGetter level = ctx.world();
        BakedModel model = ExtraModelManager.findModel(level, mutableBlockPos, state, random, seed, level instanceof IExtendBlockView extendBlockView ? extendBlockView.getModelCheckPos() : null);

        IExtraRendererContextOwner.of(ctx.world())
                .setModelData(modelData)
                .setOriginalModel(bakedModel)
                .setExtraModel(model)
                .setReplace(model != null
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
            @Local(ordinal = 0) BlockState state
            // @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            // @Share("shouldReplace") LocalBooleanRef replace,
            // @Local ModelData modelData
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
            if (this instanceof IIrisShaderAccesor iIrisShaderAccesor) {
                if (ExtraModelManager.renderAsSnowInShader(state, ctx.world(), mutableBlockPos))
                    iIrisShaderAccesor.eclipticseasons$setSnowy(buildContext, Blocks.SNOW_BLOCK.defaultBlockState());
            }
            original = false;
            long seed = state.getSeed(mutableBlockPos);
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    seed,
                    rendererHolder.getModelData(),
                    ExtraModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);


            int y = mutableBlockPos.getY();
            int layer = ExtraModelManager.getLayer(ctx.localSlice(), mutableBlockPos, state, snowModel, seed);
            if (layer > 0) {
                if (this instanceof IIrisShaderAccesor iIrisShaderAccesor) {
                    iIrisShaderAccesor.eclipticseasons$setSnowy(buildContext, Blocks.SNOW.defaultBlockState());
                }
                ctx.update(mutableBlockPos,
                        mutableBlockPos2.setY(mutableBlockPos2.getY() + 1),
                        state,
                        ExtraModelManager.getSnowLayerModel(layer),
                        seed,
                        rendererHolder.getModelData(),
                        ExtraModelManager.getRenderType(state));
                cache.getBlockRenderer().renderModel(ctx, buffers);
                mutableBlockPos2.setY(mutableBlockPos2.getY() - 1);
            }
            mutableBlockPos.setY(y);
        }

        if (!original) {
            rendererHolder.resetAll();
        }
        return original;
    }


    @Inject(
            remap = false,
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderCache;getBlockModels()Lnet/minecraft/client/renderer/block/BlockModelShaper;")
    )
    private void eclipticseasons$renderSnowLayerIn_below(
            ChunkBuildContext buildContext,
            CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local BlockRenderContext ctx,
            @Local(ordinal = 0) BlockPos.MutableBlockPos mutableBlockPos,
            @Local LocalRef<BlockState> stateLocalRef
    ) {
        var state = ExtraModelManager.shouldBlockAsSnowyState(stateLocalRef.get(), ctx.localSlice(), mutableBlockPos);
        if (state != stateLocalRef.get())
            stateLocalRef.set(state);
    }

    @Inject(
            //remap = false,
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
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
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            remap = false,
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lme/jellysquid/mods/sodium/client/world/WorldSlice;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V")
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

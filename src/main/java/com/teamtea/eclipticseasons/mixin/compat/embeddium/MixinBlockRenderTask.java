package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.client.IESRendererHolder;
import com.teamtea.eclipticseasons.client.core.ESRendererHolderImpl;
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

        IESRendererHolder.of(ctx.world())
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

        ESRendererHolderImpl rendererHolder = IESRendererHolder.of(ctx.world());

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
                    iIrisShaderAccesor.eclipticseasons$setSnowy(buildContext, Blocks.SNOW.defaultBlockState());
            }
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

}

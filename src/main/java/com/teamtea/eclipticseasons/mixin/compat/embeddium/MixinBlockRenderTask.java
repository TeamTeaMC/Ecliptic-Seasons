package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
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
            @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            @Share("shouldReplace") LocalBooleanRef replace
    ) {
        random.setSeed(seed);
        eclipticseasons$checkPos.set(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());
        BakedModel model = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random, seed, eclipticseasons$checkPos);
        snowModelRef.set(model);
        replace.set(model != null
                && ModelManager.isModelReplaceable(state, ctx.world(), mutableBlockPos, model));
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
            @Share("snowModelRef") LocalRef<BakedModel> snowModelRef,
            @Share("shouldReplace") LocalBooleanRef replace,
            @Local ModelData modelData
    ) {

        // BakedModel snowModel = null;
        // if (!original) {
        //     snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random, ctx.seed(), null);
        // } else {
        //     // if (ModelManager.isModelReplaceable(state))
        //     if (ModelManager.isModelReplaceable(state, ctx.world(), ctx.pos())) {
        //         snowModel = ModelManager.findModel(ctx.world(), mutableBlockPos, state, random, ctx.seed(), null);
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
            original = false;
            ctx.update(mutableBlockPos,
                    mutableBlockPos2,
                    state,
                    snowModel,
                    seed,
                    // TODO: we need the model data
                    modelData,
                    ModelManager.getRenderType(state));
            cache.getBlockRenderer().renderModel(ctx, buffers);
        }
        return original;
    }

}

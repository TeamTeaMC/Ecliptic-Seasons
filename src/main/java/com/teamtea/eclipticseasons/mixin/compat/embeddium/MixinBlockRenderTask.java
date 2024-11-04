package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildBuffers;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderCache;
import org.embeddedt.embeddium.impl.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ChunkBuilderMeshingTask.class})
public abstract class MixinBlockRenderTask {

    @Shadow(remap = false)
    @Final
    private RandomSource random;

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
            @Local(ordinal = 0) BlockState state
    ) {

        BakedModel snowModel=null;
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

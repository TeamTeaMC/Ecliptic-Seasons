package com.teamtea.eclipticseasons.mixin.compat.oculus;


import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.iris.IIrisShaderAccesor;
import com.teamtea.eclipticseasons.config.ClientConfig;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.irisshaders.iris.compat.sodium.impl.block_context.ChunkBuildBuffersExt;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {ChunkBuilderMeshingTask.class}, priority = 1200)
public abstract class MixinIrisForgeHelpers implements IIrisShaderAccesor {

    // @Inject(
    //         method = {"execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"},
    //         at = {@At(
    //                 value = "INVOKE",
    //                 target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"
    //         )},
    //         locals = LocalCapture.CAPTURE_FAILHARD
    // )
    // private void eclipticseasons$after_iris_wrapGetBlockLayer(ChunkBuildContext context, CancellationToken cancellationSource, CallbackInfoReturnable<ChunkBuildOutput> cir, BuiltSectionInfo.Builder renderData, VisGraph occluder, ChunkBuildBuffers buffers, BlockRenderCache cacheLocal, WorldSlice slice, int baseX, int baseY, int baseZ, int maxX, int maxY, int maxZ, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos renderOffset, BlockRenderContext context2, int relY, int relZ, int relX, BlockState blockState) {
    //     if (context.buffers instanceof ChunkBuildBuffersExt
    //             && EclipticSeasonsApi.getInstance().isSnowyBlock(Minecraft.getInstance().level, blockState, pos)) {
    //         blockState = Blocks.SNOW.defaultBlockState();
    //         ((ChunkBuildBuffersExt) context.buffers).iris$setMaterialId(blockState, (short) -1, (byte) blockState.getLightEmission());
    //     }
    //
    // }

    @Override
    public void eclipticseasons$setSnowy(ChunkBuildContext context, BlockState blockState) {
        if (context.buffers instanceof ChunkBuildBuffersExt
                && CompatModule.ClientConfig.unifiedSnowyBlockShading.get()) {
            ((ChunkBuildBuffersExt) context.buffers).iris$setMaterialId(blockState, (short) -1, (byte) blockState.getLightEmission());
        }
    }

    @Override
    public void eclipticseasons$reset(ChunkBuildContext buildContext) {
        if (buildContext.buffers instanceof ChunkBuildBuffersExt) {
            ((ChunkBuildBuffersExt) buildContext.buffers).iris$resetBlockContext();
        }
    }
}

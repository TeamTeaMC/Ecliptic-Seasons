package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.google.common.annotations.Beta;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Beta
@Mixin(value = {AbstractBlockRenderContext.class}, priority = 1200)
public abstract class MixinIrisForgeHelpers {

    @Shadow
    protected BlockPos pos;
    @Shadow
    protected BlockAndTintGetter level;


    //@Inject(
    //        method = {"bufferDefaultModel"},
    //        at = {@At(
    //                value = "INVOKE",
    //                target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/RenderType;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/util/List;"
    //        )}
    //)
    //private void eclipticseasons$bufferDefaultModel_aftergetBlockAppearance(BakedModel model, BlockState state, CallbackInfo ci, @Local Direction cullFace) {
    //    if ((Object) this instanceof BlockRenderer r) {
    //        if (this instanceof SodiumStatus sodiumStatus
    //                && sodiumStatus.getSnowModel() instanceof ISnowyReplaceModel snowyBakedModelWrapper
    //                && CompatModule.ClientConfig.unifiedSnowyBlockShading.isTrue()) {
    //            if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && cullFace != null) {
    //                if (CompatModule.ClientConfig.unifiedSnowyBlockSides.isFalse() && cullFace != Direction.UP)
    //                    return;
    //
    //                if (ExtraModelManager.renderAsSnowInShader(state, level, pos)) {
    //                    ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SNOW_BLOCK.defaultBlockState()));
    //                }
    //            }
    //        }
    //    }
    //
    //}

}

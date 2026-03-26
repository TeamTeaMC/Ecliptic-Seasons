package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.google.common.annotations.Beta;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
//import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({VanillaModelEncoder.class})
@Beta
public abstract class MixinFabricVanillaModelEncoder {

    //@ModifyExpressionValue(
    //        remap = false,
    //        method = "emitBlockQuads",
    //        at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    //)
    //private static List<BakedQuad> eclipticseasons$emitBlockQuads_getQuads_cancel_top(List<BakedQuad> original,
    //                                                                           @Local(argsOnly = true) BakedModel bakedModel,
    //                                                                           @Local(argsOnly = true) RenderContext renderContext,
    //                                                                           @Local(argsOnly = true) Supplier<RandomSource> randomSourceSupplier,
    //                                                                           @Local(argsOnly = true) BlockState state,
    //                                                                           @Local Direction direction) {
    //    if (renderContext instanceof TerrainRenderContextLevelGetter terrainRenderContextLevelGetter) {
    //        BlockAndTintGetter blockAndTintGetter = terrainRenderContextLevelGetter.eclipticseasons$get();
    //        if (blockAndTintGetter instanceof ISeedProvider seedProvider) {
    //            if (blockAndTintGetter instanceof ExtendBlockView extendBlockView) {
    //                return ExtraModelManager.cancelTop(bakedModel, blockAndTintGetter, state, terrainRenderContextLevelGetter.eclipticseasons$getPos(), direction, randomSourceSupplier.get(), seedProvider.getCacheSeed(), original, extendBlockView.getCacheBakeQuad());
    //            }
    //        }
    //    }
    //    return original;
    //}


}

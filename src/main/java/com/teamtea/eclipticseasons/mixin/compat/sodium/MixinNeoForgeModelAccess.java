package com.teamtea.eclipticseasons.mixin.compat.sodium;


import net.caffeinemc.mods.sodium.client.world.LevelSlice;
// import net.caffeinemc.mods.sodium.neoforge.model.NeoForgeModelAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({LevelSlice.class})
public abstract class MixinNeoForgeModelAccess {


    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getQuads",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$getQuads_getQuads(List<BakedQuad> original, @Local BlockAndTintGetter blockAndTintGetter, @Local BlockPos pos, @Local BlockState state, @Local Direction side, @Local RandomSource rand) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, side, rand, state.getSeed(pos), original);
    // }


    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getQuads",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> eclipticseasons$getQuads_getQuads(List<BakedQuad> original, @Local BakedModel bakedModel, @Local BlockAndTintGetter blockAndTintGetter, @Local BlockPos pos, @Local BlockState state, @Local Direction side, @Local RandomSource rand) {
    //     return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, side, rand, state.getSeed(pos), original);
    // }


}

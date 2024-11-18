package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.caffeinemc.mods.sodium.neoforge.model.NeoForgeModelAccess;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin({NeoForgeModelAccess.class})
public abstract class MixinNeoForgeModelAccess {


    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getQuads",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> ecliptic$getQuads_getQuads(List<BakedQuad> original, @Local BlockAndTintGetter blockAndTintGetter, @Local BlockPos pos, @Local BlockState state, @Local Direction side, @Local RandomSource rand) {
    //     return ModelManager.appendOverlay(blockAndTintGetter, state, pos, side, rand, state.getSeed(pos), original);
    // }


    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getQuads",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    // )
    // private List<BakedQuad> ecliptic$getQuads_getQuads(List<BakedQuad> original, @Local BakedModel bakedModel, @Local BlockAndTintGetter blockAndTintGetter, @Local BlockPos pos, @Local BlockState state, @Local Direction side, @Local RandomSource rand) {
    //     return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, pos, side, rand, state.getSeed(pos), original);
    // }


}

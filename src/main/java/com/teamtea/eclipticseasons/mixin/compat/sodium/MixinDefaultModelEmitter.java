package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import net.caffeinemc.mods.sodium.client.services.DefaultModelEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin({DefaultModelEmitter.class})
public abstract class MixinDefaultModelEmitter {


    @ModifyExpressionValue(
            remap = false,
            method = "emitModel",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;collectPartsOf(Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/caffeinemc/mods/sodium/client/render/helper/ListStorage;)Ljava/util/List;")
    )
    private List<BlockStateModelPart> eclipticseasons$getQuads_getQuads(
            List<BlockStateModelPart> original,
            @Local(argsOnly = true) BlockStateModel stateModel,
            @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter,
            @Local(argsOnly = true) BlockPos pos,
            @Local(argsOnly = true) BlockState state,
            @Local(argsOnly = true) RandomSource random) {
        IExtraRendererContextOwner.of(blockAndTintGetter)
                .apply(blockAndTintGetter, pos, state, random, original);
        return original;
    }


}

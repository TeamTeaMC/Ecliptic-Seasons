package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockOcclusionCache.class})
public abstract class MixinBlockOcclusionCache {

    @ModifyExpressionValue(
            method = "shouldDrawSide",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$skip_if_fake_snow(BlockState original,
                                                         @Local(argsOnly = true) BlockState selfState,
                                                         @Local(argsOnly = true) BlockGetter view,
                                                         @Local(name = "otherPos") BlockPos.MutableBlockPos otherPos,
                                                         @Local(argsOnly = true) BlockPos selfPos,
                                                         @Local(argsOnly = true) Direction facing) {
        return ExtraModelManager.getFakeBlockState(original, selfState, view, otherPos, selfPos, facing);
    }


}

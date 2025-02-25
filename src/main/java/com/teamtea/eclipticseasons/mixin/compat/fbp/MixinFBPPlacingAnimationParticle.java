package com.teamtea.eclipticseasons.mixin.compat.fbp;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import hantonik.fbp.particle.FBPPlacingAnimationParticle;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FBPPlacingAnimationParticle.class})
public abstract class MixinFBPPlacingAnimationParticle {

    @ModifyExpressionValue(
            remap = false,
            method = "renderBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderTypeLookup;getMovingBlockRenderType(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/renderer/RenderType;")
    )
    private RenderType eclipticseasons$coldEnoughToSnow(RenderType original, @Local(argsOnly = true) BlockState blockState) {
        if (ModelManager.shouldCutoutMipped(blockState))
            return RenderType.cutoutMipped();
        return original;
    }
}

package com.teamtea.eclipticseasons.mixin.compat.fbp;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import hantonik.fbp.platform.services.ForgeClientHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ForgeClientHelper.class})
public abstract class MixinFancyBlockParticles {

    @ModifyExpressionValue(
            method = "renderBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getMovingBlockRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;")
    )
    private RenderType eclipticseasons$coldEnoughToSnow(RenderType original, @Local(argsOnly = true) BlockState blockState) {
        if (ModelManager.shouldCutoutMipped(blockState))
            return RenderType.cutoutMipped();
        return original;
    }

    @Inject(
            remap = false,
            method = "coldEnoughToSnow",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void eclipticseasons$coldEnoughToSnow(Holder<Biome> biome, BlockPos pos, Level level, CallbackInfoReturnable<Boolean> cir) {
        boolean v = eclipticseasons$isCold(biome, pos, level);
        cir.setReturnValue(v);
    }

    @Inject(
            remap = false,
            method = "warmEnoughToRain",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void eclipticseasons$warmEnoughToRain(Holder<Biome> biome, BlockPos pos, Level level, CallbackInfoReturnable<Boolean> cir) {
        boolean v = eclipticseasons$isCold(biome, pos, level);
        cir.setReturnValue(!v);
    }

    @Unique
    private static boolean eclipticseasons$isCold(Holder<Biome> biome, BlockPos pos, Level level) {
        return WeatherManager.getPrecipitationAt(level, biome.value(), pos) == Biome.Precipitation.SNOW;
    }

}

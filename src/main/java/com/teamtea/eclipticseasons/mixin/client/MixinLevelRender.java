package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(WorldRenderer.class)
public abstract class MixinLevelRender {


    @Shadow
    @Nullable
    private ClientWorld level;


    @WrapOperation(
            method = {"renderSnowAndRain", "tickRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F")
    )
    private float ecliptic$renderSnowAndRain_getPrecipitationAt(Biome instance, BlockPos blockPos, Operation<Float> original) {
        if (ClientWeatherChecker.isBiomeRainyLast(instance))
            return WeatherManager.getPrecipitationAt(level, instance, blockPos) == Biome.RainType.SNOW ?
                    0f : 1f;
        return WeatherManager.getRainOrSnow(level, instance, blockPos) == Biome.RainType.SNOW ?
                0f : 1f;
    }

    @WrapOperation(
            method = "tickRain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;")
    )
    private Biome.RainType ecliptic$tickRain_getPrecipitationAt(Biome instance, Operation<Biome.RainType> original) {
        if (ClientWeatherChecker.isBiomeRainyLast(instance))
            return WeatherManager.getPrecipitationAt(level, instance, BlockPos.ZERO);
        return WeatherManager.getRainOrSnow(level, instance, BlockPos.ZERO);
    }

    @Inject(
            method = "renderSnowAndRain",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V")
    )
    private void ecliptic$renderSnowAndRain_ModifySnowAmount(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci, @Local(ordinal = 3) LocalIntRef integerLocalRef) {
        integerLocalRef.set(ClientWeatherChecker.ModifySnowAmount(integerLocalRef.get(), pPartialTick, level));
    }

    @WrapOperation(
            method = "tickRain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playLocalSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V")
    )
    private void ecliptic$tickRain_modifySound(ClientWorld instance, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundSource, float pVolume, float pPitch, boolean pDistanceDelay, Operation<Void> original) {
        original.call(instance, blockPos, soundEvent, soundSource, ClientWeatherChecker.modifyVolume(soundEvent, pVolume, level), ClientWeatherChecker.modifyPitch(soundEvent, pPitch, level), pDistanceDelay);

    }

    @ModifyVariable(
            method = {"tickRain"},
            at = @At("STORE"),
            ordinal = 0
    )
    private int ecliptic$tickRain_modifyAmount(int originalNum) {
        return ClientWeatherChecker.modifyRainAmount(originalNum, level);
    }

}

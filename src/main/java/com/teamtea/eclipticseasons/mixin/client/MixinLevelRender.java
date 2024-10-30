package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRender {


    @Shadow
    @Nullable
    public ClientLevel level;

    @WrapOperation(
            method = {"tickRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> ecliptic$tickRain_getBiome(LevelReader instance, BlockPos pPos, Operation<Holder<Biome>> original) {
        return instance instanceof Level clevel ?
                MapChecker.getSurfaceBiome(clevel, pPos) :
                original.call(instance, pPos);
    }

    @WrapOperation(
            method = {"renderSnowAndRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> ecliptic$tickRain_getBiome(Level instance, BlockPos blockPos, Operation<Holder<Biome>> original) {
        return level != null ? MapChecker.getSurfaceBiome(instance, blockPos) : original.call(instance, blockPos);
    }

    @WrapOperation(
            method = {"tickRain", "renderSnowAndRain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation ecliptic$renderSnowAndRain_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        return level != null && (WeatherManager.isRainingOrSnowAt(level, pos)
                || ClientWeatherChecker.isBiomeRainyLast(biome)) ?
                WeatherManager.getPrecipitationAt(level, biome, pos) : Biome.Precipitation.NONE;
    }


    @Inject(
            method = "renderSnowAndRain",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V")
    )
    private void ecliptic$renderSnowAndRain_ModifySnowAmount(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci, @Local(ordinal = 3) LocalIntRef integerLocalRef) {
        // if (ServerConfig.Debug.useSolarWeather.get())
        integerLocalRef.set(ClientWeatherChecker.ModifySnowAmount(integerLocalRef.get(), pPartialTick, level));
    }

    @WrapOperation(
            method = "tickRain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")
    )
    private void ecliptic$tickRain_modifySound(ClientLevel instance, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float pVolume, float pPitch, boolean pDistanceDelay, Operation<Void> original) {
        // if (ServerConfig.Debug.useSolarWeather.get())
        {
            original.call(instance, blockPos, soundEvent, soundSource, ClientWeatherChecker.modifyVolume(soundEvent, pVolume, level), ClientWeatherChecker.modifyPitch(soundEvent, pPitch, level), pDistanceDelay);
        }
        // else {
        //     original.call(instance, blockPos, soundEvent, soundSource, pVolume, pPitch, pDistanceDelay);
        // }
    }

    @ModifyVariable(
            method = {"tickRain"},
            at = @At("STORE"),
            ordinal = 0
    )
    private int ecliptic$tickRain_modifyAmount(int originalNum) {
        // if (ServerConfig.Debug.useSolarWeather.get()) {
        return ClientWeatherChecker.modifyRainAmount(originalNum, level);
        // }
        // else return originalNum;
    }

}

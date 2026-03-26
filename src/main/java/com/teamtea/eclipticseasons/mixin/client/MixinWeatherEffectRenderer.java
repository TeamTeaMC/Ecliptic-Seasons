package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class MixinWeatherEffectRenderer {


    @WrapOperation(
            method = {"getPrecipitationAt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickRain_getBiome(Level instance, BlockPos blockPos, Operation<Holder<Biome>> original) {
        return EclipticUtil.hasLocalWeather(instance) ?
                MapChecker.getSurfaceBiome(instance, blockPos) :
                original.call(instance, blockPos);
    }

    // ModifyExpressionValue may cost much time than it
    @WrapOperation(
            method = {"getPrecipitationAt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$renderSnowAndRain_tickRain_getPrecipitationAt(Biome instance, BlockPos pos, int seaLevel, Operation<Biome.Precipitation> original, @Local(argsOnly = true) Level level) {
        if (EclipticUtil.hasLocalWeather(level)) {
            // if (ClientWeatherChecker.isBiomeRainyLast(biome))
            //     return WeatherManager.getPrecipitationAt(level, biome, pos);
            return EclipticUtil.getRainOrSnow(level, instance, pos);
        }
        return VanillaWeather.handlePrecipitationAt(level, instance, pos);
    }


    @WrapOperation(
            method = "tickRainParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")
    )
    private void eclipticseasons$tickRain_modifySound(ClientLevel instance, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float pVolume, float pPitch, boolean pDistanceDelay, Operation<Void> original) {
        if (EclipticUtil.hasLocalWeather(instance)) {
            original.call(instance, blockPos, soundEvent, soundSource, ClientWeatherChecker.modifyVolume(soundEvent, pVolume, instance), ClientWeatherChecker.modifyPitch(soundEvent, pPitch, instance), pDistanceDelay);
        } else {
            original.call(instance, blockPos, soundEvent, soundSource, pVolume, pPitch, pDistanceDelay);
        }
    }

    // @ModifyVariable(
    //         method = {"tickRainParticles"},
    //         at = @At("STORE"),
    //         ordinal = 0
    // )
    // private int eclipticseasons$tickRain_modifyAmount(int originalNum,
    //                                                   @Local(argsOnly = true) ClientLevel level) {
    //     if (EclipticUtil.hasLocalWeather(level)) {
    //         return ClientWeatherChecker.modifyRainAmount(originalNum, level);
    //     } else return originalNum;
    // }

    @Inject(
            method = {"tickRainParticles"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;")
    )
    private void eclipticseasons$tickRain_modifyAmount(ClientLevel level, Camera camera, int ticks, ParticleStatus particleStatus, int weatherRadius, CallbackInfo ci,
                                                       @Local(name = "rainLevel") LocalFloatRef floatRef) {
        if (EclipticUtil.hasLocalWeather(level)) {
            floatRef.set(ClientWeatherChecker.modifyRainAmount(floatRef.get(), level));
        }
    }

}

package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.particle.ParticleUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.environment.SolarTime;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class MixinClientClientLevel {
    @Shadow
    public abstract void addDestroyBlockEffect(BlockPos p_171667_, BlockState p_171668_);

    @Inject(at = {@At("RETURN")}, method = {"animateTick"})
    private void eclipticseasons$animateTick(int x, int y, int z, CallbackInfo ci) {
        ParticleUtil.createParticle((ClientLevel) (Object) this, x, y, z);
    }

    @Inject(at = {@At("RETURN")}, method = {"addEnvironmentAttributeLayers"})
    private void eclipticseasons$addEnvironmentAttributeLayers(EnvironmentAttributeSystem.Builder environmentAttributes, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        SolarTime.attachSolarLayer((Level) (Object) this, environmentAttributes);
    }

    @Inject(at = {@At("RETURN")}, method = {"addDestroyBlockEffect"})
    private void eclipticseasons$addDestroyBlockEffect(BlockPos pos, BlockState state, CallbackInfo ci) {
        // ParticleUtil.attachSnowyParticle((ClientLevel)(Object)this,pos,state);
    }

    @WrapWithCondition(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V")}, method = {"doAnimateTick"})
    private boolean eclipticseasons$doAnimateTick(
            Block instance, BlockState state, Level level, BlockPos pos, RandomSource random, @Local BlockState blockState, @Local(argsOnly = true, ordinal = 0) int pPosX, @Local(argsOnly = true, ordinal = 1) int pPosY, @Local(argsOnly = true, ordinal = 2) int pPosZ, @Local(argsOnly = true, ordinal = 3) int pRange, @Local(argsOnly = true) BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        boolean shouldcancel = ParticleUtil.doAnimateTick((ClientLevel) (Object) this,
                pPosX, pPosY, pPosZ,
                pRange,
                random,
                blockpos$mutableblockpos,
                blockState);
        // if (!shouldcancel) {
        //     original.call(instance, pState, pLevel, pPos, random);
        // }
        return !shouldcancel;
    }


    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"getPrecipitationAt"})
    private Holder<Biome> eclipticseasons$getPrecipitationAt_biome(ClientLevel instance, BlockPos pos, Operation<Holder> original) {
        return MapChecker.getSurfaceBiome(instance, pos);
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;")}, method = {"getPrecipitationAt"})
    private Biome.Precipitation eclipticseasons$getPrecipitationAt_get(Biome instance, BlockPos pos, int seaLevel, Operation<Biome.Precipitation> original) {
        return WeatherManager.getPrecipitationAt((Level) (Object) this, instance, pos);
    }

    @WrapOperation(
            method = "tickWeatherEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")
    )
    private void eclipticseasons$tickRain_modifySound(ClientLevel instance, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float pVolume, float pPitch, boolean pDistanceDelay, Operation<Void> original) {
        original.call(instance, blockPos, soundEvent, soundSource, ClientWeatherChecker.modifyVolume(soundEvent, pVolume, instance), ClientWeatherChecker.modifyPitch(soundEvent, pPitch, instance), pDistanceDelay);
    }

    @Inject(
            method = {"tickWeatherEffects"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;")
    )
    private void eclipticseasons$tickRain_modifyAmount(CallbackInfo ci, @Local(name = "rainLevel") LocalFloatRef floatRef) {
        floatRef.set(ClientWeatherChecker.modifyRainAmount(floatRef.get(), (Level) (Object) this));
    }
}

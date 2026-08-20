package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.particle.ParticleUtil;
import com.teamtea.eclipticseasons.common.environment.SolarTime;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
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
}

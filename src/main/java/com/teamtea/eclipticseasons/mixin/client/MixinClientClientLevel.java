package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class MixinClientClientLevel {
    @Shadow
    public abstract void addDestroyBlockEffect(BlockPos p_171667_, BlockState p_171668_);

    @Inject(at = {@At("RETURN")}, method = {"animateTick"})
    private void eclipticseasons$animateTick(int x, int y, int z, CallbackInfo ci) {
        ParticleUtil.createParticle((ClientLevel)(Object)this,x,y,z);
    }

    @Inject(at = {@At("RETURN")}, method = {"addDestroyBlockEffect"})
    private void eclipticseasons$addDestroyBlockEffect(BlockPos pos, BlockState state, CallbackInfo ci) {
        ParticleUtil.attachSnowyParticle((ClientLevel)(Object)this,pos,state);
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V")}, method = {"doAnimateTick"})
    private void eclipticseasons$doAnimateTick(
            Block instance,
            BlockState pState,
            Level pLevel,
            BlockPos pPos,
            RandomSource pRandom,
            Operation<Void> original,
            @Local BlockState blockState,
            @Local(argsOnly = true, ordinal = 0) int pPosX,
            @Local(argsOnly = true, ordinal = 1) int pPosY,
            @Local(argsOnly = true, ordinal = 2) int pPosZ,
            @Local(argsOnly = true, ordinal = 3) int pRange,
            @Local(argsOnly = true) BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        boolean shouldcancel= ParticleUtil.doAnimateTick((ClientLevel) (Object) this,
                pPosX, pPosY, pPosZ,
                pRange,
                pRandom,
                blockpos$mutableblockpos,
                blockState);
        if(!shouldcancel){
            original.call(instance, pState, pLevel, pPos, pRandom);
        }
    }
}

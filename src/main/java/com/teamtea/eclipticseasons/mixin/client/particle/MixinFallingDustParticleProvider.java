package com.teamtea.eclipticseasons.mixin.client.particle;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({FallingDustParticle.Provider.class})
public abstract class MixinFallingDustParticleProvider {

    @WrapOperation(at = {
            @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;")},
            method = {"createParticle(Lnet/minecraft/core/particles/BlockParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;"})
    public MapColor eclipticseasons$getColor(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<MapColor> original) {
        var ii = MapColorReplacer.getBlockIfSnowColorAndCareLoad(blockGetter, instance, pos);
        if (ii != null)
            return ii;
        return original.call(instance, blockGetter, pos);
    }


}

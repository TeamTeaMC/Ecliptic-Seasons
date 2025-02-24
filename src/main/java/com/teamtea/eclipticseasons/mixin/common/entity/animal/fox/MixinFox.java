package com.teamtea.eclipticseasons.mixin.common.entity.animal.fox;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoxEntity.class)
public class MixinFox {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$tick(World instance, Operation<Boolean> original) {
        return WeatherManager.isThunderAt((ServerWorld) ((FoxEntity)(Object)this).level,((FoxEntity)(Object)this).blockPosition());
    }

}

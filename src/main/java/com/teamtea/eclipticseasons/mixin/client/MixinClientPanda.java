package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PandaEntity.class)
public class MixinClientPanda {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$tick(World instance, Operation<Boolean> original) {
        if (instance instanceof ClientWorld )
            return ClientWeatherChecker.isThunderAt((ClientWorld) instance, ((PandaEntity) (Object) this).blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$isScared(World instance, Operation<Boolean> original) {
        if (instance instanceof ClientWorld )
            return ClientWeatherChecker.isThunderAt((ClientWorld) instance, ((PandaEntity) (Object) this).blockPosition());
        return original.call(instance);
    }
}

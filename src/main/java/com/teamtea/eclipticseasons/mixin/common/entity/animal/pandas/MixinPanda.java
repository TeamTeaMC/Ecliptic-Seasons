package com.teamtea.eclipticseasons.mixin.common.entity.animal.pandas;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PandaEntity.class)
public class MixinPanda {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$tick(World instance, Operation<Boolean> original) {
        if (instance instanceof ServerWorld)
            return WeatherManager.isThunderAt((ServerWorld) instance, ((PandaEntity) (Object) this).blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$isScared(World instance, Operation<Boolean> original) {
        if (instance instanceof ServerWorld )
            return WeatherManager.isThunderAt((ServerWorld) instance, ((PandaEntity) (Object) this).blockPosition());
        return original.call(instance);
    }
}

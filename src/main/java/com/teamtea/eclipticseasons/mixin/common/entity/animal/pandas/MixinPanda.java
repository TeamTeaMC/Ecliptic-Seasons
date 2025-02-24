package com.teamtea.eclipticseasons.mixin.common.entity.animal.pandas;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Panda.class)
public class MixinPanda {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$tick(Level instance, Operation<Boolean> original) {
        if (instance instanceof ServerLevel serverLevel)
            return WeatherManager.isThunderAt(serverLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$isScared(Level instance, Operation<Boolean> original) {
        if (instance instanceof ServerLevel serverLevel)
            return WeatherManager.isThunderAt(serverLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }
}

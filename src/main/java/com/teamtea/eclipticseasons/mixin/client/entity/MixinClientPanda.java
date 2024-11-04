package com.teamtea.eclipticseasons.mixin.client.entity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Panda.class)
public class MixinClientPanda {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean ecliptic$tick(Level instance, Operation<Boolean> original) {
        if (instance instanceof ClientLevel clientLevel)
            if (EclipticUtil.useSolarWeather())
                return ClientWeatherChecker.isThunderAt(clientLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean ecliptic$isScared(Level instance, Operation<Boolean> original) {
        if (instance instanceof ClientLevel clientLevel)
            if (EclipticUtil.useSolarWeather())
                return ClientWeatherChecker.isThunderAt(clientLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }
}

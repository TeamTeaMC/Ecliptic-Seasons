package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.config.ServerConfig;
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
    private boolean ecliptic$Client_tick(Level instance, Operation<Boolean> original) {
        if (ServerConfig.Weather.useSolarWeather.get())
            if (instance instanceof ClientLevel clientLevel)
                return ClientWeatherChecker.isThunderAt(clientLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean ecliptic$Client_isScared(Level instance, Operation<Boolean> original) {
        if (ServerConfig.Weather.useSolarWeather.get())
            if (instance instanceof ClientLevel clientLevel)
                return ClientWeatherChecker.isThunderAt(clientLevel, ((Panda) (Object) this).blockPosition());
        return original.call(instance);
    }
}

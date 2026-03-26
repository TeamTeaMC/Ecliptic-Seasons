package com.teamtea.eclipticseasons.mixin.client.animal;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Panda.class)
public abstract class MixinClientPanda extends Animal {

    protected MixinClientPanda(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$Client_tick(Level instance, Operation<Boolean> original) {
        if (instance instanceof ClientLevel clientLevel)
            if (EclipticUtil.hasLocalWeather(clientLevel))
                return WeatherManager.isThunderAtBiome(clientLevel, blockPosition());
        return original.call(instance);
    }

    @WrapOperation(
            method = "isScared",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$Client_isScared(Level instance, Operation<Boolean> original) {
        if (instance instanceof ClientLevel clientLevel)
            if (EclipticUtil.hasLocalWeather(clientLevel))
                return WeatherManager.isThunderAtBiome(clientLevel, blockPosition());
        return original.call(instance);
    }
}

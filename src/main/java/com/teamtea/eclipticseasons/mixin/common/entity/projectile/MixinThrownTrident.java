package com.teamtea.eclipticseasons.mixin.common.entity.projectile;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public class MixinThrownTrident {


    @WrapOperation(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$isDarkEnoughToSpawn_isThundering(Level instance, Operation<Boolean> original) {
        if (EclipticUtil.hasLocalWeather(instance)) {
            return WeatherManager.isThunderAtBiome(instance, ((ThrownTrident) (Object) this).blockPosition());
        }
        return instance.isThundering();
    }
}

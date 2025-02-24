package com.teamtea.eclipticseasons.mixin.common.entity.projectile;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TridentEntity.class)
public class MixinThrownTrident {


    @WrapOperation(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$isDarkEnoughToSpawn_isThundering(World instance, Operation<Boolean> original) {
        return WeatherManager.isThunderAt((ServerWorld) ((TridentEntity)(Object)this).level, ((TridentEntity)(Object)this).blockPosition());
    }
}

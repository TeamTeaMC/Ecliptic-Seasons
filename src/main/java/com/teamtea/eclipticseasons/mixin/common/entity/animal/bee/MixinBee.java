package com.teamtea.eclipticseasons.mixin.common.entity.animal.bee;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeeEntity.class)
public class MixinBee {

    @WrapOperation(
            method = "wantsToEnterHive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isRaining()Z")
    )
    private boolean eclipticseasons$wantsToEnterHiveCheckRain(World instance, Operation<Boolean> original) {
        return WeatherUtil.isEntityInRain((BeeEntity)(Object)this);
    }

}

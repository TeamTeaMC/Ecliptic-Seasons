package com.teamtea.eclipticseasons.mixin.common.entity.animal.bee;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeeEntity.PollinateGoal.class)
public class MixinBee_BeePollinateGoal {

   @Dynamic
   @Shadow @Final private BeeEntity this$0;

    @WrapOperation(
            method = "canBeeUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isRaining()Z")
    )
    private boolean ecliptic$canBeeUseCheckRain(World instance, Operation<Boolean> original) {
        return WeatherUtil.isEntityInRain(this$0);
    }


    @WrapOperation(
            method = "canBeeContinueToUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isRaining()Z")
    )
    private boolean ecliptic$canBeeContinueToUseCheckRain(World instance, Operation<Boolean> original) {
        return WeatherUtil.isEntityInRain(this$0);
    }

}

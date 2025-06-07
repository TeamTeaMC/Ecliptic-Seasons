package com.teamtea.eclipticseasons.mixin.client.block;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightningRodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningRodBlock.class)
public class MixinLightningRodBlock {

    @WrapOperation(
            method = "animateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")
    )
    private boolean eclipticseasons$onProjectileHit_isThundering(Level instance, Operation<Boolean> original, @Local(ordinal = 0) BlockPos blockPos) {
        if (EclipticUtil.hasLocalWeather(instance))
            return ClientWeatherChecker.isThunderAt((ClientLevel) instance, blockPos);
        else return original.call(instance);
    }

}

package com.teamtea.eclipticseasons.mixin.common.entity.animal.fox;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoxEntity.FindShelterGoal.class)
public class MixinFox_SeekShelterGoal {

    @Dynamic
    @Shadow
    @Final
    private FoxEntity this$0;

    @WrapOperation(
            method = "canUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z")
    )
    private boolean eclipticseasons$canUse_Thunder(World instance, Operation<Boolean> original) {
        return WeatherManager.isThunderAt((ServerWorld) (this$0).level, this$0.blockPosition());
    }

}

package com.teamtea.eclipticseasons.mixin.common.entity.monster;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MonsterEntity.class)
public class MixinMonster {


    @WrapOperation(
            method = "isDarkEnoughToSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;isThundering()Z")
    )
    private static boolean eclipticseasons$isDarkEnoughToSpawn_isThundering(ServerWorld instance, Operation<Boolean> original, @Local(ordinal = 0) BlockPos blockPos) {
        return WeatherManager.isThunderAt(instance,blockPos);
    }
}

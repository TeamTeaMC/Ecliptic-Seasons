package com.teamtea.eclipticseasons.mixin.game;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.bee.Bee;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@SuppressWarnings("target")
@Mixin(value = {Bee.BeePollinateGoal.class})
public class MixinBee_BeePollinateGoal {

    @Shadow
    @Dynamic
    @Final
    Bee this$0;

    @Unique
    private int eclipticseasons$season_checkTime = 0;


    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/bee/Bee$BeePollinateGoal;findNearbyFlower()Ljava/util/Optional;")}, method = {"canBeeUse"})
    public Optional<BlockPos> eclipticseasons$canBeeUse(Bee.BeePollinateGoal instance, Operation<Optional<BlockPos>> original) {
        if (eclipticseasons$season_checkTime >= 0) {
            eclipticseasons$season_checkTime--;
            return Optional.empty();
        } else if (AnimalHooks.cancelBeePollinate(this$0)) {
            eclipticseasons$season_checkTime = 40;
            return Optional.empty();
        }
        return original.call(instance);
    }
}

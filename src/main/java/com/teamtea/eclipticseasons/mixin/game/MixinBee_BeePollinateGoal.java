package com.teamtea.eclipticseasons.mixin.game;


import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Bee.BeePollinateGoal.class, Bee.BeeGoToKnownFlowerGoal.class})
public class MixinBee_BeePollinateGoal {

    @Shadow
    @Final
    Bee this$0;


    @Inject(at = {@At("RETURN")}, method = {"canBeeUse"}, cancellable = true)
    public void eclipticseasons$canBeeUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (AnimalHooks.cancelBeePollinate(this$0)) {
                cir.setReturnValue(false);
            }
        }
    }
}

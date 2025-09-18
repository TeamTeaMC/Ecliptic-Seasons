package com.teamtea.eclipticseasons.mixin.game;


import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("target")
@Mixin(value = {Bee.BeeGoToKnownFlowerGoal.class})
public class MixinBee_BeeGoToKnownFlowerGoal {

    @Shadow@Dynamic
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

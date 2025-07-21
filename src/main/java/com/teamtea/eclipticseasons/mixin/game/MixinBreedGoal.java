package com.teamtea.eclipticseasons.mixin.game;


import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BreedGoal.class})
public abstract class MixinBreedGoal {
    @Shadow @Final protected Animal animal;

    @Shadow public abstract void stop();

    @Inject(at = {@At("RETURN")}, method = {"canUse"}, cancellable = true)
    public void eclipticseasons$canUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if(AnimalHooks.cancelBreed(animal)){
                stop();
                cir.setReturnValue(false);
            }
        }
    }
}

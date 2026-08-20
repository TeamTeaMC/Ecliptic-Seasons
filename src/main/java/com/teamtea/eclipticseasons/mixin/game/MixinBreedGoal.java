package com.teamtea.eclipticseasons.mixin.game;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BreedGoal.class})
public abstract class MixinBreedGoal {
    @Shadow
    @Final
    protected Animal animal;

    @Shadow
    public abstract void stop();

    @ModifyReturnValue(at = {@At("RETURN")}, method = {"canUse"})
    public boolean eclipticseasons$canUse(boolean original) {
        if (original) {
            if (AnimalHooks.cancelBreed(animal)) {
                stop();
                original = false;
            }
        }
        return original;
    }
}

package com.teamtea.eclipticseasons.mixin.game;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCow.class)
public abstract class MixinCow extends Animal {

    protected MixinCow(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyExpressionValue(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/cow/AbstractCow;isBaby()Z")}, method = {"mobInteract"})
    private boolean eclipticseasons$mobInteract(boolean original) {
        if (!AnimalHooks.cancelBreed(this)) {
            original = false;
        }
        return original;
    }
}

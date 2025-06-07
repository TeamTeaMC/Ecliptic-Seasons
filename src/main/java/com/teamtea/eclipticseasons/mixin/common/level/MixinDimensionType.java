package com.teamtea.eclipticseasons.mixin.common.level;


import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DimensionType.class})
public abstract class MixinDimensionType {
    @Inject(at = {@At("HEAD")}, method = {"timeOfDay"}, cancellable = true)
    public void eclipticseasons$getTimeOfDay(long p_63905_, CallbackInfoReturnable<Float> cir) {
        // cir.setReturnValue( AsmHandler.getSeasonCelestialAngle(p_63905_,(DimensionType)(Object)this));
    }
}

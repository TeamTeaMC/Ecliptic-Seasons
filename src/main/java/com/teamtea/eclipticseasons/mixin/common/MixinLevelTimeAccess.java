package com.teamtea.eclipticseasons.mixin.common;



import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({LevelTimeAccess.class})
public interface MixinLevelTimeAccess extends LevelTimeAccess{

    // @Shadow(remap = false) long dayTime();

    // @Inject(at = {@At("HEAD")}, method = {"getTimeOfDay"}, cancellable = true)
    // public default void mixin_getTimeOfDay(float p_46943_, CallbackInfoReturnable<Float> cir) {
    //     // cir.setReturnValue( AsmHandler.getSeasonCelestialAngle(p_63905_,(DimensionType)(Object)this));
    //
    // }

    @Override
    default float getTimeOfDay(float p_46943_) {
        return SolarAngelHelper.getSeasonCelestialAngle(this, dayTime());
    }

    // @Override
    // default int getMoonPhase() {
    //     return this.dimensionType().moonPhase(this.dayTime());
    // }
}

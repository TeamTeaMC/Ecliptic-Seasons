package com.teamtea.eclipticseasons.mixin.common;



import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import net.minecraft.world.IDayTimeReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({IDayTimeReader.class})
public interface MixinLevelTimeAccess extends IDayTimeReader {

    @Override
    default float getTimeOfDay(float p_46943_) {
        return SolarAngelHelper.getSeasonCelestialAngle((IDayTimeReader)(Object)this, dayTime());
    }

    // @Override
    // default int getMoonPhase() {
    //     return this.dimensionType().moonPhase(this.dayTime());
    // }
}

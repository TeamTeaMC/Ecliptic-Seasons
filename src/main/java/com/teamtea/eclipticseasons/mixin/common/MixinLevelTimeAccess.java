package com.teamtea.eclipticseasons.mixin.common;



import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({LevelTimeAccess.class})
public interface MixinLevelTimeAccess extends LevelTimeAccess{

    @Override
    default float getTimeOfDay(float p_46943_) {
        if (ServerConfig.Season.daylightChange.get())
            return SolarAngelHelper.getSeasonCelestialAngle(this, dayTime());
        else return this.dimensionType().timeOfDay(this.dayTime());
    }

    // @Override
    // default int getMoonPhase() {
    //     return this.dimensionType().moonPhase(this.dayTime());
    // }
}

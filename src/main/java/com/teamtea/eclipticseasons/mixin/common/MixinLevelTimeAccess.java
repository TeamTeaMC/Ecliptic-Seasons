package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({LevelTimeAccess.class})
public interface MixinLevelTimeAccess extends LevelTimeAccess {

    /**
     * @author jianzoushihu ( joe vettek)
     * @reason Ecliptic Seasons adjusts day/night cycle duration based on seasonal variations.
     */
    @Overwrite
    @Override
    default float getTimeOfDay(float p_46943_) {
        if (CommonConfig.Season.daylightChange.get()
                && this instanceof Level level
                && MapChecker.isValidDimension(level))
            return SolarAngelHelper.getSeasonCelestialAngle(this, dayTime());
        else return this.dimensionType().timeOfDay(this.dayTime());
    }

    // @Override
    // default int getMoonPhase() {
    //     return this.dimensionType().moonPhase(this.dayTime());
    // }
}

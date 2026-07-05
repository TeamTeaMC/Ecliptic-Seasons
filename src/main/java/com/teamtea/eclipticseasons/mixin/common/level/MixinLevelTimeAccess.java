package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelTimeAccess.class})
public interface MixinLevelTimeAccess extends LevelTimeAccess {

    @Shadow
    long dayTime();

    // /**
    //  * @author jianzoushihu ( joe vettek)
    //  * @reason Ecliptic Seasons adjusts day/night cycle duration based on seasonal variations.
    //  */
    // @Overwrite
    // @Override
    // default float getTimeOfDay(float p_46943_) {
    //     if (CommonConfig.Season.daylightChange.getAsBoolean()
    //             && this instanceof Level level
    //             && MapChecker.isValidDimension(level))
    //         return SolarAngelHelper.getSeasonCelestialAngle(this, dayTime());
    //     else return this.dimensionType().timeOfDay(this.dayTime());
    // }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;timeOfDay(J)F")}, method = {"getTimeOfDay"})
    default float eclipticseasons$getTimeOfDay(
            DimensionType instance,
            long dayTime,
            Operation<Float> original) {
        if (CommonConfig.Season.daylightChange.getAsBoolean()
                && this instanceof Level level
                && MapChecker.isValidDimension(level))
            return SolarAngelHelper.getSeasonCelestialAngle(this, dayTime());
        return original.call(instance, dayTime);
    }

    // @Override
    // default int getMoonPhase() {
    //     return this.dimensionType().moonPhase(this.dayTime());
    // }
}

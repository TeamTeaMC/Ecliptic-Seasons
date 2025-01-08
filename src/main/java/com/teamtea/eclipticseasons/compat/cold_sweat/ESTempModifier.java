package com.teamtea.eclipticseasons.compat.cold_sweat;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.config.ConfigSettings;
import com.momosoftworks.coldsweat.util.math.CSMath;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Function;

public class ESTempModifier extends TempModifier {
    public ESTempModifier() {
    }

    public Function<Double, Double> calculate(LivingEntity entity, Temperature.Trait trait) {
        if (MapChecker.isValidDimension(entity.level())) {
            SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(entity.level());
            double startValue = getSeasonModifier(solarTerm.ordinal());
            double endValue = getSeasonModifier(solarTerm.ordinal() + 1);

            return (temp) ->
                    temp + (double) ((float) CSMath.blend(startValue, endValue, EclipticUtil.getTimeInSolarTerm(entity.level()), 0.0, CommonConfig.Season.lastingDaysOfEachTerm.get()));
        } else {
            return (temp) -> temp;
        }
    }


    public static double getSeasonModifier(int index) {
        index = (index + 24) % 24;
        ForgeConfigSpec.ConfigValue<List<? extends Double>> listConfigValue = switch (
                index / 6) {
            case 0 -> CompatModule.CommonConfig.cold_sweat_springs;
            case 1 -> CompatModule.CommonConfig.cold_sweat_summers;
            case 2 -> CompatModule.CommonConfig.cold_sweat_autumns;
            case 3 -> CompatModule.CommonConfig.cold_sweat_winters;
            default -> throw new IllegalStateException("Unexpected value: " + index / 6);
        };
        return listConfigValue.get().get(index % 6);
    }

}

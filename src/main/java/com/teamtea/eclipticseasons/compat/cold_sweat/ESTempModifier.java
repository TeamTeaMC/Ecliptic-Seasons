package com.teamtea.eclipticseasons.compat.cold_sweat;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.config.ConfigSettings;
import com.momosoftworks.coldsweat.util.math.CSMath;
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
            var season = EclipticUtil.getNowSolarTerm(entity.level());
            double startValue;
            double endValue;

            startValue = getSeasonModifier(season.ordinal() );
            endValue = getSeasonModifier(season.ordinal() + 1);

            // switch (season.ordinal()) {
            //     case 0:
            //     case 1:
            //         startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[0];
            //         endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[1];
            //         break;
            //     case 2:
            //     case 3:
            //         startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[1];
            //         endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[2];
            //         break;
            //     case 4:
            //     case 5:
            //         startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[2];
            //         endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[0];
            //         break;
            //     case 6:
            //     case 7:
            //         startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[0];
            //         endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[1];
            //         break;
            //     case 8:
            //     case 9:
            //         startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[1];
            //         endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[2];
            //         break;
            //     case 10:
            //     case 11:
            //         startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[2];
            //         endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[0];
            //         break;
            //     case 12:
            //     case 13:
            //         startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[0];
            //         endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[1];
            //         break;
            //     case 14:
            //     case 15:
            //         startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[1];
            //         endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[2];
            //         break;
            //     case 16:
            //     case 17:
            //         startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[2];
            //         endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[0];
            //         break;
            //     case 18:
            //     case 19:
            //         startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[0];
            //         endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[1];
            //         break;
            //     case 20:
            //     case 21:
            //         startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[1];
            //         endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[2];
            //         break;
            //     case 22:
            //     case 23:
            //         startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[2];
            //         endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[0];
            //         break;
            //
            //     default:
            //         return (temp) -> temp;
            // }

            return (temp) ->
                    temp + (double) ((float) CSMath.blend(startValue, endValue, getTimeInSolarTerm(entity.level()), 0.0, CommonConfig.Season.lastingDaysOfEachTerm.get()));
        } else {
            return (temp) -> temp;
        }
    }

    public static int getTimeInSolarTerm(Level level) {
        return EclipticUtil.getNowSolarDay(level) -
                CommonConfig.Season.lastingDaysOfEachTerm.get() * EclipticUtil.getNowSolarTerm(level).ordinal();
    }


    public static double getSeasonModifier(int index) {
        index = (index + 24) % 24;
        ForgeConfigSpec.ConfigValue<List<? extends Float>> listConfigValue = switch (
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

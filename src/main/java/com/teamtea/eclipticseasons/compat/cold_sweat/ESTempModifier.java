package com.teamtea.eclipticseasons.compat.cold_sweat;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.config.ConfigSettings;
import com.momosoftworks.coldsweat.util.math.CSMath;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class ESTempModifier extends TempModifier {
    public ESTempModifier() {
    }

    public Function<Double, Double> calculate(LivingEntity entity, Temperature.Trait trait) {
        if (MapChecker.isValidDimension(entity.level())) {
            var season = SimpleUtil.getNowSolarTerm(entity.level());
            double startValue;
            double endValue;
            switch (season.ordinal()) {
                case 0:
                case 1:
                    startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[0];
                    endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[1];
                    break;
                case 2:
                case 3:
                    startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[1];
                    endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[2];
                    break;
                case 4:
                case 5:
                    startValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[2];
                    endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[0];
                    break;
                case 6:
                case 7:
                    startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[0];
                    endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[1];
                    break;
                case 8:
                case 9:
                    startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[1];
                    endValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[2];
                    break;
                case 10:
                case 11:
                    startValue = ((Double[]) ConfigSettings.SUMMER_TEMPS.get())[2];
                    endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[0];
                    break;
                case 12:
                case 13:
                    startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[0];
                    endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[1];
                    break;
                case 14:
                case 15:
                    startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[1];
                    endValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[2];
                    break;
                case 16:
                case 17:
                    startValue = ((Double[]) ConfigSettings.AUTUMN_TEMPS.get())[2];
                    endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[0];
                    break;
                case 18:
                case 19:
                    startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[0];
                    endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[1];
                    break;
                case 20:
                case 21:
                    startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[1];
                    endValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[2];
                    break;
                case 22:
                case 23:
                    startValue = ((Double[]) ConfigSettings.WINTER_TEMPS.get())[2];
                    endValue = ((Double[]) ConfigSettings.SPRING_TEMPS.get())[0];
                    break;

                default:
                    return (temp) -> temp;
            }

            return (temp) ->
                    temp + (double) ((float) CSMath.blend(startValue, endValue, getTimeInSolarTerm(entity.level()), 0.0, ServerConfig.Season.lastingDaysOfEachTerm.get()));
        } else {
            return (temp) -> temp;
        }
    }

    public static int getTimeInSolarTerm(Level level) {
        return SimpleUtil.getNowSolarDay(level) -
                ServerConfig.Season.lastingDaysOfEachTerm.get() * SimpleUtil.getNowSolarTerm(level).ordinal() ;
    }


}

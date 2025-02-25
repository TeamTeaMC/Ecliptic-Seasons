package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.RegistryObject;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;

import java.util.List;

public class LSO_ESUtil {
    public static double averageSeasonTemperature;
    // public static double averageTropicalSeasonTemperature;

    public static RegistryObject<ModifierBase> eclipticseasons$EclipticSeasons;



    public static void initAverageTemperatures() {
        averageSeasonTemperature = 0;
        List<ForgeConfigSpec.ConfigValue<List<? extends Double>>> configValueList = CompatModule.of(CompatModule.CommonConfig.legendarysurvivaloverhaul_springs,
                CompatModule.CommonConfig.legendarysurvivaloverhaul_summers,
                CompatModule.CommonConfig.legendarysurvivaloverhaul_autumns,
                CompatModule.CommonConfig.legendarysurvivaloverhaul_winters);

        for (ForgeConfigSpec.ConfigValue<List<? extends Double>> listConfigValue : configValueList) {
            for (int i = 0; i < 6; i++) {
                averageSeasonTemperature += listConfigValue.get().get(i);
            }
        }
        averageSeasonTemperature /= 24.0;
    }

}
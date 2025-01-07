package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.RegistryObject;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;

import java.util.List;

public class LSO_ESUtil {
    public static double averageSeasonTemperature;
    // public static double averageTropicalSeasonTemperature;

    public static RegistryObject<ModifierBase> ecliptic$EclipticSeasons;

    // public static Component seasonTooltip(BlockPos blockPos, Level level) {
    //     if (!hasSeasons(level)) {
    //         return Component.translatable("message.legendarysurvivaloverhaul.sereneseasons.no_season_dimension");
    //     } else {
    //         SereneSeasonsUtil.SeasonType seasonType = getSeasonType(level.getBiome(blockPos));
    //         var season = EclipticUtil.getNowSolarTerm(level);
    //
    //         // int subSeasonDuration = (int) ((double) season.getSubSeasonDuration() / (double) season.getDayDuration());
    //         int subSeasonDuration = 12;
    //         StringBuilder subSeasonName = new StringBuilder();
    //         if (seasonType == SereneSeasonsUtil.SeasonType.NO_SEASON) {
    //             return Component.translatable("message.legendarysurvivaloverhaul.sereneseasons.no_season_info");
    //         } else {
    //
    //             return ((MutableComponent) season.getTranslation()).append(", %s/%s".formatted(
    //                     getTimeInSolarTerm(level),
    //                     CommonConfig.Season.lastingDaysOfEachTerm.get()
    //             ));
    //         }
    //     }
    // }


    public static int getTimeInSolarTerm(Level level) {
        return EclipticUtil.getNowSolarDay(level) -
                CommonConfig.Season.lastingDaysOfEachTerm.get() * EclipticUtil.getNowSolarTerm(level).ordinal() + 1;
    }



    public static void initAverageTemperatures() {
        averageSeasonTemperature = 0;
        List<ForgeConfigSpec.ConfigValue<List<? extends Double>>> configValueList = List.of(CompatModule.CommonConfig.legendarysurvivaloverhaul_springs,
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
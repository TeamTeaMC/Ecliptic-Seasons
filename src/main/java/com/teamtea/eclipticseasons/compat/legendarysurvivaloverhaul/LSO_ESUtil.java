package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.RegistryObject;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.common.integration.sereneseasons.SereneSeasonsUtil;
import sfiomn.legendarysurvivaloverhaul.config.Config;

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

    public static SereneSeasonsUtil.SeasonType getSeasonType(Holder<Biome> biome) {
        // if (Config.Baked.tropicalSeasonsEnabled && biome.is(ClimateTypeBiomeTags.MONSOONAL)) {
        //     return SereneSeasonsUtil.SeasonType.TROPICAL_SEASON;
        // } else
        {
            return
                    // !Config.Baked.defaultSeasonEnabled &&
                    biome.is(ClimateTypeBiomeTags.RAINLESS) ?
                            SereneSeasonsUtil.SeasonType.NO_SEASON : SereneSeasonsUtil.SeasonType.NORMAL_SEASON;
        }
    }


    public static boolean hasSeasons(Level level) {
        return MapChecker.isValidDimension(level);
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

        // averageTropicalSeasonTemperature += Config.Baked.earlyWetSeasonModifier;
        // averageTropicalSeasonTemperature += Config.Baked.earlyDrySeasonModifier;
        // averageTropicalSeasonTemperature += Config.Baked.midWetSeasonModifier;
        // averageTropicalSeasonTemperature += Config.Baked.midDrySeasonModifier;
        // averageTropicalSeasonTemperature += Config.Baked.lateWetSeasonModifier;
        // averageTropicalSeasonTemperature += Config.Baked.lateDrySeasonModifier;
        // averageTropicalSeasonTemperature /= 6.0;
    }

}
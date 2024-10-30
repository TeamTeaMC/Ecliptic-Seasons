package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.common.integration.sereneseasons.SereneSeasonsUtil;
import sfiomn.legendarysurvivaloverhaul.config.Config;

public class LSO_ESUtil {
    public static double averageSeasonTemperature;
    public static double averageTropicalSeasonTemperature;

    public static RegistryObject<ModifierBase> ecliptic$EclipticSeasons;

    public static Component seasonTooltip(BlockPos blockPos, Level level) {
        if (!hasSeasons(level)) {
            return Component.translatable("message.legendarysurvivaloverhaul.sereneseasons.no_season_dimension");
        } else {
            SereneSeasonsUtil.SeasonType seasonType = getSeasonType(level.getBiome(blockPos));
            var season = SimpleUtil.getNowSolarTerm(level);

            // int subSeasonDuration = (int) ((double) season.getSubSeasonDuration() / (double) season.getDayDuration());
            int subSeasonDuration = 12;
            StringBuilder subSeasonName = new StringBuilder();
            if (seasonType == SereneSeasonsUtil.SeasonType.NO_SEASON) {
                return Component.translatable("message.legendarysurvivaloverhaul.sereneseasons.no_season_info");
            } else {

                return ((MutableComponent) season.getTranslation()).append(", %s/%s".formatted(
                        getTimeInSolarTerm(level),
                        ServerConfig.Season.lastingDaysOfEachTerm.get()
                ));
            }
        }
    }


    public static int getTimeInSolarTerm(Level level) {
        return SimpleUtil.getNowSolarDay(level) -
                ServerConfig.Season.lastingDaysOfEachTerm.get() * SimpleUtil.getNowSolarTerm(level).ordinal()+1;
    }

    public static SereneSeasonsUtil.SeasonType getSeasonType(Holder<Biome> biome) {
        if (Config.Baked.tropicalSeasonsEnabled && biome.is(SeasonTypeBiomeTags.MONSOONAL)) {
            return SereneSeasonsUtil.SeasonType.TROPICAL_SEASON;
        } else {
            return !Config.Baked.defaultSeasonEnabled &&
                    biome.is(SeasonTypeBiomeTags.RAINLESS) ?
                    SereneSeasonsUtil.SeasonType.NO_SEASON : SereneSeasonsUtil.SeasonType.NORMAL_SEASON;
        }
    }


    public static boolean hasSeasons(Level level) {
        return MapChecker.isValidDimension(level);
    }

    public static void initAverageTemperatures() {
        averageSeasonTemperature += Config.Baked.earlyAutumnModifier;
        averageSeasonTemperature += Config.Baked.earlySpringModifier;
        averageSeasonTemperature += Config.Baked.earlySummerModifier;
        averageSeasonTemperature += Config.Baked.earlyWinterModifier;
        averageSeasonTemperature += Config.Baked.midAutumnModifier;
        averageSeasonTemperature += Config.Baked.midSpringModifier;
        averageSeasonTemperature += Config.Baked.midSummerModifier;
        averageSeasonTemperature += Config.Baked.midWinterModifier;
        averageSeasonTemperature += Config.Baked.lateAutumnModifier;
        averageSeasonTemperature += Config.Baked.lateSpringModifier;
        averageSeasonTemperature += Config.Baked.lateSummerModifier;
        averageSeasonTemperature += Config.Baked.lateWinterModifier;
        averageSeasonTemperature /= 12.0;
        averageTropicalSeasonTemperature += Config.Baked.earlyWetSeasonModifier;
        averageTropicalSeasonTemperature += Config.Baked.earlyDrySeasonModifier;
        averageTropicalSeasonTemperature += Config.Baked.midWetSeasonModifier;
        averageTropicalSeasonTemperature += Config.Baked.midDrySeasonModifier;
        averageTropicalSeasonTemperature += Config.Baked.lateWetSeasonModifier;
        averageTropicalSeasonTemperature += Config.Baked.lateDrySeasonModifier;
        averageTropicalSeasonTemperature /= 6.0;
    }

}
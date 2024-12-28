package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.common.integration.sereneseasons.SereneSeasonsUtil;
import sfiomn.legendarysurvivaloverhaul.config.Config;
import sfiomn.legendarysurvivaloverhaul.util.MathUtil;

public class LSO_ESModifier extends ModifierBase {

    public LSO_ESModifier() {
    }

    public float getWorldInfluence(Player player, Level level, BlockPos pos) {
        if (!Config.Baked.seasonTemperatureEffects) {
            return 0.0F;
        } else {
            try {
                return this.getUncaughtWorldInfluence(level, pos);
            } catch (Exception var5) {
                LegendarySurvivalOverhaul.LOGGER.error("An error has occurred with Serene Seasons compatibility, disabling modifier", var5);
                LegendarySurvivalOverhaul.sereneSeasonsLoaded = false;
                return 0.0F;
            }
        }
    }

    public float getUncaughtWorldInfluence(Level level, BlockPos pos) {
        SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
        if (nowSolarTerm != SolarTerm.NONE && LSO_ESUtil.hasSeasons(level)) {
            Vec3i[] posOffsets;
            if (Config.Baked.tropicalSeasonsEnabled) {
                posOffsets = new Vec3i[]{new Vec3i(0, 0, 0), new Vec3i(10, 0, 0), new Vec3i(-10, 0, 0), new Vec3i(0, 0, 10), new Vec3i(0, 0, -10)};
            } else {
                posOffsets = new Vec3i[]{new Vec3i(0, 0, 0)};
            }

            float value = 0.0F;
            int validSpot = posOffsets.length;
            double targetUndergroundTemperature = 0.0;
            Vec3i[] var9 = posOffsets;
            int var10 = posOffsets.length;

            for (int var11 = 0; var11 < var10; ++var11) {
                Vec3i offset = var9[var11];
                SereneSeasonsUtil.SeasonType seasonType = LSO_ESUtil.getSeasonType(level.getBiome(pos.offset(offset)));
                if (seasonType == SereneSeasonsUtil.SeasonType.NO_SEASON) {
                    --validSpot;
                } else {
                    int timeInSubSeason;
                    if (seasonType != SereneSeasonsUtil.SeasonType.TROPICAL_SEASON) {
                        timeInSubSeason = LSO_ESUtil.getTimeInSolarTerm(level);
                        timeInSubSeason += nowSolarTerm.ordinal() % 2 == 0 ? 0 : ServerConfig.Season.lastingDaysOfEachTerm.get();
                        targetUndergroundTemperature = LSO_ESUtil.averageSeasonTemperature;
                        switch (nowSolarTerm.ordinal()) {
                            case 0:
                            case 1:
                                value += this.getSeasonModifier(Config.Baked.lateWinterModifier, Config.Baked.earlySpringModifier, Config.Baked.midSpringModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 2:
                            case 3:
                                value += this.getSeasonModifier(Config.Baked.earlySpringModifier, Config.Baked.midSpringModifier, Config.Baked.lateSpringModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 4:
                            case 5:
                                value += this.getSeasonModifier(Config.Baked.midSpringModifier, Config.Baked.lateSpringModifier, Config.Baked.earlySummerModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 6:
                            case 7:
                                value += this.getSeasonModifier(Config.Baked.lateSpringModifier, Config.Baked.earlySummerModifier, Config.Baked.midSummerModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 8:
                            case 9:
                                value += this.getSeasonModifier(Config.Baked.earlySummerModifier, Config.Baked.midSummerModifier, Config.Baked.lateSummerModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 10:
                            case 11:
                                value += this.getSeasonModifier(Config.Baked.midSummerModifier, Config.Baked.lateSummerModifier, Config.Baked.earlyAutumnModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 12:
                            case 13:
                                value += this.getSeasonModifier(Config.Baked.lateSummerModifier, Config.Baked.earlyAutumnModifier, Config.Baked.midAutumnModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 14:
                            case 15:
                                value += this.getSeasonModifier(Config.Baked.earlyAutumnModifier, Config.Baked.midAutumnModifier, Config.Baked.lateAutumnModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 16:
                            case 17:
                                value += this.getSeasonModifier(Config.Baked.midAutumnModifier, Config.Baked.lateAutumnModifier, Config.Baked.earlyWinterModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 18:
                            case 19:
                                value += this.getSeasonModifier(Config.Baked.lateAutumnModifier, Config.Baked.earlyWinterModifier, Config.Baked.midWinterModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 20:
                            case 21:
                                value += this.getSeasonModifier(Config.Baked.earlyWinterModifier, Config.Baked.midWinterModifier, Config.Baked.lateWinterModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 22:
                            case 23:
                                value += this.getSeasonModifier(Config.Baked.midWinterModifier, Config.Baked.lateWinterModifier, Config.Baked.earlySpringModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                        }
                    } else {
                        timeInSubSeason = LSO_ESUtil.getTimeInSolarTerm(level);
                        timeInSubSeason += ServerConfig.Season.lastingDaysOfEachTerm.get() * (nowSolarTerm.ordinal() % 4);
                        targetUndergroundTemperature = LSO_ESUtil.averageTropicalSeasonTemperature;
                        int ordinal = nowSolarTerm.ordinal() + 6;
                        ordinal -= ordinal > 23 ? 24 : 0;
                        switch (ordinal) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                value += this.getSeasonModifier(Config.Baked.lateWetSeasonModifier, Config.Baked.earlyDrySeasonModifier, Config.Baked.midDrySeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                                value += this.getSeasonModifier(Config.Baked.earlyDrySeasonModifier, Config.Baked.midDrySeasonModifier, Config.Baked.lateDrySeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                                value += this.getSeasonModifier(Config.Baked.midDrySeasonModifier, Config.Baked.lateDrySeasonModifier, Config.Baked.earlyWetSeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                                value += this.getSeasonModifier(Config.Baked.lateDrySeasonModifier, Config.Baked.earlyWetSeasonModifier, Config.Baked.midWetSeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                                value += this.getSeasonModifier(Config.Baked.earlyWetSeasonModifier, Config.Baked.midWetSeasonModifier, Config.Baked.lateWetSeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                                break;
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                                value += this.getSeasonModifier(Config.Baked.midWetSeasonModifier, Config.Baked.lateWetSeasonModifier, Config.Baked.earlyDrySeasonModifier, timeInSubSeason, ServerConfig.Season.lastingDaysOfEachTerm.get());
                        }
                    }
                }
            }

            value = validSpot == 0 ? 0.0F : value / (float) validSpot;
            return this.applyUndergroundEffect(value, level, pos, (float) targetUndergroundTemperature);
        } else {
            return 0.0F;
        }
    }

    private float getSeasonModifier(double previousSeasonModifier, double currentSeasonModifier, double nextSeasonModifier, int time, int subSeasonDuration) {
        return time < subSeasonDuration / 2 ? this.calculateSinusoidalBetweenSeasons(previousSeasonModifier, currentSeasonModifier, time + subSeasonDuration / 2, subSeasonDuration) : this.calculateSinusoidalBetweenSeasons(currentSeasonModifier, nextSeasonModifier, time - subSeasonDuration / 2, subSeasonDuration);
    }

    private float calculateSinusoidalBetweenSeasons(double previousSeasonModifier, double nextSeasonModifier, int time, int subSeasonDuration) {
        double tempDiff = nextSeasonModifier - previousSeasonModifier;
        double seasonModifier = (Math.sin((double) time * Math.PI / (double) subSeasonDuration - 1.5707963267948966) + 1.0) * (tempDiff / 2.0) + previousSeasonModifier;
        return MathUtil.round((float) seasonModifier, 2);
    }
}

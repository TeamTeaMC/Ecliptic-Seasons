package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeConfigSpec;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.util.MathUtil;

import java.util.List;

public class LSO_ESModifier extends ModifierBase {

    public LSO_ESModifier() {
    }

    public float getWorldInfluence(PlayerEntity player, World world, BlockPos pos) {
        // if (!Config.Baked.seasonTemperatureEffects) {
        //     return 0.0F;
        // } else
        {
            try {
                return this.getUncaughtWorldInfluence(world, pos);
            } catch (Exception var5) {
                // LegendarySurvivalOverhaul.LOGGER.error("An error has occurred with Serene Seasons compatibility, disabling modifier", var5);
                return 0.0F;
            }
        }
    }

    public float getUncaughtWorldInfluence(World level, BlockPos pos) {
        SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
        if (nowSolarTerm != SolarTerm.NONE && MapChecker.isValidDimension(level)) {
            int timeInSubSeason = EclipticUtil.getTimeInSolarTerm(level);
            double targetUndergroundTemperature = LSO_ESUtil.averageSeasonTemperature;
            int ordinal = nowSolarTerm.ordinal();
            float value = this.getSeasonModifier(getSeasonModifier(ordinal - 1), getSeasonModifier(ordinal), getSeasonModifier(ordinal + 1), timeInSubSeason, CommonConfig.Season.lastingDaysOfEachTerm.get());
            return this.applyUndergroundEffect((float) (value-targetUndergroundTemperature), level, pos);
        } else {
            return 0.0F;
        }
    }

    private float getSeasonModifier(double previousSeasonModifier, double currentSeasonModifier, double nextSeasonModifier, int time, int subSeasonDuration) {
        return time < subSeasonDuration / 2 ? this.calculateSinusoidalBetweenSeasons(previousSeasonModifier, currentSeasonModifier, time + subSeasonDuration / 2, subSeasonDuration) : this.calculateSinusoidalBetweenSeasons(currentSeasonModifier, nextSeasonModifier, time - subSeasonDuration / 2, subSeasonDuration);
    }

    public static double getSeasonModifier(int index) {
        index = (index + 24) % 24;

        ForgeConfigSpec.ConfigValue<List<? extends Double>> listConfigValue;
        switch (index / 6) {
            case 0:
                listConfigValue = CompatModule.CommonConfig.legendarysurvivaloverhaul_springs;
                break;
            case 1:
                listConfigValue = CompatModule.CommonConfig.legendarysurvivaloverhaul_summers;
                break;
            case 2:
                listConfigValue = CompatModule.CommonConfig.legendarysurvivaloverhaul_autumns;
                break;
            case 3:
                listConfigValue = CompatModule.CommonConfig.legendarysurvivaloverhaul_winters;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + index / 6);
        }
        return listConfigValue.get().get(index % 6);
    }

    private float calculateSinusoidalBetweenSeasons(double previousSeasonModifier, double nextSeasonModifier, int time, int subSeasonDuration) {
        double tempDiff = nextSeasonModifier - previousSeasonModifier;
        double seasonModifier = (Math.sin((double) time * Math.PI / (double) subSeasonDuration - 1.5707963267948966) + 1.0) * (tempDiff / 2.0) + previousSeasonModifier;
        return MathUtil.round((float) seasonModifier, 2);
    }
}

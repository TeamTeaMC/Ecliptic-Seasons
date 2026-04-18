package com.teamtea.eclipticseasons.common.core.solar;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Map;

@ApiStatus.Experimental
public class SolarTermHelper {

    private static Map<SolarTerm, Holder<SeasonPhase>> getSolarTermHolderMap(Biome biome, boolean isServer) {
        return (isServer ? BiomeClimateManager.SEASON_PHASE_MAP : BiomeClimateManager.CLIENT_SEASON_PHASE_MAP)
                .get(biome);
    }

    public static @NonNull ISolarTerm get(Level level, BlockPos pos) {
        SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
        Biome biome = CropGrowthHandler.getCropBiome(level, pos).value();
        Map<SolarTerm, Holder<SeasonPhase>> solarTermHolderMap = getSolarTermHolderMap(biome, level instanceof ServerLevel);
        return get(solarTermHolderMap, nowSolarTerm);
    }


    public static @NonNull ISolarTerm get(Level level, BlockPos pos, SolarTerm nowSolarTerm) {
        Biome biome = CropGrowthHandler.getCropBiome(level, pos).value();
        Map<SolarTerm, Holder<SeasonPhase>> solarTermHolderMap = getSolarTermHolderMap(biome, level instanceof ServerLevel);
        return get(solarTermHolderMap, nowSolarTerm);
    }

    public static @NonNull ISolarTerm get(Holder<Biome> biome, SolarTerm nowSolarTerm) {
        Map<SolarTerm, Holder<SeasonPhase>> solarTermHolderMap = getSolarTermHolderMap(biome.value(), BiomeClimateManager.isServerInstance(biome.value()));
        return get(solarTermHolderMap, nowSolarTerm);
    }

    public static @NonNull ISolarTerm get(Map<SolarTerm, Holder<SeasonPhase>> solarTermHolderMap, SolarTerm nowSolarTerm) {
        return CommonConfig.Season.enableLocalInfoCalendar.get() && solarTermHolderMap != null && solarTermHolderMap.containsKey(nowSolarTerm) ?
                solarTermHolderMap.get(nowSolarTerm).value() :
                nowSolarTerm;
    }

    public static @NonNull ISolarTerm getNext(Holder<Biome> biome, SolarTerm nowSolarTerm) {
        return getNextTermAndStart(biome, nowSolarTerm).getSecond();
    }

    public static SolarTerm getNextStartTerm(Holder<Biome> biome, SolarTerm nowSolarTerm) {
        return getNextTermAndStart(biome, nowSolarTerm).getFirst();
    }

    public static Pair<SolarTerm, ISolarTerm> getNextTermAndStart(Holder<Biome> biome, SolarTerm nowSolarTerm) {
        Map<SolarTerm, Holder<SeasonPhase>> solarTermHolderMap = getSolarTermHolderMap(biome.value(), BiomeClimateManager.isServerInstance(biome.value()));
        ISolarTerm iSolarTerm = get(solarTermHolderMap, nowSolarTerm);
        SolarTerm solarTermNext = nowSolarTerm.getNextSolarTerm();
        ISolarTerm iSolarTermNext = get(solarTermHolderMap, solarTermNext);
        int count = 0;
        while (count < 24 && iSolarTermNext == iSolarTerm) {
            solarTermNext = solarTermNext.getNextSolarTerm();
            iSolarTermNext = get(solarTermHolderMap, solarTermNext);
            count++;
        }
        return Pair.of(solarTermNext, iSolarTermNext);
    }

    public static boolean isChanged(Level level, BlockPos pos, SolarTerm solarTerm, SolarTerm solarTermLast) {
        var biome = CropGrowthHandler.getCropBiome(level, pos);
        return get(biome, solarTerm) == get(biome, solarTermLast);
    }

    public static @Nullable ISolarTerm isChangedAndGet(Level level, BlockPos pos, SolarTerm solarTerm, SolarTerm solarTermLast, boolean ignoreChangeCheck) {
        var biome = CropGrowthHandler.getCropBiome(level, pos);
        ISolarTerm iSolarTerm = get(biome, solarTerm);
        if (ignoreChangeCheck || iSolarTerm != get(biome, solarTermLast))
            return iSolarTerm;
        return null;
    }
}

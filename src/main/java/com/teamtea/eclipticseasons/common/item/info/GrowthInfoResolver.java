package com.teamtea.eclipticseasons.common.item.info;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientExtraUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.item.GrowthDetectorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

public final class GrowthInfoResolver {

    public static GrowthInfo resolve(Level level, BlockPos pos, BlockState state) {
        Map<Holder<AgroClimaticZone>, CropGrowControl> controlMap =
                CropGrowthHandler.getControlMap(state.getBlock());

        if (controlMap == null) {
            return null;
        }

        Holder<Biome> biomeHolder = CropGrowthHandler.getCropBiome(level, pos);
        Holder<AgroClimaticZone> climateHolder =
                CropGrowthHandler.getclimateTypeHolder(biomeHolder);

        Holder<AgroClimaticZone> fallback =
                CropGrowthHandler.getDefaultAgroClimaticZoneHolder(level);

        CropGrowControl growControl =
                CropGrowthHandler.getCropGrowControl(controlMap, climateHolder);

        if (growControl == null) {
            growControl = CropGrowthHandler.getCropGrowControl(controlMap, fallback);
        }

        if (growControl == null) {
            return null;
        }

        float greenhouseChance = 0;
        for (int i = 0; i < 100; i++) {
            greenhouseChance += CropGrowthHandler.isInRoom(
                    level,
                    pos,
                    state,
                    growControl.notGreenHouse()
            ) ? 1 : 0;
        }

        int greenhouseLevel = greenhouseChance > 50 ? 1 : greenhouseChance > 10 ? 2 : 3;
        boolean greenhouse = greenhouseLevel == 1;

        float growChance = 0;
        for (int i = 0; i < 100; i++) {
            growChance += GrowthDetectorItem.getGrowChance(level, pos, state);
        }

        int growChanceLevel =
                growChance > 80f ? 1 :
                        growChance > 60f ? 2 :
                                growChance > 40f ? 3 :
                                        growChance > 20f ? 4 :
                                                growChance > 0f ? 5 : 6;

        boolean needsSeasonCore = false;
        boolean humidityMismatch = false;

        List<Season> likedSeasons = List.of();
        List<Humidity> likedHumidity = List.of();

        if (growChance <= 40) {
            likedSeasons = CropGrowthHandler.getLikeSeasonsInTemperate(
                    state,
                    controlMap,
                    fallback
            );

            SolarDataManager saveData = SolarHolders.getSaveData(level);
            if (!likedSeasons.isEmpty()
                    && saveData != null
                    && saveData.findNearGreenHouseProvider(pos, likedSeasons) == null) {
                needsSeasonCore = !likedSeasons.contains(EclipticSeasonsApi.getInstance().getSeasonSignal(level, pos));
            }

            likedHumidity = CropGrowthHandler.getLikeHumidityInTemperate(
                    state,
                    controlMap,
                    fallback
            );

            if (!likedHumidity.isEmpty()) {
                Humidity currentHumidity = level instanceof ServerLevel serverLevel
                        ? EclipticSeasonsApi.getInstance().getAdjustedHumidity(serverLevel, pos)
                        : Humidity.getHumid(ClientExtraUtil.modifyHumidity(level, pos, EclipticUtil.getHumidityLevelAt(level, pos)
                ));

                humidityMismatch = !likedHumidity.contains(currentHumidity);
            }
        }

        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        float humidity = EclipticUtil.getHumidityLevelAt(
                level,
                solarTerm,
                biomeHolder,
                pos,
                true
        );

        return new GrowthInfo(
                pos,
                state,
                state.getBlock().getName(),
                climateHolder,
                greenhouse,
                greenhouseLevel,
                growChance,
                growChanceLevel,
                needsSeasonCore,
                humidityMismatch,
                humidity,
                likedSeasons,
                likedHumidity
        );
    }

    private GrowthInfoResolver() {
    }
}
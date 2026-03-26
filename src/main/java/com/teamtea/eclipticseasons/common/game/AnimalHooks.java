package com.teamtea.eclipticseasons.common.game;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.game.BreedSeasonType;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.AnimalBehaviorTag;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class AnimalHooks {

    public static Season findCurrentSeason(List<Pair<Season, Integer>> localSeason, int index, Season defaultGet) {
        if (localSeason.isEmpty()) return defaultGet;
        if (localSeason.size() == 1) return localSeason.get(0).getFirst();
        int accumulatedLength = 0;
        for (int i = 0; i < localSeason.size(); i++) {
            Season season = localSeason.get(i).getFirst();
            int seasonLength = localSeason.get(i).getSecond();
            if (index < accumulatedLength + seasonLength) {
                return season;
            }
            accumulatedLength += seasonLength;
        }
        return defaultGet;
    }

    public static Season getUseSeason(Level level, Entity entity) {
        BlockPos pos = entity.blockPosition();
        return getUseSeason(level, pos);
    }

    public static Season getUseSeason(Level level, BlockPos pos) {
        SolarTerm nowSolarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);

        Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(level, pos);
        Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(cropBiome);
        if (agroClimaticZoneHolder != null) {
            List<Pair<Season, Integer>> pairs = agroClimaticZoneHolder.value().seasonalSignalDurations();
            return findCurrentSeason(pairs, nowSolarTerm.ordinal(), nowSolarTerm.getSeason());
        }

        return nowSolarTerm.getSeason();
    }

    public static boolean cancelBreed(Animal animal) {
        if (!CommonConfig.Animal.enableBreed.get()) return false;

        Season season = getUseSeason(animal.level(), animal);

        BreedSeasonType breedSeasonType = null;
        for (BreedSeasonType seasonType : BreedSeasonType.values()) {
            if (animal.getType().builtInRegistryHolder().is(seasonType.getTag())) {
                breedSeasonType = seasonType;
                break;
            }
        }
        if (breedSeasonType != null) {
            List<Season> seasons = new ArrayList<>();
            for (Season collectValidValue : Season.collectValidValues()) {
                if (breedSeasonType.getInfo().isSuitable(collectValidValue))
                    seasons.add(collectValidValue);
            }
            if (!breedSeasonType.getInfo().isSuitable(season)) {
                if (!CommonConfig.Animal.enableCoreWork.get() || withoutSeasonBonus(animal.level(), animal.blockPosition(), seasons))
                    return true;
            } else {
                if (CommonConfig.Animal.enableCoreWork.get()) {
                    withoutSeasonBonus(animal.level(), animal.blockPosition(), seasons);
                }
            }

            if (CommonConfig.Animal.enableTimeBreed.get()) {
                boolean isDay = EclipticSeasonsApi.getInstance().isDay(animal.level());
                if (animal.getType().builtInRegistryHolder().is(AnimalBehaviorTag.DAY)) {
                    return !isDay;
                } else if (animal.getType().builtInRegistryHolder().is(AnimalBehaviorTag.NIGHT)) {
                    return isDay;
                } else if (animal.getType().builtInRegistryHolder().is(AnimalBehaviorTag.ALL_TIME)) {
                    return false;
                } else return !isDay;
            } else return false;
        }
        return false;
    }


    public static boolean withoutSeasonBonus(Level level, BlockPos pos, List<Season> seasons) {
        if (!seasons.isEmpty()) {
            SolarDataManager saveData = SolarHolders.getSaveData(level);
            return saveData != null && saveData.findNearGreenHouseProvider(pos, seasons) == null;
        } else {
            return true;
        }
    }


    public static boolean cancelBeePollinate(Bee bee) {
        if (!CommonConfig.Animal.enableBee.get()) return false;
        List<Season> seasons = CommonConfig.castSeasonList(CommonConfig.Animal.beePollinateSeasons.get());
        Season season = getUseSeason(bee.level(), bee);
        return !seasons.contains(season)
                && (!CommonConfig.Animal.enableCoreWork.get() || withoutSeasonBonus(bee.level(), bee.blockPosition(), seasons));
    }

    public static boolean cancelBeeOut(Level level, BlockPos blockPos) {
        if (!CommonConfig.Animal.enableBee.get()) return false;
        List<Season> seasons = CommonConfig.castSeasonList(CommonConfig.Animal.beeActiveSeasons.get());
        Season season = getUseSeason(level, blockPos);
        if (!seasons.contains(season)) {
            if (EclipticSeasonsApi.getInstance().getPrecipitationAt(level, blockPos) == Biome.Precipitation.SNOW) {
                return !CommonConfig.Animal.enableCoreWork.get() || withoutSeasonBonus(level, blockPos, seasons);
            }
        }
        List<Season> seasons2 = CommonConfig.castSeasonList(CommonConfig.Animal.beePollinateSeasons.get());

        return (!seasons2.contains(season) && (level.getRandom().nextBoolean()
                || (!CommonConfig.Animal.enableCoreWork.get() || withoutSeasonBonus(level, blockPos, seasons2))));
    }

    public static List<Component> getBreedInfo(LivingEntity entity) {
        return entity == null ? List.of() : getBreedInfo(entity.getType());
    }

    public static List<Component> getBreedInfo(EntityType<?> entity) {
        if (!CommonConfig.Animal.enableBreed.get()) return List.of();

        BreedSeasonType breedSeasonType = null;
        for (BreedSeasonType seasonType : BreedSeasonType.values()) {
            if (entity.builtInRegistryHolder().is(seasonType.getTag())) {
                breedSeasonType = seasonType;
                break;
            }
        }

        if (breedSeasonType != null) {
            return breedSeasonType.getInfo().getTooltip();
        }
        return List.of();
    }
}

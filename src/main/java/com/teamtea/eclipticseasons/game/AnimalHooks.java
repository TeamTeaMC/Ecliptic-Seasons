package com.teamtea.eclipticseasons.game;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.game.BreedSeasonType;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.AnimalBehaviorTag;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

public class AnimalHooks {
    public static boolean cancelBreed(Animal animal) {
        if(!ServerConfig.Animal.enableBreed.get())return false;

        Season season = EclipticSeasonsApi.getInstance().getSolarTerm(animal.level()).getSeason();

        BreedSeasonType breedSeasonType = null;
        for (BreedSeasonType seasonType : BreedSeasonType.values()) {
            if (animal.getType().is(seasonType.getTag())) {
                breedSeasonType = seasonType;
                break;
            }
        }

        if (breedSeasonType != null) {
            if (!breedSeasonType.getInfo().isSuitable(season)) {
                return true;
            }

            boolean isDay = EclipticSeasonsApi.getInstance().isDay(animal.level());

            if (animal.getType().is(AnimalBehaviorTag.DAY)) {
                return !isDay;
            } else if (animal.getType().is(AnimalBehaviorTag.NIGHT)) {
                return isDay;
            }else if (animal.getType().is(AnimalBehaviorTag.ALL_TIME)) {
                return false;
            } else return !isDay;


        }

        return season != Season.SPRING && season != Season.SUMMER;

        // if (animal instanceof Cow
        //         || animal instanceof Sheep
        //         || animal instanceof Pig
        //         || animal instanceof Horse
        //         || animal instanceof Donkey
        //         || animal instanceof Panda
        //         || animal instanceof Turtle
        //         || animal instanceof Llama
        // ) {
        //     return season != Season.SPRING && season != Season.SUMMER;
        // } else if (animal instanceof Wolf
        //         || animal instanceof Bee) {
        //     return season != Season.SPRING;
        // } else if (animal instanceof Cat
        //         || animal instanceof Ocelot
        //         || animal instanceof Rabbit) {
        //     return season != Season.SPRING && season != Season.AUTUMN;
        // } else if (animal instanceof Fox) {
        //     return season != Season.WINTER;
        // } else if (animal instanceof Camel) {
        //     return season != Season.SUMMER && season != Season.AUTUMN;
        // } else if (animal instanceof Strider) {
        //     return false;
        // } else if (animal instanceof Frog) {
        //     return (season != Season.SPRING && season != Season.SUMMER)
        //             || EclipticSeasonsApi.getInstance().isDay(animal.level());
        // }
        //
        // if (EclipticSeasonsApi.getInstance().isNight(animal.level())) {
        //     return true;
        // // }
        // return false;
    }

    public static boolean cancelBeePollinate(Bee bee) {
        if(!ServerConfig.Animal.enableBee.get())return false;

        Season season = EclipticSeasonsApi.getInstance().getSolarTerm(bee.level()).getSeason();
        return season != Season.SPRING;
    }

    public static boolean cancelBeeOut(Level level, BlockPos blockPos) {
        if(!ServerConfig.Animal.enableBee.get())return false;

        Season season = EclipticSeasonsApi.getInstance().getSolarTerm(level).getSeason();
        if (season == Season.WINTER) {
            if (level.getBiome(blockPos).value().getTemperature(blockPos) < 0.2f) {
                return false;
            }
        }
        return season != Season.SPRING && level.getRandom().nextBoolean();
    }
}

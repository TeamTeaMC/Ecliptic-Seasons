package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

public class SoundControllers {

    public static final List<SoundEvent> EMPTY = new ArrayList<>();


    public static interface ISoundController {

        default boolean isValidIndoor() {
            return false;
        }

        default boolean isValidInDay() {
            return true;
        }

        boolean isValidInSolarTerm(SolarTerm solarTerm);

        default boolean isValidTime(SolarTerm solarTerm, boolean isDay, boolean inDoor) {
            return inDoor == this.isValidIndoor() && isDay == this.isValidInDay() && this.isValidInSolarTerm(solarTerm);
        }


        List<SoundEvent> getSoundEvents(Level level, Player player, Holder<Biome> biome, SolarTerm solarTerm, boolean isDay, boolean inDoor);

    }


    public static class SpringSoundController implements ISoundController {

        @Override
        public boolean isValidInSolarTerm(SolarTerm solarTerm) {
            return solarTerm.getSeason() == Season.SPRING;
        }

        @Override
        public List<SoundEvent> getSoundEvents(Level level, Player player, Holder<Biome> biome, SolarTerm solarTerm, boolean isDay, boolean inDoor) {
            if (solarTerm.getSeason() == Season.SPRING) {
                if (player.isInWaterOrRain()) {
                    if ((biome.is(Biomes.CHERRY_GROVE)
                            || biome.is(BiomeTags.IS_FOREST)
                            || biome.is(Tags.Biomes.IS_PLAINS))
                            && !biome.is(Tags.Biomes.IS_COLD)) {
                        return Stream.of(EclipticSeasons.SoundEventsRegistry.spring_forest).toList();
                    }
                }
            }
            return SoundControllers.EMPTY;
        }
    }
}

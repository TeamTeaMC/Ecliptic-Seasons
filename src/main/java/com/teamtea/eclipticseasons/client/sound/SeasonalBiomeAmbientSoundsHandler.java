package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.*;

public class SeasonalBiomeAmbientSoundsHandler implements AmbientSoundHandler {
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private final BiomeManager biomeManager;
    private final RandomSource random;
    // private final Map<Biome, LoopSoundInstance> loopSounds = new HashMap<>();

    @Nullable
    private Biome previousBiome;
    private Season previousSeason;
    private boolean previousIsDay;

    // private final List<SimplePair<Biome, LoopSeasonalSoundInstance>> loopSoundList = new ArrayList<>();
    private final Set<LoopSeasonalSoundInstance> loopSounds = new HashSet<>();

    public SeasonalBiomeAmbientSoundsHandler(LocalPlayer localPlayer, SoundManager soundManager, BiomeManager biomeManager) {
        this.random = localPlayer.level().getRandom();
        this.player = localPlayer;
        this.soundManager = soundManager;
        this.biomeManager = biomeManager;
    }

    public void tick() {

        // this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);

        // loopSoundList.removeIf(pair -> pair.getValue().isStopped());
        loopSounds.removeIf(AbstractTickableSoundInstance::isStopped);


        boolean indoor = isIndoor();
        // EclipticSeasons.logger((player.level().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition())));

        Holder<Biome> biome = getBiome();

        SolarTerm solarTerm = ClientCon.nowSolarTerm;
        Season season = ClientCon.nowSeason;
        boolean isDayNow = ClientCon.isDay;
        if (biome.value() != this.previousBiome) {
            this.previousBiome = biome.value();
        }
        if (season != this.previousSeason || isDayNow != this.previousIsDay) {
            this.previousSeason = season;
            this.previousIsDay = isDayNow;
        }
        {
            SoundEvent soundEvent = null;
            Level level = player.level();
            if (MapChecker.isValidDimension(level)) {

                boolean raining = level.isRaining();
                TimePeriod timePeriod = TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
                boolean inWater = player.isInWater();
                List<SeasonalBiomeAmbient> seasonalBiomeAmbientList = new ArrayList<>();
                for (SeasonalBiomeAmbient sound : ClientRef.sounds) {
                    if (sound.isIndoor() != indoor) continue;
                    if (sound.isRain() != raining) continue;
                    if (sound.getSeason() != Season.NONE) {
                        if (sound.getSeason() != season) continue;
                    } else {
                        if (!solarTerm.isInTerms(sound.getStart(), sound.getEnd())) continue;
                    }
                    if (!sound.isIgnore_time()) {
                        if (sound.getTimePeriod() != TimePeriod.NONE) {
                            if (sound.getTimePeriod() != timePeriod) continue;
                        } else {
                            if (sound.isDay() != isDayNow) continue;
                        }
                    }
                    if (sound.isInwater() != inWater) continue;
                    if (sound.getBiomes().size() > 0 && !sound.getBiomes().contains(biome)) continue;
                    if (sound.getIgnored_biomes().contains(biome)) continue;
                    if (sound.getSeed() > 0 && level.getRandom().nextInt(sound.getSeed()) > 0) continue;
                    seasonalBiomeAmbientList.add(sound);
                }
                if (seasonalBiomeAmbientList.size() > 1) {
                    seasonalBiomeAmbientList.sort(Comparator.comparing(SeasonalBiomeAmbient::getPriority));
                }
                soundEvent = seasonalBiomeAmbientList.isEmpty() ? null :
                        seasonalBiomeAmbientList.getFirst().getSoundEventHolder().value();
                // switch (season) {
                //     case SPRING -> {
                //         if (!player.isInWaterOrRain()
                //                 && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(level, player.blockPosition())) {
                //             if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS)) && !biome.is(Tags.Biomes.IS_COLD)) {
                //                 soundEvent = SoundEventsRegistry.spring_forest;
                //             }
                //         }
                //     }
                //     case SUMMER -> {
                //         // if (player.level().isNight())
                //         // 客户端不计算是否为夜晚
                //         if (!player.isInWaterOrRain()
                //                 && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(level, player.blockPosition())) {
                //             if (!isDayNow) {
                //                 if (!(biome.is(BiomeTags.IS_SAVANNA)
                //                         || biome.is(Tags.Biomes.IS_CAVE)
                //                         || biome.is(Tags.Biomes.IS_DESERT)
                //                         || biome.is(BiomeTags.IS_BADLANDS)
                //                         || biome.is(Tags.Biomes.IS_MOUNTAIN_PEAK))) {
                //                     soundEvent = SoundEventsRegistry.night_river;
                //                 }
                //             } else {
                //                 if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS) || biome.is(BiomeTags.IS_RIVER))) {
                //                     soundEvent = SoundEventsRegistry.garden_wind;
                //                 }
                //             }
                //         }
                //
                //     }
                //     case AUTUMN -> {
                //         if (!player.isInWater()) {
                //             if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST))) {
                //                 soundEvent = SoundEventsRegistry.windy_leave;
                //             }
                //         }
                //     }
                //     case WINTER -> {
                //         if (!player.isInWater()) {
                //             if (!biome.is(Tags.Biomes.IS_CAVE)) {
                //                 if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) && ClientWeatherChecker.isRain((ClientLevel) level))) {
                //                     soundEvent = SoundEventsRegistry.winter_forest;
                //                 } else soundEvent = SoundEventsRegistry.winter_cold;
                //             }
                //         }
                //     }
                //     case NONE -> {
                //     }
                // }
            }
            if (soundEvent != null) {
                boolean needAdd = true;

                for (LoopSeasonalSoundInstance soundInstance : this.loopSounds) {
                    Identifier key = soundInstance.getIdentifier();
                    boolean isTargetSound = key.equals(soundEvent.location());
                    if (isTargetSound) {
                        if (indoor) {
                            soundInstance.fadeOut();
                        } else {
                            // todo seems the check here is strange
                            // if (!soundManager.isActive(loopSound)) {
                            //     it.remove();
                            // } else {
                            //     loopSound.fadeIn();
                            //     needAdd = false;
                            // }
                            if (!soundInstance.isStopped())
                                soundInstance.fadeIn();
                        }
                        needAdd = false;
                    } else {
                        soundInstance.fadeOut();
                    }
                }


                // todo update the volume of autumn wind
                if (needAdd && !indoor) {
                    // EclipticSeasons.logger(needAdd, soundEvent.getLocation());
                    LoopSeasonalSoundInstance loopSoundInstance = new LoopSeasonalSoundInstance(soundEvent, loopSounds);
                    this.loopSounds.add(loopSoundInstance);
                    this.soundManager.play(loopSoundInstance);
                }
            } else {
                this.loopSounds.forEach(LoopSeasonalSoundInstance::fadeOut);
                // for (SimplePair<Biome, LoopSeasonalSoundInstance> pair : this.loopSoundList) {
                //     pair.getValue().fadeOut();
                // }
            }
        }
    }

    private @NonNull Holder<Biome> getBiome() {
        return this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
    }

    private boolean isIndoor() {
        return (player.level().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition())) < 12;
    }

}

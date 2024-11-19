package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.SimplePair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    private final List<SimplePair<Biome, LoopSoundInstance>> loopSoundList = new ArrayList<>();

    public SeasonalBiomeAmbientSoundsHandler(LocalPlayer localPlayer, SoundManager soundManager, BiomeManager biomeManager) {
        this.random = localPlayer.level().getRandom();
        this.player = localPlayer;
        this.soundManager = soundManager;
        this.biomeManager = biomeManager;
    }

    public void tick() {

        // this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);

        loopSoundList.removeIf(pair -> pair.getValue().isStopped());

        boolean indoor =

                (player.level().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition())) < 11;
        // EclipticSeasons.logger((player.level().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition())));

        Holder<Biome> biome = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
        Season season = ClientCon.nowSolarTerm.getSeason();
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
            switch (season) {
                case SPRING -> {
                    if (!player.isInWaterOrRain()) {
                        if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS)) && !biome.is(Tags.Biomes.IS_COLD)) {
                            soundEvent = EclipticSeasons.SoundEventsRegistry.spring_forest;
                        }
                    }
                }
                case SUMMER -> {
                    // if (player.level().isNight())
                    // 客户端不计算是否为夜晚
                    if (!player.isInWaterOrRain()) {
                        if (!isDayNow) {
                            if (!(biome.is(BiomeTags.IS_SAVANNA)
                                    && !biome.is(Tags.Biomes.IS_CAVE)
                                    && !biome.is(Tags.Biomes.IS_DESERT)
                                    && !biome.is(BiomeTags.IS_BADLANDS)
                                    && !biome.is(Tags.Biomes.IS_MOUNTAIN_PEAK))) {
                                soundEvent = EclipticSeasons.SoundEventsRegistry.night_river;
                            }
                        } else {
                            if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_PLAINS) || biome.is(BiomeTags.IS_RIVER))) {
                                soundEvent = EclipticSeasons.SoundEventsRegistry.garden_wind;
                            }
                        }
                    }

                }
                case AUTUMN -> {
                    if (!player.isInWater()) {
                        if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST))) {
                            soundEvent = EclipticSeasons.SoundEventsRegistry.windy_leave;
                        }
                    }
                }
                case WINTER -> {
                    if (!player.isInWater()) {
                        if (!biome.is(Tags.Biomes.IS_CAVE)) {
                            if ((biome.is(Biomes.CHERRY_GROVE) || biome.is(BiomeTags.IS_FOREST) && ClientWeatherChecker.isRain((ClientLevel) player.level()))) {
                                soundEvent = EclipticSeasons.SoundEventsRegistry.winter_forest;
                            } else soundEvent = EclipticSeasons.SoundEventsRegistry.winter_cold;
                        }
                    }
                }
                case NONE -> {
                }
            }
            if (soundEvent != null) {
                boolean needAdd = true;
                for (SimplePair<Biome, LoopSoundInstance> pair : this.loopSoundList) {
                    boolean isTargetSound = pair.getValue().getLocation().compareTo(soundEvent.getLocation()) == 0;
                    if (indoor || !isTargetSound) {
                        pair.getValue().fadeOut();
                    } else {
                        pair.getValue().fadeIn();
                    }
                    if (isTargetSound) {
                        needAdd = false;
                        if (System.currentTimeMillis() % 200 <= 5
                                &&!soundManager.isActive(pair.getValue())) {
                            needAdd = true;
                            loopSoundList.remove(pair);
                        }
                        break;
                    }
                }


                if (needAdd && !indoor) {
                    // EclipticSeasons.logger(needAdd, soundEvent.getLocation());
                    LoopSoundInstance loopSoundInstance = new LoopSoundInstance(soundEvent);
                    this.loopSoundList.add(SimplePair.of(biome.value(), loopSoundInstance));
                    this.soundManager.play(loopSoundInstance);
                }
            } else {
                for (SimplePair<Biome, LoopSoundInstance> pair : this.loopSoundList) {
                    pair.getValue().fadeOut();
                }
            }
        }
    }

    public static class LoopSoundInstance extends AbstractTickableSoundInstance {
        private int fadeDirection;
        private int fade;

        public LoopSoundInstance(SoundEvent soundEvent) {
            super(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.looping = true;
            // loop need delay bigger than 0
            this.delay = 0;
            this.volume = 0.5F;
            this.relative = true;
            // this.fade=40;
        }

        public void tick() {
            if (this.fade < 0) {
                this.stop();
            }
            // EclipticSeasons.logger(this, this.fade);
            this.fade += this.fadeDirection;
            this.volume = Mth.clamp((float) this.fade / 40.0F, 0.0F, 1.0F);

        }

        public void fadeOut() {
            this.fade = Math.min(this.fade, 40);
            if (this.fade >= -1)
                this.fadeDirection = -1;
            else this.fadeDirection = 0;
        }

        public void fadeIn() {
            this.fade = Math.max(0, this.fade);
            if (this.fade < 40)
                this.fadeDirection = 1;
            else this.fadeDirection = 0;
        }

    }
}

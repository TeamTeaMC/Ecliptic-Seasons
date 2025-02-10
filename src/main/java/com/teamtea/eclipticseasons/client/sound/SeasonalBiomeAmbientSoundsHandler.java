package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.audio.*;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class SeasonalBiomeAmbientSoundsHandler implements IAmbientSoundHandler {
    private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
    private static final float SKY_MOOD_RECOVERY_RATE = 0.001F;
    private final ClientPlayerEntity player;
    private final SoundHandler soundManager;
    private final BiomeManager biomeManager;
    private final Random random;
    private final Object2ObjectArrayMap<Biome, LoopSoundInstance> loopSounds = new Object2ObjectArrayMap<>();
    private float moodiness;
    @Nullable
    private Biome previousBiome;
    private Season previousSeason;
    private boolean previousIsDay;

    public SeasonalBiomeAmbientSoundsHandler(ClientPlayerEntity localPlayer, SoundHandler soundManager, BiomeManager biomeManager) {
        this.random = localPlayer.level.getRandom();
        this.player = localPlayer;
        this.soundManager = soundManager;
        this.biomeManager = biomeManager;
    }

    public float getMoodiness() {
        return this.moodiness;
    }

    public void tick() {
        this.loopSounds.values().removeIf(LoopSoundInstance::isStopped);

        // boolean indoor = (player.level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition()) < 12);
        boolean indoor = (player.level.getLightEngine().getLayerListener(LightType.SKY).getLightValue(player.blockPosition())) < 11;
        // EclipticSeasons.logger((player.level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(player.blockPosition())));

        Biome biome = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
        if (biome != this.previousBiome) {
            this.previousBiome = biome;
        }

        Optional<RegistryKey<Biome>> reskey = player.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
                .getResourceKey(biome);

        if (reskey.isPresent()) {
            Set<BiomeDictionary.Type> sets = BiomeDictionary.getTypes(reskey.get());
            Season season = SimpleUtil.getNowSolarTerm(player.level).getSeason();
            boolean isDayNow = SimpleUtil.isDay(player.level);
            if (season != this.previousSeason || isDayNow != this.previousIsDay) {
                this.loopSounds.values().forEach(LoopSoundInstance::fadeOut);
                {
                    this.previousSeason = season;
                    this.previousIsDay = isDayNow;
                    this.loopSounds.clear();
                }
            }

            SoundEvent soundEvent = null;
            if (MapChecker.isValidDimension(player.level)) {
                switch (season) {
                    case SPRING: {
                        if (!player.isInWaterOrRain()) {
                            if ((sets.contains(BiomeDictionary.Type.FOREST) || sets.contains(BiomeDictionary.Type.PLAINS)) && !sets.contains(BiomeDictionary.Type.COLD)) {
                                soundEvent = EclipticSeasons.SoundEventsRegistry.spring_forest;
                            }
                        }
                        break;
                    }
                    case SUMMER: {
                        if (!player.isInWaterOrRain())
                        {
                            if (!isDayNow) {
                                if (!(sets.contains(BiomeDictionary.Type.SAVANNA)
                                        && !sets.contains(BiomeDictionary.Type.MESA)
                                        && !sets.contains(BiomeDictionary.Type.DEAD)
                                        && !sets.contains(BiomeDictionary.Type.SANDY)
                                        && !sets.contains(BiomeDictionary.Type.MOUNTAIN))) {
                                    soundEvent = EclipticSeasons.SoundEventsRegistry.night_river;
                                }
                            } else {
                                if ((sets.contains(BiomeDictionary.Type.FOREST) || sets.contains(BiomeDictionary.Type.PLAINS) || sets.contains(BiomeDictionary.Type.RIVER))) {
                                    soundEvent = EclipticSeasons.SoundEventsRegistry.garden_wind;
                                }
                            }
                        }
                        break;
                    }
                    case AUTUMN: {
                        if (!player.isInWater()) {
                            if ((sets.contains(BiomeDictionary.Type.FOREST))) {
                                soundEvent = EclipticSeasons.SoundEventsRegistry.windy_leave;
                            }
                        }
                        break;
                    }
                    case WINTER: {
                        if (!player.isInWater()) {
                            {
                                if ((sets.contains(BiomeDictionary.Type.FOREST) && ClientWeatherChecker.isRain((ClientWorld) player.level))) {
                                    soundEvent = EclipticSeasons.SoundEventsRegistry.winter_forest;
                                } else soundEvent = EclipticSeasons.SoundEventsRegistry.winter_cold;
                            }
                        }
                        break;
                    }
                    case NONE: {
                        break;
                    }
                }
            }
            if (soundEvent != null) {
                SoundEvent finalSoundEvent = soundEvent;
                this.loopSounds.compute(biome, (biome1, loopSoundInstance) -> {
                    if (loopSoundInstance == null) {
                        loopSoundInstance = new LoopSoundInstance(finalSoundEvent);
                        this.soundManager.play(loopSoundInstance);
                    } else {
                        if (!this.soundManager.isActive(loopSoundInstance)
                                && !indoor
                        ) {
                            this.soundManager.play(loopSoundInstance);
                        }
                    }

                    return loopSoundInstance;
                });

                for (Map.Entry<Biome, LoopSoundInstance> entry : this.loopSounds.entrySet()) {
                    LoopSoundInstance loopSoundInstance = entry.getValue();
                    if (indoor || entry.getKey() != biome)
                        loopSoundInstance.fadeOut();
                    else
                        loopSoundInstance.fadeIn();
                }
            }
        }
    }

    public static class LoopSoundInstance extends TickableSound {
        private int fadeDirection;
        private int fade;

        public LoopSoundInstance(SoundEvent p_119658_) {
            super(p_119658_, SoundCategory.AMBIENT);
            this.looping = true;
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
            this.volume = MathHelper.clamp((float) this.fade / 40.0F, 0.0F, 1.0F);

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

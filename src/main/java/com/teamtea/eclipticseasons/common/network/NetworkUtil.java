package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeRainDispatcher;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class NetworkUtil {

    public static Level getClient() {
        return Minecraft.getInstance().level;
    }

    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static boolean processSolarTermsMessage(SolarTermsMessage solarTermsMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {

                SolarHolders.getSaveDataLazy(NetworkUtil.getClient()).ifPresent(data ->
                        {
                            data.setSolarTermsDay(solarTermsMessage.solarDay);
                            // BiomeClimateManager.updateTemperature(NetworkUtil.getClient(), data.getSolarTerm());
                            BiomeColorsHandler.needRefresh = true;
                            ClientCon.tick(getClient());
                        }
                );
            }
        });
        return true;
    }

    public static boolean processBiomeWeatherMessage(BiomeWeatherMessage biomeWeatherMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {

            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level level = NetworkUtil.getClient();
                Registry<WeatherEffect> weatherEffects = level.registryAccess().registryOrThrow(ESRegistries.WEATHER_EFFECT);
                var lists = WeatherManager.getBiomeList(NetworkUtil.getClient());
                if (lists != null) {
                    boolean update = false;
                    for (WeatherManager.BiomeWeather biomeWeather : lists) {
                        if (biomeWeatherMessage.rain[biomeWeather.id] == 0
                                && biomeWeather.rainTime > 0) {
                            ClientWeatherChecker.addLastRainyBiome(biomeWeather.biomeHolder.value(), (long) (1 / ClientWeatherChecker.getRate()));
                        }
                        if (!update
                            //&& biomeWeather.rainTime + biomeWeather.clearTime + biomeWeather.thunderTime > 0
                        )
                            update = biomeWeather.getSnowDepth() != biomeWeatherMessage.snowDepth[biomeWeather.id];
                        biomeWeather.rainTime = biomeWeatherMessage.rain[biomeWeather.id] * 10000;
                        biomeWeather.clearTime = biomeWeatherMessage.clear[biomeWeather.id] * 10000;
                        biomeWeather.thunderTime = biomeWeatherMessage.thuder[biomeWeather.id] * 10000;
                        biomeWeather.setSnowDepth(biomeWeatherMessage.snowDepth[biomeWeather.id]);
                        biomeWeather.effect = weatherEffects.getHolder(biomeWeatherMessage.special[biomeWeather.id]).orElse(null);
                        biomeWeather.setBiomeRain(BiomeRainDispatcher.getBiomeRain(
                                level instanceof ServerLevel, biomeWeatherMessage.weather[biomeWeather.id]));
                    }
                    if (update)
                        ClientCon.agent.setChange(true);
                }
            }
        });
        return true;
    }

    public static boolean processEmptyMessage(EmptyMessage emptyMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                // note 观察是否更新正常
                if (ClientConfig.Renderer.resetRendererAfterSleep.get()) {
                    Minecraft.getInstance().levelRenderer.allChanged();
                } else {
                    if (Minecraft.getInstance().cameraEntity instanceof LivingEntity livingEntity)
                        WorldRenderer.setAllDirty(SectionPos.of(livingEntity.getOnPos()));
                }
            }

        });
        return true;
    }


    public static boolean processDataPackEvent(DataPackEventMessage dataPackEvent, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {

            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                RegistryAccess registryAccess = ClientCon.getUseLevel().registryAccess();
                if (dataPackEvent.resourceKey.equals(ESRegistries.HUMIDITY_CONTROL)) {
                    List<HumidityControl> build = dataPackEvent.build(registryAccess, HumidityControl.class);
                    ClientCon.humidityControls.addAll(build);
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.BIOME_CLIMATE_SETTING)) {
                    ClientCon.biomeDataPackCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.SNOW_DEFINITIONS)) {
                    ClientCon.snowDefCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.SEASON_CYCLE)) {
                    ClientCon.seasonCycleCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.BIOME_RAIN)) {
                    ClientCon.biomeRainCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.SNOW_TERM)) {
                    ClientCon.snowTermCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.AGRO_CLIMATE)) {
                    ClientCon.aczCache = dataPackEvent;
                } else if (dataPackEvent.resourceKey.equals(ESRegistries.CROP)) {
                    ClientCon.cropCache = dataPackEvent;
                }
            }
        });
        return true;
    }

    public static boolean processHumidModifyMessage(HumidModifyMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level client = getClient();
                if (client != null) {
                    ClientCon.humidityModificationLevel = message.value;
                }
            }
        });
        return true;
    }


    public static boolean processUpdateTempChangeMessage(UpdateTempChangeMessage emptyMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (getClient() != null) {
                SolarHolders.getSaveDataLazy(getClient()).ifPresent(solarDataManager -> {
                    solarDataManager.setSolarTempChange(emptyMessage.change);
                });
            }
        });
        return true;
    }

    public static boolean processChunkBiomeUpdateMessage(ChunkBiomeUpdateMessage chunkBiomeUpdateMessage, Supplier<NetworkEvent.Context> iPayloadContext) {
        iPayloadContext.get().enqueueWork(() -> {
            if (ClientCon.getUseLevel() != null) {

                // ChunkPos chunkPos = new ChunkPos(chunkBiomeUpdateMessage.x, chunkBiomeUpdateMessage.z);
                // int minBlockX = chunkPos.getMinBlockX();
                // int minBlockZ = chunkPos.getMinBlockZ();
                // ChunkInfoMap chunkMap = MapChecker.getChunkInfoMapOrCreate(ClientCon.getUseLevel(),
                //         chunkPos.getMiddleBlockPosition(64));
                // if (chunkMap != null) {
                //     int[] biomes = chunkBiomeUpdateMessage.biomes;
                //     for (int i = 0; i < 16; i++) {
                //         for (int j = 0; j < 16; j++) {
                //             chunkMap.updateBiome(minBlockX + i, minBlockZ + j, biomes[i * 16 + j]);
                //         }
                //     }
                // }

                // =======================
                // port from 1.21
                LevelChunk chunkAt = ClientCon.getUseLevel().getChunkAt(chunkBiomeUpdateMessage.chunkPos.getWorldPosition());
                if (!(chunkAt.isEmpty())
                        && chunkAt instanceof IChunkBiomeHolder chunkBiomeHolder) {
                    chunkBiomeHolder.eclipticseasons$setBiomeHolder(new BiomeHolder(chunkBiomeUpdateMessage.biomes, true, chunkBiomeUpdateMessage.version));
                } else {
                    BiomeHolder.BIOME_HOLDER_MAP.put(chunkBiomeUpdateMessage.chunkPos,
                            new BiomeHolder(chunkBiomeUpdateMessage.biomes, true, chunkBiomeUpdateMessage.version));
                }
            }
        });
        return true;
    }

}

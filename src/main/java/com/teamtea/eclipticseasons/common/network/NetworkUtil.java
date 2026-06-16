package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.render.worldui.GrowthInfoClientCache;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeRainDispatcher;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.sync.ESConfigFilePayload;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class NetworkUtil {

    public static Level getClient() {
        return ClientCon.getUseLevel();
    }

    public static Player getPlayer() {
        return ClientCon.getAgent().getCameraEntity() instanceof Player player ?
                player : null;
    }

    public static boolean processSolarTermsMessage(SolarTermsMessage solarTermsMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {

                SolarHolders.getSaveDataLazy(NetworkUtil.getClient()).ifPresent(data ->
                        {
                            SolarTerm old = data.getSolarTerm();
                            data.setSolarTermsDay(solarTermsMessage.solarDay);
                            SolarTerm solarTerm = data.getSolarTerm();
                            if (solarTerm != old) {
                                MinecraftForge.EVENT_BUS.post(new SolarTermChangeEvent(old, solarTerm, getClient(), data.getSolarTermsDay()));
                                ClientCon.getAgent().setTermChange(true);
                            }
                            // BiomeClimateManager.updateTemperature(NetworkUtil.getClient(), data.getSolarTerm());
                            BiomeColorsHandler.needRefresh = true;
                            ClientCon.tick(getClient());
                            if (solarTerm != old) {
                                ClientCon.getAgent().setAllChunkDirty();
                            }
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
                        if (!update
                            //&& biomeWeather.rainTime + biomeWeather.clearTime + biomeWeather.thunderTime > 0
                        )
                            update = biomeWeather.getSnowDepth() != biomeWeatherMessage.snowDepth[biomeWeather.id];
                        biomeWeather.setSnowDepth(biomeWeatherMessage.snowDepth[biomeWeather.id]);
                        biomeWeather.effect = weatherEffects.getHolder(biomeWeatherMessage.special[biomeWeather.id]).orElse(null);
                        biomeWeather.setBiomeRain(BiomeRainDispatcher.getBiomeRain(
                                level instanceof ServerLevel, biomeWeatherMessage.weather[biomeWeather.id]));
                    }
                    if (update)
                        ClientCon.agent.setSnowChange(true);
                }
            }
        });
        return true;
    }

    public static boolean processEmptyMessage(EmptyMessage emptyMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                if (ClientConfig.Renderer.resetRendererAfterSleep.get()) {
                    ClientCon.getAgent().setAllRendererChanged();
                } else {
                    ClientCon.getAgent().setAllChunkDirty();
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

    public static boolean processConfigSync(SimpleNetworkHandler.S2CConfigData msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ESConfigSync.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
            contextSupplier.get().setPacketHandled(true);
            SimpleNetworkHandler.CHANNEL.reply(new SimpleNetworkHandler.C2SAcknowledge(), contextSupplier.get());
        });
        return true;
    }


    // public static boolean handleClientAck2(SimpleNetworkHandler.C2SAcknowledge c2SAcknowledge, Supplier<NetworkEvent.Context> contextSupplier) {
    //    contextSupplier.get().enqueueWork(() -> {
    //        contextSupplier.get().setPacketHandled(true);
    //    });
    //    return true;
    //}

    public static void handleClientAck(HandshakeHandler handshakeHandler, SimpleNetworkHandler.C2SAcknowledge c2SAcknowledge, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().setPacketHandled(true);
    }

    public static boolean processConfigInGame(ESConfigFilePayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        ESConfigSync.INSTANCE.receiveSyncedConfig(payload, contextSupplier);
        return true;
    }

    public static boolean handleGrowthInfoQuery(GrowthInfoMessage payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (getClient() != null) {
                GrowthInfoClientCache.update(payload.info.orElse(null));
            }
        });
        return true;
    }

    public static boolean handleNoneSnowAreaMessage(NoneSnowAreaMessage noneSnowAreaMessage, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if (getClient() != null) {
                LevelChunk chunk = getClient().getChunk(noneSnowAreaMessage.chunkPos.x, noneSnowAreaMessage.chunkPos.z);
                NoneSnowArea noneSnowArea = chunk.getCapability(NoneSnowArea.NONE_SNOW_AREA_CAPABILITY).orElseGet(NoneSnowArea::empty);
                noneSnowArea.copyFrom(noneSnowAreaMessage.value);
            }
        });
        return true;
    }
}

package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.common.core.biome.BiomeRainDispatcher;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.ESConfigSync;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class NetworkUtil {


    public static void processSolarTermsMessage2(SolarTermsMessage solarTermsMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            SolarHolders.getSaveDataLazy(context.player().level()).ifPresent(data -> {
                SolarTerm old = data.getSolarTerm();
                data.setSolarTermsDay(solarTermsMessage.solarDay);
                SolarTerm solarTerm = data.getSolarTerm();
                if (solarTerm != old) {
                    NeoForge.EVENT_BUS.post(new SolarTermChangeEvent(old, solarTerm, context.player().level(), data.getSolarTermsDay()));
                    ClientCon.getAgent().setChange(true);
                }
                // note 不再需要更新
                // BiomeClimateManager.updateTemperature(context.player().level(), data.getSolarTerm());
                BiomeColorsHandler.needRefresh = true;
                ClientCon.tick(context.player().level());
                BiomeColorsHandler.reloadColors();
                if (solarTerm != old) {
                    ClientCon.getAgent().setAllChunkDirty();
                }
            });
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processEmptyMessage(EmptyMessage emptyMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (ClientConfig.Renderer.resetRendererAfterSleep.get()) {
                ClientCon.getAgent().setAllRendererChanged();
            } else {
                ClientCon.getAgent().setAllChunkDirty();
            }
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processBiomeWeatherMessage(BiomeWeatherMessage biomeWeatherMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            var lists = WeatherManager.getBiomeList(context.player().level());
            if (lists != null) {
                Level level = context.player().level();
                Registry<WeatherEffect> weatherEffects = level.registryAccess().lookupOrThrow(ESRegistries.WEATHER_EFFECT);

                boolean update = false;
                for (WeatherManager.BiomeWeather biomeWeather : lists) {
                    if (biomeWeatherMessage.rain[biomeWeather.id] == 0 && biomeWeather.rainTime > 0) {
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
                    biomeWeather.effect = weatherEffects.get(biomeWeatherMessage.special[biomeWeather.id]).orElse(null);

                    biomeWeather.setBiomeRain(BiomeRainDispatcher.getBiomeRain(
                            level instanceof ServerLevel, biomeWeatherMessage.weather[biomeWeather.id]));
                }

                if (update)
                    ClientCon.agent.setChange(true);
                // if (update
                //         && ClientCon.agent.getCameraEntity() != null
                //         && ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                //     WorldRenderer.setAllDirty(SectionPos.of(ClientCon.agent.getCameraEntity().getOnPos()));
                // }
            }
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processChunkUpdateMessage(ChunkUpdateMessage chunkUpdateMessage, IPayloadContext context) {
        int[][] blocks = new int[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                blocks[i][j] = chunkUpdateMessage.snowyArea[i * 16 + j];
            }
        }
        context.enqueueWork(() -> {
            if (context.player().level() instanceof Level level && level.isClientSide()) {
                if (level.getChunk(chunkUpdateMessage.x, chunkUpdateMessage.z) instanceof LevelChunk levelChunk) {
                    var snow = new SnowyRemover(blocks);
                    levelChunk.setData(AttachmentRegistry.SNOWY_REMOVER, new SnowyRemover(blocks));

                    // if too less chunk need re compile render, it would not work
                    List<Integer> y = chunkUpdateMessage.y;
                    if (y.size() == 1) {
                        y = new ArrayList<>(y);
                        y.add(y.getFirst() - 1);
                    }
                    for (Integer ySection : y) {
                        WorldRenderer.setSectionDirty(SectionPos.of(chunkUpdateMessage.x, ySection, chunkUpdateMessage.z));
                    }
                }
            }

        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }


//    public static void handleConfigMessage(ConfigMessage configMessage, IPayloadContext iPayloadContext) {
//        iPayloadContext.enqueueWork(() -> {
//            CommonConfig.Season.validDimensions.set(configMessage.SeasonalDimensions().stream().map(
//                    k -> k.identifier().toString()
//            ).toList());
//        });
//    }

    public static void processChunkBiomeUpdateMessage(ChunkBiomeUpdateMessage chunkBiomeUpdateMessage, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            if (ClientCon.getUseLevel() != null) {
                ChunkAccess chunk = ClientCon.getUseLevel().getChunk(chunkBiomeUpdateMessage.x, chunkBiomeUpdateMessage.z, ChunkStatus.FULL, false);
                if (chunk != null) {
                    chunk.setData(AttachmentRegistry.BIOME_HOLDER, new BiomeHolder(chunkBiomeUpdateMessage.biomes, true, chunkBiomeUpdateMessage.version));
                }
            }
        });
    }

    public static void processHumidModifyMessage(HumidModifyMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level() instanceof Level level && level.isClientSide()) {
                ClientCon.humidityModificationLevel = message.value;
            }

        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processUpdateTempChangeMessage(UpdateTempChangeMessage emptyMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level() instanceof Level level) {
                SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                    solarDataManager.setSolarTempChange(emptyMessage.change);
                });
            }
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void handle(ESConfigFilePayload payload, IPayloadContext context) {
        ESConfigSync.INSTANCE.receiveSyncedConfig(payload.contents(), payload.fileName());
    }

}

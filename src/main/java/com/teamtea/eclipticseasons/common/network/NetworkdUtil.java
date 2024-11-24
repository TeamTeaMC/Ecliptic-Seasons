package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkdUtil {


    public static void processSolarTermsMessage2(SolarTermsMessage solarTermsMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            SolarHolders.getSaveDataLazy(context.player().level()).ifPresent(data -> {
                data.setSolarTermsDay(solarTermsMessage.solarDay);
                BiomeClimateManager.updateTemperature(context.player().level(), data.getSolarTerm());
                BiomeColorsHandler.needRefresh = true;
                ClientCon.tick(context.player().level());
                BiomeColorsHandler.reloadColors();
            });
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processEmptyMessage2(EmptyMessage emptyMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().levelRenderer.allChanged();
        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void processBiomeWeatherMessage2(BiomeWeatherMessage biomeWeatherMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
            var lists = WeatherManager.getBiomeList(context.player().level());
            if (lists != null) {
                for (WeatherManager.BiomeWeather biomeWeather : lists) {
                    if (biomeWeatherMessage.rain[biomeWeather.id] == 0 && biomeWeather.rainTime > 0) {
                        ClientWeatherChecker.addLastRainyBiome(biomeWeather.biomeHolder.value(), (long) (1 / ClientWeatherChecker.rate));
                    }
                    biomeWeather.rainTime = biomeWeatherMessage.rain[biomeWeather.id] * 10000;
                    biomeWeather.clearTime = biomeWeatherMessage.clear[biomeWeather.id] * 10000;
                    biomeWeather.thunderTime = biomeWeatherMessage.thuder[biomeWeather.id] * 10000;
                    biomeWeather.snowDepth = biomeWeatherMessage.snowDepth[biomeWeather.id];
                }
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
                    levelChunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, new SnowyRemover(blocks));

                    for (BlockPos blockPos : chunkUpdateMessage.blockPosList) {
                        ClientMapFixer.clearBlockPos(blockPos);
                        MapChecker.updatePosForce(level, blockPos, snow.notSnowyAt(blockPos) ? level.getMaxBuildHeight() + 1 : level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ()) - 1);
                    }
                    for (Integer ySection : chunkUpdateMessage.y) {
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

    public static void processMapFixerMessage(MapFixerMessage mapFixerMessage, IPayloadContext context) {
        Set<SectionPos> sectionPosSet = new HashSet<>();
        List<BlockPos> blockPosList = new ArrayList<>(mapFixerMessage.blockPosList);
        for (int i = 0; i < mapFixerMessage.blockPosList.size(); i++) {
            BlockPos blockPos = blockPosList.get(i);
            BlockPos blockPos1 = new BlockPos(blockPos.getX(), mapFixerMessage.startYList.get(i), blockPos.getZ());
            blockPosList.add(blockPos1);
            sectionPosSet.add(SectionPos.of(blockPos));
            sectionPosSet.add(SectionPos.of(blockPos1));
        }

        context.enqueueWork(() -> {
            if (context.player().level() instanceof Level level && level.isClientSide()) {
                for (BlockPos blockPos : mapFixerMessage.blockPosList) {
                    MapChecker.updatePosForce(level, blockPos, blockPos.getY());
                }
                for (SectionPos ySection : sectionPosSet) {
                    WorldRenderer.setSectionDirty(ySection);
                }
            }

        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("eclipticseasons.networking.failed", e.getMessage()));
            return null;
        });
    }


    public static void handleConfigMessage(ConfigMessage configMessage, IPayloadContext iPayloadContext) {
    iPayloadContext.enqueueWork(()->{
        ServerConfig.Season.validDimensions.set(configMessage.SeasonalDimensions().stream().map(
                k->k.location().toString()
        ).toList());
    });
    }
}

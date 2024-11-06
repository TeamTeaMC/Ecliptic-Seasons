package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NetworkdUtil {

    public static ClientLevel getClient() {
        return Minecraft.getInstance().level;
    }


    public static void processSolarTermsMessage2(SolarTermsMessage solarTermsMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
                    SolarHolders.getSaveDataLazy(context.player().level()).ifPresent(data ->
                            {
                                data.setSolarTermsDay(solarTermsMessage.solarDay);
                                BiomeClimateManager.updateTemperature(context.player().level(), data.getSolarTerm());
                                BiomeColorsHandler.needRefresh = true;
                            }
                    );
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("ecliptic.networking.failed", e.getMessage()));
                    return null;
                });
    }

    public static void processEmptyMessage2(EmptyMessage emptyMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
                    Minecraft.getInstance().levelRenderer.allChanged();
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("ecliptic.networking.failed", e.getMessage()));
                    return null;
                });
    }

    public static void processBiomeWeatherMessage2(BiomeWeatherMessage biomeWeatherMessage, IPayloadContext context) {
        context.enqueueWork(() -> {
                    var lists = WeatherManager.getBiomeList(context.player().level());
                    if (lists != null) {
                        for (WeatherManager.BiomeWeather biomeWeather : lists) {
                            if(biomeWeatherMessage.rain[biomeWeather.id]==0
                                    && biomeWeather.rainTime>0){
                                ClientWeatherChecker.addLastRainyBiome(biomeWeather.biomeHolder.value(), (long) (1/ClientWeatherChecker.rate));
                            }
                            biomeWeather.rainTime = biomeWeatherMessage.rain[biomeWeather.id] * 10000;
                            biomeWeather.clearTime = biomeWeatherMessage.clear[biomeWeather.id] * 10000;
                            biomeWeather.thunderTime = biomeWeatherMessage.thuder[biomeWeather.id] * 10000;
                            biomeWeather.snowDepth = biomeWeatherMessage.snowDepth[biomeWeather.id];
                        }
                    }
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("ecliptic.networking.failed", e.getMessage()));
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
            if(context.player().level() instanceof Level level
            &&level.isClientSide()) {
                if (level.getChunk(chunkUpdateMessage.x, chunkUpdateMessage.z) instanceof LevelChunk levelChunk) {
                    var snow= new SnowyRemover(blocks);
                    levelChunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, new SnowyRemover(blocks));

                    for (BlockPos blockPos : chunkUpdateMessage.blockPosList) {
                        ClientMapFixer.clearBlockPos(blockPos);
                        MapChecker.updatePosForce(level,blockPos,
                                snow.notSnowyAt(blockPos)?
                                level.getMaxBuildHeight() + 1:
                                level.getHeight(Heightmap.Types.MOTION_BLOCKING,blockPos.getX(),blockPos.getZ())-1
                                )
                        ;

                    }
                    if (Minecraft.getInstance().cameraEntity instanceof Player player) {
                        // BlockPos pos = player.getOnPos();
                        // SectionPos sectionPos = SectionPos.of(pos);
                        // Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(sectionPos.x(), sectionPos.y(), sectionPos.z());
                        for (Integer ySection : chunkUpdateMessage.y) {
                            Minecraft.getInstance().levelRenderer.setSectionDirty(chunkUpdateMessage.x,ySection,chunkUpdateMessage.z);
                        }
                    }
                }
            }

        }).exceptionally(e -> {
            // Handle exception
            context.disconnect(Component.translatable("ecliptic.networking.failed", e.getMessage()));
            return null;
        });
    }
}

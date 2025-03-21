package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.message.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.message.BroomUseMessage;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

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
                var lists = WeatherManager.getBiomeList(NetworkUtil.getClient());
                if (lists != null) {
                    for (WeatherManager.BiomeWeather biomeWeather : lists) {
                        if (biomeWeatherMessage.rain[biomeWeather.id] == 0
                                && biomeWeather.rainTime > 0) {
                            ClientWeatherChecker.addLastRainyBiome(biomeWeather.biomeHolder.value(), (long) (1 / ClientWeatherChecker.rate));
                        }
                        biomeWeather.rainTime = biomeWeatherMessage.rain[biomeWeather.id] * 10000;
                        biomeWeather.clearTime = biomeWeatherMessage.clear[biomeWeather.id] * 10000;
                        biomeWeather.thunderTime = biomeWeatherMessage.thuder[biomeWeather.id] * 10000;
                        biomeWeather.snowDepth = biomeWeatherMessage.snowDepth[biomeWeather.id];
                    }
                }
            }
        });
        return true;
    }

    public static boolean processEmptyMessage(EmptyMessage emptyMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Minecraft.getInstance().levelRenderer.allChanged();
            }
        });
        return true;
    }

    public static boolean processBroomUseMessage(BroomUseMessage broomUseMessage, Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level client = getClient();
                if (client != null ) {
                    int startY = client.getMaxBuildHeight() + 1;
                    BlockPos blockPos = broomUseMessage.blockPos;
                    // MapChecker.updatePosForce(level, blockPos, blockPos.getY());
                    ClientMapFixer.addPlanner(client, Blocks.AIR.defaultBlockState(), blockPos, client.getGameTime(), startY);
                    WorldRenderer.setSectionDirtyWithNeighbors(SectionPos.of(blockPos));
                }
            }
        });
        return true;
    }
}

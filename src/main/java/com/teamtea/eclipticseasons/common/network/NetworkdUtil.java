package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NetworkdUtil {

    public static ClientLevel getClient() {
        return Minecraft.getInstance().level;
    }

    public static boolean processSolarTermsMessage(SolarTermsMessage solarTermsMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {

                SolarHolders.getSaveDataLazy(NetworkdUtil.getClient()).ifPresent(data ->
                        {
                            data.setSolarTermsDay(solarTermsMessage.solarDay);
                            BiomeClimateManager.updateTemperature(NetworkdUtil.getClient(),data.getSolarTermIndex());
                            BiomeColorsHandler.needRefresh = true;
                        }
                );
                // try {
                //     // if (AllListener.getSaveDataLazy(NetworkdUtil.getClient()).resolve().get().getSolarTermsDay() % ServerConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                //     //     // 强制刷新
                //     //     // var cc = Minecraft.getInstance().levelRenderer;
                //     //     // cc.allChanged();
                //     // }
                // } catch (Exception e) {
                //     e.printStackTrace();
                // }
            }
        });
        return true;
    }

    public static boolean processBiomeWeatherMessage(BiomeWeatherMessage biomeWeatherMessage, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {

            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                var lists = WeatherManager.getBiomeList(NetworkdUtil.getClient());
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
}

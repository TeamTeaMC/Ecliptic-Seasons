package com.teamtea.eclipticseasons.common.network;

import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.message.*;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
                // note 观察是否更新正常
                if(ClientConfig.Renderer.resetRendererAfterSleep.get()){
                    Minecraft.getInstance().levelRenderer.allChanged();
                }
                else {
                    if (Minecraft.getInstance().cameraEntity instanceof LivingEntity livingEntity)
                        WorldRenderer.setAllDirty(SectionPos.of(livingEntity.getOnPos()));
                }
            }

        });
        return true;
    }

    public static boolean processBroomUseMessage(BroomUseMessage broomUseMessage, Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level client = getClient();
                if (client != null) {
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

    public static boolean processDataPackEvent(DataPackEventMessage dataPackEvent, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {

            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                RegistryAccess registryAccess = ClientCon.getUseLevel().registryAccess();
                if (dataPackEvent.resourceKey.equals(ESRegistries.HUMIDITY_CONTROL)) {
                    List<HumidityControl> build = dataPackEvent.build(registryAccess, HumidityControl.class);
                    ClientCon.humidityControls.addAll(build);
                }
                if (dataPackEvent.resourceKey.equals(ESRegistries.BIOME_CLIMATE_SETTING)) {
                    ClientCon.biomeDataPackCache = dataPackEvent;
                }
                if (dataPackEvent.resourceKey.equals(ESRegistries.SNOW_DEFINITIONS)) {
                    ClientCon.snowDefCache = dataPackEvent;
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
                    ClientCon.humidityModificationLevel =(int)message.value;
                }
            }
        });
        return true;
    }
}

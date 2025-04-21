package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.util.EclipticTagTool;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.handler.CustomRandomTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class AllListener {

    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        BiomeClimateManager.resetBiomeTemps(tagsUpdatedEvent.getRegistryAccess());
        WeatherManager.informUpdateBiomes(tagsUpdatedEvent.getRegistryAccess());
        EclipticTagTool.BIOME_TAG_KEY_MAP.clear();
    }


    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        WeatherManager.BIOME_WEATHER_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(PlayerSleepInBedEvent event) {
        if (event.getResultStatus() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
            BlockPos pos = event.getPos();
            Level level = event.getEntity().level;
            if (pos != null && EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, MapChecker.getSurfaceBiome(level, pos).value())) {
                event.setResult((Player.BedSleepingProblem) null);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepingTimeCheckEvent event) {
        if (event.getResult() == Event.Result.DEFAULT) {
            BlockPos pos = event.getSleepingLocation().orElse(null);
            Level level = event.getEntity().level;
            if (pos != null && EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, MapChecker.getSurfaceBiome(level, pos).value())) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {

            long newTime = event.getNewTime(), oldDayTime = ((Level) event.getLevel()).getDayTime();
            WeatherManager.updateAfterSleep(level, newTime, oldDayTime);

        }

    }


    @SubscribeEvent
    public static void onLevelEventLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            WeatherManager.createLevelBiomeWeatherList(serverLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(serverLevel, SolarDataManager.get(serverLevel));
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            WeatherManager.BIOME_WEATHER_LIST.remove(level);
            // if (level instanceof ServerLevel serverLevel)
            {
                SolarHolders.DATA_MANAGER_MAP.remove(level);
            }
        }

    }


    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)
                && !event.level.isClientSide()
                // TODO: fix the level resource key
                && MapChecker.isValidDimension(event.level)) {
            SolarDataManager data = SolarHolders.getSaveData(event.level);
            if (data != null) {
                data.updateTicks((ServerLevel) event.level);
            }
        }

        // CustomRandomTickHandler.onWorldTick(event);

    }

    // @SubscribeEvent
    // public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<Level> event) {
    //     if (ServerConfig.Season.enable.get() && event.getObject().dimension() == Level.OVERWORLD) {
    //         var cc = new SolarProvider();
    //         provider = LazyOptional.of(() -> cc);
    //         event.addCapability(new ResourceLocation(Ecliptic.MODID, "world_solar_terms"), cc);
    //     }
    // }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && !(event.getEntity() instanceof FakePlayer)) {
            WeatherManager.onLoggedIn(serverPlayer, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WeatherManager.onLoggedIn(serverPlayer, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // WeatherManager.onLoggedIn(serverPlayer, false);
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                // TODO：修复这里
                WeatherManager.onLoggedIn(serverPlayer, false);
            });
            t.start();

        }
    }

    @SubscribeEvent
    public static void onCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onCropGrowUp(SaplingGrowTreeEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }
}

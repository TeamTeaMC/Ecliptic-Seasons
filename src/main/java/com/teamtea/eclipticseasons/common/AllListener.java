package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.util.EclipticTagTool;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.handler.CustomRandomTickHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class AllListener {
    // public static LazyOptional<SolarProvider> provider = LazyOptional.empty();

    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new HashMap<>();


    public static SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    public static LazyOptional<SolarDataManager> getSaveDataLazy(Level level) {
        return LazyOptional.of(() -> DATA_MANAGER_MAP.getOrDefault(level, new SolarDataManager(level)));
    }

    public static void createSaveData(Level level,SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.put(level, solarDataManager);
    }

    // TagsUpdatedEvent invoke before ServerAboutToStartEvent
    // TODO：优化这个问题，理论上来说，更新数据的时候不能发送群系包，话说回来，既然是群系天气，实际上与level关系不大，不应该一个level一个
    // 但是这也说不准啊，谁知道谁无聊就搞这个呢
    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        BiomeClimateManager.resetBiomeTemps(tagsUpdatedEvent.getTagManager());
        WeatherManager.informUpdateBiomes(tagsUpdatedEvent.getTagManager());
        EclipticTagTool.BIOME_TAG_KEY_MAP.clear();
    }


    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        WeatherManager.BIOME_WEATHER_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
    }


    // @SubscribeEvent
    // public static void onServerStartedEvent(ServerStartedEvent event) {
    //     BiomeClimateManager.resetBiomeTemps(event.getServer().registryAccess());
    //     WeatherManager.informUpdateBiomes(event.getServer().registryAccess());
    // }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof ServerLevel level) {

            long newTime = event.getNewTime(), oldDayTime = (level).getDayTime();
            WeatherManager.updateAfterSleep(level, newTime, oldDayTime);
            // // TODO: 根据季节更新概率
            // if (!serverLevel.isRaining() && serverLevel.getRandom().nextFloat() > 0.8) {
            //     serverLevel.setWeatherParameters(0,
            //             ServerLevel.RAIN_DURATION.sample(serverLevel.getRandom()),
            //             true, false);
            // }
        }

    }


    @SubscribeEvent
    public static void onLevelEventLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverLevel) {
            WeatherManager.createLevelBiomeWeatherList(serverLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
          createSaveData(serverLevel, SolarDataManager.get(serverLevel));
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level level) {
            WeatherManager.BIOME_WEATHER_LIST.remove(level);
            // if (level instanceof ServerLevel serverLevel)
            {
                DATA_MANAGER_MAP.remove(level);
            }
        }

    }


    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)
                && !event.world.isClientSide()
                // TODO: fix the level resource key
                && event.world.dimensionType().natural()
                && !event.world.dimensionType().hasFixedTime()) {
            SolarDataManager data = getSaveData(event.world);
            if (data!=null) {
                data.updateTicks((ServerLevel) event.world);
            }
        }

        CustomRandomTickHandler.onWorldTick(event);

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
            WeatherManager.onLoggedIn(serverPlayer,true);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WeatherManager.onLoggedIn(serverPlayer,false);
        }
    }


    @SubscribeEvent
    public static void onCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }
}

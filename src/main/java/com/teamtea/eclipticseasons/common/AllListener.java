package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.util.EclipticTagTool;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.handler.CustomRandomTickHandler;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class AllListener {
    // public static LazyOptional<SolarProvider> provider = LazyOptional.empty();


    // TagsUpdatedEvent invoke before ServerAboutToStartEvent
    // TODO：优化这个问题，理论上来说，更新数据的时候不能发送群系包，话说回来，既然是群系天气，实际上与level关系不大，不应该一个level一个
    // 但是这也说不准啊，谁知道谁无聊就搞这个呢
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


    // @SubscribeEvent
    // public static void onServerStartedEvent(ServerStartedEvent event) {
    //     BiomeClimateManager.resetBiomeTemps(event.getServer().registryAccess());
    //     WeatherManager.informUpdateBiomes(event.getServer().registryAccess());
    // }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {

            long newTime = event.getNewTime(), oldDayTime = ((Level) event.getLevel()).getDayTime();
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
        if (event.phase.equals(TickEvent.Phase.END) && ServerConfig.Season.enableInform.get() && !event.level.isClientSide() && event.level.dimension() == Level.OVERWORLD) {
            SolarHolders.getSaveDataLazy(event.level).ifPresent(data ->
            {
                if (!event.level.players().isEmpty()) {
                    data.updateTicks((ServerLevel) event.level);
                }
            });
        }

        CustomRandomTickHandler.onWorldTick(event);

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

    @SubscribeEvent
    public static void onSaplingGrowTree(SaplingGrowTreeEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }
}

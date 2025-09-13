package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecordCa;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.common.core.snow.WeatherStatusKeeper;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.DataPackEventMessage;
import com.teamtea.eclipticseasons.common.network.message.HumidModifyMessage;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.*;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class AllListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdatedEventEarly(TagsUpdatedEvent tagsUpdatedEvent) {
        BiomeClimateManager.resetBiomeTags(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
    }

    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        BiomeClimateManager.resetBiomeTemps(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        WeatherManager.informUpdateBiomes(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        CropInfoManager.init(tagsUpdatedEvent);
        CropGrowthHandler.resetUpdate(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        NaturalPlantHandler.resetUpdate(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        SnowChecker.resetUpdate(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
    }

    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        WeatherManager.BIOME_WEATHER_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
        WeatherManager.BIOME_WEATHER_QUERY_LIST.clear();
    }

    @SubscribeEvent
    public static void onServerStoppingEvent(ServerStoppingEvent event) {
        CropGrowthHandler.clearOnClientExitOrServerClose();
        NaturalPlantHandler.clearOnClientExitOrServerClose();
        BiomeClimateManager.clearOnClientExitOrServerClose();
        SnowChecker.clearOnClientExitOrServerClose();
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(PlayerSleepInBedEvent event) {
        if (event.getResultStatus() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
            BlockPos pos = event.getPos();
            Level level = event.getEntity().level();
            if (pos != null && EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, pos)) {
                event.setResult((Player.BedSleepingProblem) null);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepingTimeCheckEvent event) {
        if (event.getResult() == Event.Result.DEFAULT) {
            BlockPos pos = event.getSleepingLocation().orElse(null);
            Level level = event.getEntity().level();
            if (pos != null && EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, pos)) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            long newTime = event.getNewTime(), oldDayTime = level.getDayTime();
            WeatherManager.updateAfterSleep(level, newTime, oldDayTime);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (CommonConfig.Season.validDimensions.get().contains(level.dimension().location().toString()))
                MapChecker.validDimension.add(level);
            WeatherManager.createLevelBiomeWeatherList(level);
            SolarHolders.createSaveData(level, SolarDataManager.get(level));
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            WeatherManager.BIOME_WEATHER_LIST.remove(level);
            WeatherManager.NEXT_CHECK_BIOME_MAP.remove(level);
            WeatherManager.BIOME_WEATHER_QUERY_LIST.remove(level);

            // if (level instanceof ServerLevel serverLevel)
            {
                SolarHolders.DATA_MANAGER_MAP.remove(level);
            }
            if (!level.isClientSide()) {

            }
            MapChecker.validDimension.removeIf(l -> l.equals(level));
        }

    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        MapChecker.sendChunkLoginInfo(event.getLevel(), event.getChunk(), event.getPos(), event.getPlayer());
        SnowyMapChecker.sendChunkLoginInfo$1_20_1(event.getLevel(), event.getChunk(), event.getPos(), event.getPlayer());
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess chunk = event.getChunk();
        if (event.getLevel() instanceof Level level) {
            BiomeHolder biomeHolder = null;
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                if (event.isNewChunk()) {
                    MapChecker.setNewChunk(serverLevel, chunk);
                }
                biomeHolder = MapChecker.getOrUpdateChunkBiomeData(serverLevel, (IChunkBiomeHolder) chunk, event.getChunk().getPos());
            }

            {
                ChunkInfoMap chunkInfoMap = MapChecker.forceChunkUpdateHeight(level, chunk);

                if (biomeHolder != null && level instanceof ServerLevel serverLevel) {
                    int biomeDataVersion = SolarHolders.getSaveData(level).getBiomeDataVersion();
                    if (biomeHolder.version() != biomeDataVersion || !biomeHolder.hasUpdated()) biomeHolder = null;
                    if (biomeHolder != null) {
                        SnowyMapChecker.forceChunkUpdateHeight(serverLevel, chunk, chunkInfoMap, biomeHolder, true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            MapChecker.unloadChunk(level, event.getChunk().getPos());
            CropGrowthHandler.unloadChunk(level, event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkDataSaveEvent(ChunkDataEvent.Save event) {
        if (event.getLevel() instanceof Level level) {
            SolarDataManager data = SolarHolders.getSaveData(level);
            if (data != null) data.saveChunk(event.getChunk().getPos(), event.getData());
        }
    }

    @SubscribeEvent
    public static void onChunkDataLoadEvent(ChunkDataEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            SolarHolders.getSaveDataLazy(level)
                    .ifPresent(solarDataManager -> {
                        solarDataManager.loadChunk(event.getChunk().getPos(), event.getData());
                    });
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)
                && event.level instanceof ServerLevel serverLevel) {
            SolarDataManager data = SolarHolders.getSaveData(event.level);
            if (data != null) {
                data.tickLevel(serverLevel);
            }
        }
        MapChecker.tickLevel(event.level);
    }

    @SubscribeEvent
    public static void onPlayerTickPost(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (event.phase == TickEvent.Phase.START) {
                WeatherManager.tickPlayerSeasonEffect(serverPlayer);
            }
            if (event.phase == TickEvent.Phase.END) {
                Level level = serverPlayer.level();
                if (level.getGameTime() % 20 == 0) {
                    ModAdvancements.parentNeedCriterion.trigger(serverPlayer);

                    SolarDataManager data = SolarHolders.getSaveData(level);
                    if (data != null) {
                        float v = data.calculateHumidityModification(serverPlayer.blockPosition());
                        SimpleNetworkHandler.send(serverPlayer, new HumidModifyMessage(
                                serverPlayer.blockPosition(), Mth.floor(v)
                        ));
                    }

                    // if (((level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(serverPlayer.blockPosition())) > 12)
                    //         &&level.getRandom().nextInt(4) == 0
                    //         || level.getRandom().nextInt(128) == 0)  {
                    //     float chance = 0;
                    //     for (int i = 0; i < 20; i++) {
                    //         chance += CropGrowthHandler.isInRoom(level, serverPlayer.getOnPos().above(),
                    //                 Blocks.AIR.defaultBlockState(), Optional.empty()) ? 1 : 0;
                    //     }
                    //     if (chance > 16) {
                    //         ModAdvancements.greenhouseCriterion.trigger(serverPlayer);
                    //     }
                    // }
                }


            }
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
                WeatherManager.onLoggedIn(serverPlayer, false);
            });
            t.start();

        }
    }

    @SubscribeEvent
    public static void onCropGrowUp(CanPlantGrowEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        if (!CommonConfig.isForceCropCompatMode())
            CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onSaplingGrowTree(SaplingGrowTreeEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onSaplingGrowTree(BonemealEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }


    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(EclipticSeasons.rl("solar_term_holder"), new SolarTermsRecordCa());
        }
    }

    @SubscribeEvent
    public static void onLevelChunkAttachCapabilitiesEvent(AttachCapabilitiesEvent<LevelChunk> event) {
        if (event.getObject() instanceof LevelChunk) {
            event.addCapability(EclipticSeasons.rl("biomes_holder"), BiomeHolder.empty());
            event.addCapability(EclipticSeasons.rl("snowy_status"), SnowyStatusKeeper.create());
            event.addCapability(EclipticSeasons.rl("weather_status"), WeatherStatusKeeper.create());
        }
    }

    @SubscribeEvent
    public static void onOnDatapackSyncEvent(OnDatapackSyncEvent event) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();

        ServerPlayer player = event.getPlayer();
        List<ServerPlayer> serverPlayerList = player == null ?
                event.getPlayerList().getPlayers() : List.of(player);

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.HUMIDITY_CONTROL,
                registryAccess.registryOrThrow(ESRegistries.HUMIDITY_CONTROL).entrySet(),
                HumidityControl.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.BIOME_CLIMATE_SETTING,
                registryAccess.registryOrThrow(ESRegistries.BIOME_CLIMATE_SETTING).entrySet(),
                BiomesClimateSettings.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.SNOW_DEFINITIONS,
                registryAccess.registryOrThrow(ESRegistries.SNOW_DEFINITIONS).entrySet(),
                SnowDefinition.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.SEASON_CYCLE,
                registryAccess.registryOrThrow(ESRegistries.SEASON_CYCLE).entrySet(),
                SeasonCycle.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.BIOME_RAIN,
                registryAccess.registryOrThrow(ESRegistries.BIOME_RAIN).entrySet(),
                CustomRainBuilder.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.SNOW_TERM,
                registryAccess.registryOrThrow(ESRegistries.SNOW_TERM).entrySet(),
                CustomSnowTerm.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.AGRO_CLIMATE,
                registryAccess.registryOrThrow(ESRegistries.AGRO_CLIMATE).entrySet(),
                AgroClimaticZone.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.CROP,
                registryAccess.registryOrThrow(ESRegistries.CROP).entrySet(),
                CropGrowControlBuilder.CODEC));

    }
}

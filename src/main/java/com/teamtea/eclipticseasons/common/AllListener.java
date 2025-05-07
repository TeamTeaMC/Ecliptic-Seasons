package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecordCa;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class AllListener {
    // public static LazyOptional<SolarProvider> provider = LazyOptional.empty();


    // TagsUpdatedEvent invoke before ServerAboutToStartEvent
    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        BiomeClimateManager.resetBiomeTemps(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        WeatherManager.informUpdateBiomes(tagsUpdatedEvent.getRegistryAccess());
        CropInfoManager.init(tagsUpdatedEvent);
        CropGrowthHandler.resetUpdate(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
    }


    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        WeatherManager.BIOME_WEATHER_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
    }

    @SubscribeEvent
    public static void onServerStoppingEvent(ServerStoppingEvent event) {
        CropGrowthHandler.clearOnClientExitOrServerClose();
        BiomeClimateManager.clearOnClientExitOrServerClose();
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
            long newTime = event.getNewTime(), oldDayTime = ((Level) event.getLevel()).getDayTime();
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
            // if (level instanceof ServerLevel serverLevel)
            {
                SolarHolders.DATA_MANAGER_MAP.remove(level);
            }

            MapChecker.validDimension.removeIf(l -> l.equals(level));
        }

    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess chunk = event.getChunk();
        if (event.getLevel() instanceof Level level) {
            MapChecker.forceChunkUpdateHeight(level, chunk);
        }
    }

    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        MapChecker.unloadChunk((Level) event.getLevel(), event.getChunk().getPos());
        CropGrowthHandler.unloadChunk((Level) event.getLevel(), event.getChunk().getPos());
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
    }

    @SubscribeEvent
    public static void onPlayerTickPost(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (event.phase == TickEvent.Phase.START) {
                WeatherManager.tickPlayerSeasonEffecct(serverPlayer);
            }
            if (event.phase == TickEvent.Phase.END) {
                if (serverPlayer.level().getGameTime() % 20 == 0) {
                    ModAdvancements.parentNeedCriterion.trigger(serverPlayer);

                    SolarDataManager data = SolarHolders.getSaveData(serverPlayer.level());
                    if (data != null) {
                        float v = data.calculateHumidityModification(serverPlayer.blockPosition());
                        SimpleNetworkHandler.send(serverPlayer, new HumidModifyMessage(
                                serverPlayer.blockPosition(), Mth.floor(v)
                        ));
                    }
                }

                if (CropGrowthHandler.isInRoom(serverPlayer.level(), serverPlayer.getOnPos().above(),
                        Blocks.AIR.defaultBlockState(), Optional.empty())) {
                    ModAdvancements.greenhouseCriterion.trigger(serverPlayer);
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
                // TODO：修复这里
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
    public static void onOnDatapackSyncEvent(OnDatapackSyncEvent event) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();

        ServerPlayer player = event.getPlayer();
        List<ServerPlayer> serverPlayerList = player == null ?
                event.getPlayerList().getPlayers() : List.of(player);

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.HUMIDITY_CONTROL,
                registryAccess.registryOrThrow(ESRegistries.HUMIDITY_CONTROL).entrySet().stream().map(Map.Entry::getValue).toList(),
                HumidityControl.CODEC));

        SimpleNetworkHandler.send(serverPlayerList, new DataPackEventMessage<>(
                registryAccess,
                ESRegistries.BIOME_CLIMATE_SETTING,
                registryAccess.registryOrThrow(ESRegistries.BIOME_CLIMATE_SETTING).entrySet().stream().map(Map.Entry::getValue).toList(),
                BiomesClimateSettings.CODEC));
    }
}

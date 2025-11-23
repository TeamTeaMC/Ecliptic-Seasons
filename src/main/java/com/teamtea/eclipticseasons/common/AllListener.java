package com.teamtea.eclipticseasons.common;


import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
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
import com.teamtea.eclipticseasons.common.network.message.HumidModifyMessage;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID)
public class AllListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdatedEventEarly(TagsUpdatedEvent tagsUpdatedEvent) {
        ESSortInfo.resetUpdate(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
        BiomeClimateManager.resetBiomeTags(tagsUpdatedEvent.getRegistryAccess(), tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD);
    }

    // TagsUpdatedEvent invoke before ServerAboutToStartEvent
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
        WeatherManager.BIOME_WEATHER_QUERY_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
    }


    @SubscribeEvent
    public static void onServerStoppingEvent(ServerStoppedEvent event) {
        CropGrowthHandler.clearOnClientExitOrServerClose();
        NaturalPlantHandler.clearOnClientExitOrServerClose();
        BiomeClimateManager.clearOnClientExitOrServerClose(true);
        SnowChecker.clearOnClientExitOrServerClose();
        ESSortInfo.clearOnClientExitOrServerClose();
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(CanPlayerSleepEvent event) {
        if (event.getVanillaProblem() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
            BlockPos pos = event.getPos();
            Level level = event.getLevel();
            if (EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, pos)) {
                event.setProblem(null);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(CanContinueSleepingEvent event) {
        if (!event.mayContinueSleeping()
                && event.getProblem() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
            BlockPos pos = event.getEntity().getSleepingPos().orElse(null);
            Level level = event.getEntity().level();
            if (pos != null && EclipticUtil.hasLocalWeather(level)
                    && WeatherManager.isThunderAtBiome(level, pos)) {
                event.setContinueSleeping(true);
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTimeEvent(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {

            long newTime = event.getNewTime(),
                    oldDayTime = level.getDayTime();
            WeatherManager.updateAfterSleep(level, newTime, oldDayTime);
            // if (!serverLevel.isRaining() && serverLevel.getRandom().nextFloat() > 0.8) {
            //     serverLevel.setWeatherParameters(0,
            //             ServerLevel.RAIN_DURATION.sample(serverLevel.getRandom()),
            //             true, false);
            // }
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
            SolarHolders.DATA_MANAGER_MAP.remove(level);
            MapChecker.unloadLevel(level);
            MapChecker.validDimension.removeIf(l -> l.equals(level));
        }
    }

    // 如果是客户端，即使是混合型客户端，我们也只应该清理一次，单人世界时只看一次client会更好
    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        // if ((FMLLoader.getDist() == Dist.CLIENT) == event.getLevel().isClientSide()
        // ) {
        //     MapChecker.clearChunk(event.getChunk().getLevel(),event.getChunk().getPos());
        // }
        MapChecker.unloadChunk(event.getChunk().getLevel(), event.getChunk().getPos());
        CropGrowthHandler.unloadChunk(event.getChunk().getLevel(), event.getChunk().getPos());
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
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            SolarDataManager data = SolarHolders.getSaveData(serverLevel);
            if (data != null) {
                data.tickLevel(serverLevel);
            }
        }
        MapChecker.tickLevel(level);
    }

    @SubscribeEvent
    public static void onLevelTickPre(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel)
            WeatherManager.tickAverageWeather(event.getLevel());
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
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
            // 不知道为什么要多线程来避免问题
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
    public static void onCropGrowUp(CropGrowEvent.Pre event) {
        if (!CommonConfig.isForceCropCompatMode())
            CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onCropGrowUp(BlockGrowFeatureEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onCropGrowUp(BonemealEvent event) {
        CropGrowthHandler.beforeCropGrowUp(event);
    }

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WeatherManager.tickPlayerSeasonEffect(serverPlayer);
            // WeatherManager.tickPlayerForSeasonCheck(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Level level = serverPlayer.level();
            if (level.getGameTime() % 20 == 0) {
                ModAdvancements.parentNeedCriterion.get().trigger(serverPlayer);

                SolarDataManager data = SolarHolders.getSaveData(level);
                if (data != null) {
                    float v = data.calculateHumidityModification(serverPlayer.blockPosition(), false);
                    SimpleNetworkHandler.send(serverPlayer, new HumidModifyMessage(
                            serverPlayer.blockPosition(), v
                    ));
                }

                // if (((level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(serverPlayer.blockPosition())) > 12)
                //         && level.getRandom().nextInt(4) == 0
                //         || level.getRandom().nextInt(128) == 0) {
                //     float rainChance = 0;
                //     for (int i = 0; i < 20; i++) {
                //         rainChance += CropGrowthHandler.isInRoom(level, serverPlayer.getOnPos().above(),
                //                 Blocks.AIR.defaultBlockState(), Optional.empty()) ? 1 : 0;
                //     }
                //     if (rainChance > 16) {
                //         ModAdvancements.greenhouseCriterion.get().trigger(serverPlayer);
                //     }
                // }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Sent event) {
        MapChecker.sendChunkLoginInfo(event.getLevel(), event.getChunk(), event.getPos(), event.getPlayer());
    }

    // Not do anything here would cause dead lock
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess chunk = event.getChunk();
        BiomeHolder biomeHolder = null;
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (event.isNewChunk()) {
                MapChecker.setNewChunk(serverLevel, chunk);
            }
            biomeHolder = MapChecker.getOrUpdateChunkBiomeData(serverLevel, chunk, event.getChunk().getPos());
        }

        if (event.getLevel() instanceof Level level) {
            ChunkInfoMap chunkInfoMap = MapChecker.forceChunkUpdateHeight(level, chunk);

            if (EclipticUtil.canSnowyBlockInteract() && biomeHolder != null && level instanceof ServerLevel serverLevel) {
                int biomeDataVersion = EclipticUtil.getBiomeDataVersion(level);
                if (biomeHolder.version() != biomeDataVersion || !biomeHolder.hasUpdated()) biomeHolder = null;
                if (biomeHolder != null) {
                    SnowyMapChecker.forceChunkUpdateHeight(serverLevel, chunk, chunkInfoMap, biomeHolder, true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onNeighborNotifyEvent(BlockEvent.NeighborNotifyEvent event) {
    }


    //
    // @SubscribeEvent
    // public static void onModifyCustomSpawnersEvent(ModifyCustomSpawnersEvent event) {
    //     event.addCustomSpawner((level, spawnEnemies, spawnFriendlies) -> {
    //         Player player = level.getRandomPlayer();
    //
    //         if (player == null || level.getRandom().nextInt(100) > 0) {
    //             return 0;
    //         } else {
    //             RandomSource randomsource = level.random;
    //             int i = (8 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
    //             int j = (8 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
    //             BlockPos blockpos = player.blockPosition().offset(i, 0, j);
    //             int l = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos.getX(), blockpos.getZ());
    //             blockpos = new BlockPos(blockpos.getX(), l, blockpos.getZ());
    //             PathfinderMob cat = null;
    //             switch (EclipticUtil.getNowSolarTerm(level).getSeason()) {
    //                 case SPRING -> {
    //                     cat = EntityType.SHEEP.create(level);
    //                 }
    //                 case SUMMER -> {
    //                     cat = EntityType.PUFFERFISH.create(level);
    //                 }
    //                 case AUTUMN -> {
    //                     cat = EntityType.SNIFFER.create(level);
    //                 }
    //                 case WINTER -> {
    //                     cat = EntityType.FOX.create(level);
    //                 }
    //                 default -> {
    //                 }
    //
    //             }
    //
    //             if (cat == null) {
    //                 return 0;
    //             } else {
    //                 cat.moveTo(blockpos, 0.0F, 0.0F); // Fix MC-147659: Some witch huts spawn the incorrect cat
    //                 EventHooks.finalizeMobSpawn(cat, level, level.getCurrentDifficultyAt(blockpos), MobSpawnType.NATURAL, null);
    //                 level.addFreshEntityWithPassengers(cat);
    //                 if (cat instanceof VariantHolder variantHolder) {
    //                     try {
    //                         VariantHolder<Fox.Type> typeVariantHolder = (VariantHolder<Fox.Type>) variantHolder;
    //                         typeVariantHolder.setVariant(Fox.Type.SNOW);
    //                     } catch (Exception e) {
    //
    //                     }
    //                 }
    //                 return 1;
    //             }
    //         }
    //     });
    // }
    //
    // @SubscribeEvent
    // public static void onFinalizeSpawnEvent(FinalizeSpawnEvent event) {
    // }
    //
    // @SubscribeEvent
    // public static void onBabyEntitySpawnEvent(BabyEntitySpawnEvent event) {
    // }
    //
    // @SubscribeEvent
    // public static void onEffectParticleModificationEvent(EffectParticleModificationEvent event) {
    // }
    //
    // @SubscribeEvent
    // public static void onLivingBreatheEvent(LivingBreatheEvent event) {
    // }

}

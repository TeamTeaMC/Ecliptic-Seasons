package com.teamtea.eclipticseasons.common;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.HumidModifyMessage;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
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
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID)
public class AllListener {


    // TagsUpdatedEvent invoke before ServerAboutToStartEvent
    // TODO：优化这个问题，理论上来说，更新数据的时候不能发送群系包，话说回来，既然是群系天气，实际上与level关系不大，不应该一个level一个
    // 但是这也说不准啊，谁知道谁无聊就搞这个呢
    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        // EntityTickEvent.Post
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
    public static void onServerStoppingEvent(ServerStoppingEvent event) {
        CropGrowthHandler.clearOnClientExitOrServerClose();
        NaturalPlantHandler.clearOnClientExitOrServerClose();
        BiomeClimateManager.clearOnClientExitOrServerClose();
        SnowChecker.clearOnClientExitOrServerClose();
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
            // // TODO: 根据季节更新概率
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
            WeatherManager.BIOME_WEATHER_QUERY_LIST.remove(level);
            SolarHolders.DATA_MANAGER_MAP.remove(level);
            MapChecker.unloadLevel(level);
            if (!level.isClientSide()) {
                ServerMapFixer.unloadLevel(level);
            }
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
        ServerMapFixer.unloadChunk(event.getChunk().getLevel(), event.getChunk().getPos());
        CropGrowthHandler.unloadChunk(event.getChunk().getLevel(), event.getChunk().getPos());
    }


    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            ServerMapFixer.tick(serverLevel);
            SolarDataManager data = SolarHolders.getSaveData(serverLevel);
            if (data != null) {
                data.tickLevel(serverLevel);
            }
        }

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
            WeatherManager.tickPlayerSeasonEffecct(serverPlayer);
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
                    float v = data.calculateHumidityModification(serverPlayer.blockPosition(),false);
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
        if (event.isNewChunk() && event.getLevel() instanceof ServerLevel serverLevel) {
            MapChecker.setNewChunk(serverLevel, chunk);
        }

        if (event.getLevel() instanceof Level level) {
            MapChecker.forceChunkUpdateHeight(level, chunk);
        }
    }

    @SubscribeEvent
    public static void onQuestCheck(BlockEvent.NeighborNotifyEvent event) {
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

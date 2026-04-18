package com.teamtea.eclipticseasons.common.core.snow;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.Map;

public class SnowyMapChecker {

    public static @NonNull WeatherStatusKeeper getWeatherStatusKeeper(ChunkAccess chunk) {
        return chunk.getData(AttachmentRegistry.WEATHER_STATUS_KEEPER);
    }

    public static @NonNull SnowyStatusKeeper getSnowyStatusKeeper(ChunkAccess chunk) {
        return chunk.getData(AttachmentRegistry.SNOWY_STATUS_KEEPER);
    }

    public static @NonNull SnowyStatusKeeper getSnowyStatusKeeperCopy(LevelChunk chunk) {
        if (!EclipticUtil.canSnowyBlockInteract()) return SnowyStatusKeeper.EMPTY;
        return getSnowyStatusKeeper(chunk);
    }

    public static void updatePos(Level level, LevelChunk chunk, BlockPos pos, BlockState state, BlockState oldState, Block block) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            boolean blockDestroyOrReplace = false;
            if (state.isEmpty()) {
                blockDestroyOrReplace = true;
            } else if (MapChecker.solidTest(state)) {
                SnowyMapChecker.removeSnowyStatus(level, chunk, pos);
                SnowyMapChecker.removeSnowyStatus(level, chunk, pos.below());
            } else if (CommonConfig.Snow.snowyUnderSnowLike.get() && state.is(BlockTags.SNOW)) {
                SnowyMapChecker.setSnowyStatus(level, chunk, pos.below());
            } else if (!oldState.is(block)) {
                blockDestroyOrReplace = true;
            }

            if (blockDestroyOrReplace) {
                SnowyMapChecker.removeSnowyStatus(level, chunk, pos);
                if (CommonConfig.Snow.snowyUnderSnowLike.get() && oldState.is(BlockTags.SNOW)) {
                    SnowyMapChecker.removeSnowyStatus(level, chunk, pos.below());
                }
            }
        }
    }


    public static void updateAllChunks(ServerLevel level) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;

        int biomeDataVersion = EclipticUtil.getBiomeDataVersion(level);
        for (ChunkHolder chunk : level.getChunkSource().chunkMap.allChunksWithAtLeastStatus(ChunkStatus.FULL).toList()) {
            ChunkAccess latestChunk = chunk.getLatestChunk();
            if (latestChunk instanceof IChunkBiomeHolder chunkBiomeHolder) {
                BiomeHolder biomeHolder = chunkBiomeHolder.eclipticseasons$getBiomeHolder();
                if (biomeHolder != null) {
                    if (biomeHolder.version() != biomeDataVersion) biomeHolder = null;
                }
                SnowyMapChecker.forceChunkUpdateHeight(level, latestChunk, MapChecker.getChunkInfoMapOrCreate(level, latestChunk.getPos()), biomeHolder, false);
            }
        }
    }

    public static void removeSnowyStatus(ServerLevel level, BlockPos pos) {
        if (MapChecker.isLoaded(level, pos)) {
            removeSnowyStatus(level, level.getChunkAt(pos), pos);
        }
    }

    public static void removeSnowyStatus(Level level, LevelChunk chunk, BlockPos pos) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunk);
            if (level.isClientSide()) keeper.set(pos.asLong(), SnowyStatusKeeper.FLAG_NONE);
            else keeper.set(pos, SnowyStatusKeeper.FLAG_NONE);
        }
    }

    public static void setSnowyStatus(Level level, LevelChunk chunk, BlockPos pos) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunk);
            if (level.isClientSide()) keeper.set(pos.asLong(), SnowyStatusKeeper.FLAG_SNOW);
            else keeper.set(pos, SnowyStatusKeeper.FLAG_SNOW);
        }
    }


    public static void forceChunkUpdateHeight(ServerLevel level,
                                              ChunkAccess chunk,
                                              ChunkInfoMap chunkMap,
                                              @Nullable BiomeHolder biomeHolder,
                                              boolean loadingChunk) {
        if (!EclipticUtil.canSnowyBlockInteract() || !CommonConfig.Snow.forceChunkUpdate.get()) return;
        if (CommonConfig.Snow.forceChunkUpdateOnlyWhenMelt.get()) {
            forceUpdateChunkOnlyNotSnow(level, chunk, chunk.getPos(), chunkMap, biomeHolder, loadingChunk);
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunk);
        WeatherStatusKeeper weatherStatusKeeper = getWeatherStatusKeeper(chunk);
        boolean checkIfBiomeCacheAnyMore = false;
        var biomeSnowyUpdate = weatherStatusKeeper.collectSnowyUpdate(level, biomeHolder);

        if (chunkMap != null) {
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    checkPos.setX(i);
                    checkPos.setZ(j);
                    int k = chunkMap.getHeight(i, j);
                    checkPos.setY(k);
                    checkIfBiomeCacheAnyMore = SnowyMapChecker.isCheckIfBiomeCacheAnyMore(level, chunk, biomeHolder, biomeSnowyUpdate, checkPos, keeper, weatherStatusKeeper, k);
                }
            }
        }
        SnowyMapChecker.postAfterChunkUpdate(level, chunk, keeper, weatherStatusKeeper, checkIfBiomeCacheAnyMore, loadingChunk);
    }


    private static boolean isCheckIfBiomeCacheAnyMore(ServerLevel level,
                                                      ChunkAccess chunk,
                                                      BiomeHolder biomeHolder,
                                                      Pair<Map<Holder<Biome>, IntIntImmutablePair>, Map<Holder<Biome>, Long>> pair,
                                                      BlockPos.MutableBlockPos checkPos,
                                                      SnowyStatusKeeper keeper,
                                                      WeatherStatusKeeper weatherStatusKeeper, int solidHeight) {
        Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate = pair.getFirst();
        Map<Holder<Biome>, Long> rainUpdateMap = pair.getSecond();
        if (!biomeSnowyUpdate.isEmpty()) {
            Holder<Biome> biome = MapChecker.getSurfaceBiome(level, checkPos, biomeHolder);
            weatherStatusKeeper.getBiomeUse().add(biome);
            boolean rainInMissingTime = rainUpdateMap == null || rainUpdateMap.containsKey(biome);
            if (rainInMissingTime) {
                IntIntImmutablePair snowDepthUse = biomeSnowyUpdate.getOrDefault(biome, null);
                int heightSurface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
                for (int posH = solidHeight; posH <= heightSurface; posH++) {
                    checkPos.setY(posH);
                    BlockState state = chunk.getBlockState(checkPos);
                    int flag = MapChecker.getDefaultBlockTypeFlag(state);
                    if (flag != MapChecker.FLAG_NONE) {
                        if (SnowLightChecker.isTooLight(level, checkPos, state, flag)) {
                            keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                        } else if (snowDepthUse == null) {
                            if (MapChecker.isAboveSnowLine(level, biome.value(), checkPos)) {
                                keeper.set(checkPos, SnowyStatusKeeper.FLAG_SNOW);
                            }
                        } else if (snowDepthUse.rightInt() == 0) {
                            if (snowDepthUse.leftInt() > Math.abs(state.getSeed(checkPos) % 100)
                                    || MapChecker.isAboveSnowLine(level, biome.value(), checkPos)) {
                                keeper.set(checkPos, SnowyStatusKeeper.FLAG_SNOW);
                            } else {
                                keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                            }
                        } else {
                            int cut = snowDepthUse.rightInt() + (snowDepthUse.leftInt() - snowDepthUse.rightInt()) / 4;
                            if (cut > 0) {
                                if (cut > Math.abs(state.getSeed(checkPos) % 100)
                                        || MapChecker.isAboveSnowLine(level, biome.value(), checkPos)) {
                                    keeper.set(checkPos, SnowyStatusKeeper.FLAG_SNOW);
                                }
                            } else {
                                if (-cut <= Math.abs(state.getSeed(checkPos) % 100)) {
                                    keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }


    private static void postAfterChunkUpdate(ServerLevel level, ChunkAccess chunk, SnowyStatusKeeper keeper, WeatherStatusKeeper weatherStatusKeeper, boolean checkIfBiomeCacheAnyMore, boolean loadingChunk) {
        // client or should be ignored
        weatherStatusKeeper.updateSnowDepthRecord(level);

        keeper.checkPosValid(chunk);

        // clear biomes should not be recorded
        if (checkIfBiomeCacheAnyMore) {
            boolean shouldClear = weatherStatusKeeper.getSnowDepthRecord().keySet().removeIf(holder -> !weatherStatusKeeper.getBiomeUse().contains(holder));
            if (shouldClear) keeper.setChange();
        }

        if (!loadingChunk && chunk instanceof LevelChunk levelChunk) {
            weatherStatusKeeper.updateAndSend(level, levelChunk);
            keeper.updateAndSend(level, levelChunk);
            keeper.getStepCount().clear();
        }
    }

    public static void forceUpdateChunkOnlyNotSnow(ServerLevel level, ChunkAccess chunk, ChunkPos chunkPos,
                                                   ChunkInfoMap chunkMap,
                                                   @Nullable BiomeHolder biomeHolder,
                                                   boolean loadingChunk) {
        if (EclipticUtil.canSnowyBlockInteract() && chunkMap != null) {
            if (biomeHolder != null) {
                SnowyStatusKeeper keeper = SnowyMapChecker.getSnowyStatusKeeper(chunk);
                if (keeper.getPosMap().isEmpty()) return;
                WeatherStatusKeeper weatherStatusKeeper = SnowyMapChecker.getWeatherStatusKeeper(chunk);
                Pair<Map<Holder<Biome>, IntIntImmutablePair>, Map<Holder<Biome>, Long>> mapMapPair = weatherStatusKeeper.collectSnowyUpdate(level, biomeHolder);
                BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
                Map<Holder<Biome>, IntIntImmutablePair> first = mapMapPair.getFirst();
                if (first != null && chunk instanceof LevelChunk levelChunk) {
                    boolean shouldUpdate = false;
                    for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); ++i) {
                        for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); ++j) {
                            checkPos.setX(i);
                            checkPos.setZ(j);
                            int solidHeight = chunkMap.getHeight(i, j);
                            checkPos.setY(solidHeight);
                            IntIntImmutablePair pair = first.getOrDefault(MapChecker.getSurfaceBiome(level, checkPos, biomeHolder), null);
                            if (pair != null && pair.leftInt() <= 0) {
                                int heightSurface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
                                for (int posH = solidHeight; posH <= heightSurface; ++posH) {
                                    checkPos.setY(posH);
                                    if (keeper.isSnowyBlock(checkPos)) {
                                        keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                                    }
                                    shouldUpdate = true;
                                }
                            }
                        }
                    }
                    if (shouldUpdate)
                        keeper.updateAndSend(level, levelChunk);
                }
            }
        }
    }

    public static boolean shouldCheckSnowyStatus(Level level, BlockPos pos) {
        return EclipticUtil.canSnowyBlockInteract() && MapChecker.isLoaded(level, pos);
    }


    public static boolean isSnowyBlock(Level level, BlockPos pos) {
        return getSnowyStatusKeeper(level.getChunkAt(pos)).isSnowyBlock(pos);
    }

    public static boolean isSnowyBlock(SnowyStatusKeeper keeper, BlockPos pos) {
        return keeper.isSnowyBlock(pos);
    }

    public static void onEntityStepOn(Entity entity, Level level, BlockPos pos, BlockState blockstate) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;
        if (!(entity instanceof LivingEntity)) return;
        if (!CommonConfig.Snow.stepMelt.get()) return;

        if (level instanceof ServerLevel serverLevel
                && MapChecker.getDefaultBlockTypeFlag(blockstate) != MapChecker.FLAG_NONE
                // && level.getRandom().nextInt(48) == 0
                && level.getGameTime() % 8 == 0
                // && level.getRandom().nextInt(15) == 0
                // && MapChecker.getHeight(level, pos) <= pos.getY()
                && SnowyMapChecker.shouldCheckSnowyStatus(serverLevel, pos)) {
            LevelChunk chunkAt = level.getChunkAt(pos);
            // SnowyMapChecker.removeSnowyStatus(serverLevel, chunkAt, pos);

            SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunkAt);
            keeper.stepAndCheck(pos);

            // clean above
            BlockPos.MutableBlockPos above = pos.mutable();
            for (int count = 1; count < 4; count++) {
                above.setY(pos.getY() + count);
                BlockState stateAbove = chunkAt.getBlockState(above);
                if (stateAbove.isEmpty()) break;
                int flagAbove = MapChecker.getDefaultBlockTypeFlag(stateAbove);
                if (flagAbove == MapChecker.FLAG_NONE) break;
                // SnowyMapChecker.removeSnowyStatus(serverLevel, chunkAt, above);
                keeper.stepAndCheck(above);
            }
        }
    }
}

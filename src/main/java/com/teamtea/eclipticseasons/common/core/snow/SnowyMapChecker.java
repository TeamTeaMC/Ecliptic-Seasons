package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class SnowyMapChecker {

    public static @NotNull SnowyStatusKeeper getSnowyStatusKeeper(ChunkAccess chunk) {
        LevelChunk levelChunk = chunk instanceof LevelChunk levelChunk1 ?
                levelChunk1 : chunk instanceof ImposterProtoChunk imposterProtoChunk ?
                imposterProtoChunk.getWrapped() : new EmptyLevelChunk(ClientCon.getUseLevel(), chunk.getPos(), ClientCon.getUseLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS));
        Optional<SnowyStatusKeeper> optionalSnowyStatusKeeper = levelChunk.getCapability(SnowyStatusKeeper.SNOWY_STATUS_KEEPER_CAPABILITY, null).resolve();
        return optionalSnowyStatusKeeper.orElseGet(SnowyStatusKeeper::create);
    }

    public static @NotNull SnowyStatusKeeper getSnowyStatusKeeperCopy(LevelChunk chunk) {
        return getSnowyStatusKeeper(chunk).clone();
    }

    public static void updatePos(Level level, LevelChunk chunk, BlockPos pos, BlockState state, BlockState oldState, Block block) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            boolean blockDestroyOrReplace = false;
            if (state.isAir()) {
                blockDestroyOrReplace = true;
            } else if (MapChecker.solidTest(state)) {
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

        int biomeDataVersion = SolarHolders.getSaveData(level).getBiomeDataVersion();
        for (ChunkHolder chunk : level.getChunkSource().chunkMap.getChunks()) {
            ChunkAccess latestChunk = chunk.getFullChunk();
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
            if (level.isClientSide) keeper.set(pos.asLong(), SnowyStatusKeeper.FLAG_NONE);
            else keeper.set(pos, SnowyStatusKeeper.FLAG_NONE);
        }
    }

    public static void setSnowyStatus(Level level, LevelChunk chunk, BlockPos pos) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunk);
            if (level.isClientSide) keeper.set(pos.asLong(), SnowyStatusKeeper.FLAG_SNOW);
            else keeper.set(pos, SnowyStatusKeeper.FLAG_SNOW);
        }
    }


    public static void forceChunkUpdateHeight(ServerLevel level,
                                              ChunkAccess chunk,
                                              ChunkInfoMap chunkMap,
                                              @Nullable BiomeHolder biomeHolder,
                                              boolean loadingChunk) {
        if (!EclipticUtil.canSnowyBlockInteract() || !CommonConfig.Snow.forceChunkUpdate.get()) return;

        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        SnowyStatusKeeper keeper = getSnowyStatusKeeper(chunk);
        boolean checkIfBiomeCacheAnyMore = false;
        Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate = keeper.collectSnowyUpdate(level, biomeHolder);

        if (chunkMap != null) {
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    checkPos.setX(i);
                    checkPos.setZ(j);
                    int k = chunkMap.getHeight(i, j);
                    checkPos.setY(k);
                    checkIfBiomeCacheAnyMore = SnowyMapChecker.isCheckIfBiomeCacheAnyMore(level, chunk, biomeHolder, biomeSnowyUpdate, checkPos, keeper, k);
                }
            }
        }
        SnowyMapChecker.postAfterChunkUpdate(level, chunk, keeper, biomeSnowyUpdate, checkIfBiomeCacheAnyMore, loadingChunk);
    }


    private static boolean isCheckIfBiomeCacheAnyMore(Level level,
                                                      ChunkAccess chunk,
                                                      BiomeHolder biomeHolder,
                                                      Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate,
                                                      BlockPos.MutableBlockPos checkPos,
                                                      SnowyStatusKeeper keeper,
                                                      int solidHeight) {
        if (EclipticUtil.canSnowyBlockInteract() && !level.isClientSide && !biomeSnowyUpdate.isEmpty()) {
            Holder<Biome> biome = MapChecker.getSurfaceBiome(level, checkPos, biomeHolder);
            keeper.getBiomeUse().add(biome);
            IntIntImmutablePair snowDepthUse = biomeSnowyUpdate.getOrDefault(biome, null);
            if (snowDepthUse != null) {
                int heightSurface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
                for (int posH = solidHeight; posH <= heightSurface; posH++) {
                    checkPos.setY(posH);
                    BlockState state = chunk.getBlockState(checkPos);
                    int flag = MapChecker.getBlockTypeFlag(level, checkPos, state);
                    if (flag != MapChecker.FLAG_NONE) {
                        if (isTooLight(level, checkPos, state, flag)) {
                            keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                        } else if (snowDepthUse.rightInt() == 0) {
                            if (snowDepthUse.leftInt() > Math.abs(state.getSeed(checkPos) % 100)) {
                                keeper.set(checkPos, SnowyStatusKeeper.FLAG_SNOW);
                            } else {
                                keeper.set(checkPos, SnowyStatusKeeper.FLAG_NONE);
                            }
                        } else {
                            int cut = snowDepthUse.rightInt() + (snowDepthUse.leftInt() - snowDepthUse.rightInt()) / 4;
                            if (cut > 0) {
                                if (cut > Math.abs(state.getSeed(checkPos) % 100)) {
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


    private static void postAfterChunkUpdate(ServerLevel level, ChunkAccess chunk, SnowyStatusKeeper keeper, Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate, boolean checkIfBiomeCacheAnyMore, boolean loadingChunk) {
        // client or should be ignored
        if (!biomeSnowyUpdate.isEmpty()) {
            keeper.updateSnowDepthRecord(biomeSnowyUpdate);
        }

        keeper.checkPosValid(chunk);

        // clear biomes should not be recorded
        if (checkIfBiomeCacheAnyMore) {
            boolean shouldClear = keeper.getSnowDepthRecord().keySet().removeIf(holder -> !keeper.getBiomeUse().contains(holder));
            if (shouldClear) keeper.setChange();
        }

        if (!loadingChunk && chunk instanceof LevelChunk levelChunk) {
            keeper.updateAndSend(level, levelChunk);
        }
    }

    @Deprecated
    public static void sendChunkLoginInfo$1_20_1(ServerLevel serverLevel, LevelChunk chunk, ChunkPos chunkPos, ServerPlayer player) {
        SnowyStatusKeeper snowyStatusKeeper = SnowyMapChecker.getSnowyStatusKeeper(chunk);
        SimpleNetworkHandler.send(player, new SnowyStatusHandler(true, snowyStatusKeeper, chunkPos));
    }

    public static boolean isTooLight(BlockAndTintGetter level, BlockPos pos, BlockState state, int blockType) {
        return isTooLight(level, pos, null, state, blockType);
    }

    public static boolean isTooLight(BlockAndTintGetter level, BlockPos pos, @Nullable BlockPos.MutableBlockPos mutableBlockPos, BlockState state, int blockType) {
        if (CommonConfig.Snow.notSnowyNearGlowingBlock.get()) {
            int aboveOffset = 1 - MapChecker.getSnowOffset(state, blockType);
            if (mutableBlockPos != null) {
                mutableBlockPos.setY(pos.getY() + aboveOffset);
                pos = mutableBlockPos;
            } else pos = pos.above(aboveOffset);
            return level.getBrightness(LightLayer.BLOCK, pos) >=
                    CommonConfig.Snow.notSnowyNearGlowingBlockLevel.get();
        }
        return false;
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
}

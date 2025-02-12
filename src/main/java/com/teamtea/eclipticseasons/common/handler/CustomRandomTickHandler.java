package com.teamtea.eclipticseasons.common.handler;


import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public final class CustomRandomTickHandler {
    private static final CustomRandomTick SNOW_MELT = (state, world, pos) ->
    {
        BlockPos blockpos = new BlockPos(pos.getX(), world.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()), pos.getZ());
        if (world.isAreaLoaded(blockpos, 1) && world.getBiome(blockpos).value().getTemperature(pos) >= 0.15F && !WeatherManager.onCheckWarmEnoughToRain(pos)) {
            BlockState topState = world.getBlockState(blockpos);
            if (topState.getBlock().equals(Blocks.SNOW)) {
                world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
            } else {
                BlockState belowState = world.getBlockState(blockpos.below());
                if (belowState.getBlock().equals(Blocks.ICE)) {
                    world.setBlockAndUpdate(blockpos.below(), Blocks.WATER.defaultBlockState());
                }
            }
        }
    };

    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END) && CommonConfig.Temperature.iceMelt.get() && !event.world.isClientSide()) {
            ServerLevel level = (ServerLevel) event.world;
            int randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            if (randomTickSpeed > 0) {
                List<ChunkHolder> list = Lists.newArrayList(((ServerLevel) level).getChunkSource().chunkMap.getChunks());
                Collections.shuffle(list);
                level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder ->
                {

                    Optional<LevelChunk> optional = chunkHolder.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
                    if (optional.isPresent()) {
                        LevelChunk chunk = optional.get();
                        for (var chunksection : chunk.getSections()) {
                            if (chunksection.isRandomlyTicking()) {
                                int i = chunk.getPos().getMinBlockX();
                                int j = chunk.getPos().getMinBlockZ();
                                BlockPos blockpos1 = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, level.getBlockRandomPos(i, 0, j, 15));
                                // BlockPos blockpos2 = blockpos1.below();
                                // Biome biome = level.getBiome(blockpos1).value();
                                if (level.isAreaLoaded(blockpos1, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
                                {
                                    for (int l = 0; l < randomTickSpeed; ++l) {
                                        if (level.getRandom().nextInt(32) == 0) {
                                            int x = blockpos1.getX();
                                            int y = blockpos1.getY();
                                            int z = blockpos1.getZ();
                                            doCustomRandomTick(level, x, y, z);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private static void doCustomRandomTick(ServerLevel world, int x, int y, int z) {
        if (CommonConfig.Temperature.iceMelt.get()) {
            SNOW_MELT.tick(null, world, new BlockPos(x, y, z));
        }
    }
}

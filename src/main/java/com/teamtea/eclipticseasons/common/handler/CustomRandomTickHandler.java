package com.teamtea.eclipticseasons.common.handler;


import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


public final class CustomRandomTickHandler {
    private static final CustomRandomTick SNOW_MELT = (state, serverLevel, pos) ->
    {
        BlockPos blockpos = new BlockPos(pos.getX(), serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()), pos.getZ());
        if (serverLevel.isAreaLoaded(blockpos, 1)
        ) {
            Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(serverLevel, pos);
            if (EclipticUtil.useSolarWeather() ?
                    WeatherManager.getSnowStatus(serverLevel, surfaceBiome, pos) == WeatherManager.SnowRenderStatus.SNOW_MELT :
                    VanillaWeather.getSnowStatus(serverLevel, surfaceBiome, pos) == WeatherManager.SnowRenderStatus.SNOW_MELT) {
                BlockPos below = blockpos.below();
                BlockState topState = serverLevel.getBlockState(blockpos);
                BlockState onState = serverLevel.getBlockState(below);
                if (topState.getBlock() == Blocks.SNOW) {
                    serverLevel.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                }
                // else if (onState.getBlock() == Blocks.SNOW_BLOCK) {
                //     serverLevel.setBlockAndUpdate(below, Blocks.AIR.defaultBlockState());
                // }
                else if (onState.getBlock() == Blocks.ICE) {
                    serverLevel.setBlockAndUpdate(below, Blocks.WATER.defaultBlockState());
                }
            } else {
                if (EclipticUtil.useSolarWeather() ?
                        WeatherManager.getRainOrSnow(serverLevel, surfaceBiome.value(), pos) == Biome.Precipitation.SNOW :
                        serverLevel.isRaining() && VanillaWeather.handlePrecipitationAt(serverLevel, surfaceBiome.value(), pos) == Biome.Precipitation.SNOW) {
                    BlockPos below = blockpos.below();
                    BlockState topState = serverLevel.getBlockState(blockpos);
                    BlockState onState = serverLevel.getBlockState(below);
                    if (topState.canBeReplaced()
                            && onState.isFaceSturdy(serverLevel, pos, Direction.UP)
                            && onState.getBlock() != Blocks.SNOW_BLOCK
                            && !onState.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
                        BlockState snowState = Blocks.SNOW.defaultBlockState();
                        if (topState.is(Blocks.SNOW)) {
                            int h = topState.getValue(SnowLayerBlock.LAYERS);
                            if (h < 8) {
                                snowState = snowState.setValue(SnowLayerBlock.LAYERS, h + 1);
                            } else {
                                // if (onState.getBlock() != Blocks.SNOW_BLOCK) {
                                //     snowState = Blocks.SNOW_BLOCK.defaultBlockState();
                                // }
                                snowState = null;
                            }
                        }
                        if (snowState != null) {
                            serverLevel.setBlockAndUpdate(blockpos, snowState);
                        }
                    } else if (topState.isAir()
                            && onState.is(Blocks.WATER)) {
                        serverLevel.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
                    }
                }
            }
        }
    };

    public static void onWorldTick(LevelTickEvent.Post event) {
        if (CommonConfig.Debug.iceMelt.get()
                && !event.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) event.getLevel();
            int randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            if (randomTickSpeed > 0) {
                level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder ->
                {
                    LevelChunk optional = chunkHolder.getTickingChunk();
                    if (optional != null) {
                        int i = optional.getPos().getMinBlockX();
                        int j = optional.getPos().getMinBlockZ();
                        for (int l = 0; l < randomTickSpeed; ++l) {
                            BlockPos blockRandomPos = level.getBlockRandomPos(i, 0, j, 15);
                            BlockPos blockpos1 = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockRandomPos);
                            if (level.isAreaLoaded(blockpos1, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
                            {
                                {
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
                });
            }
        }
    }

    private static void doCustomRandomTick(ServerLevel serverLevel, int x, int y, int z) {
        if (CommonConfig.Debug.iceMelt.get()) {
            SNOW_MELT.tick(null, serverLevel, new BlockPos(x, y, z));
        }
    }
}

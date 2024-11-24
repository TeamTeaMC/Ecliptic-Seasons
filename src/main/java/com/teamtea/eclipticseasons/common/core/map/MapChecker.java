package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.message.ChunkUpdateMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.*;

public class MapChecker {
    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 + 5;

    public static final Map<Level, List<ChunkInfoMap>> REGION_LIST_COLLECTOR = new IdentityHashMap<>();
    public static List<ChunkInfoMap> CLIENT_REGION_LIST = new ArrayList<>();
    public static final int FLAG_NONE = 0;
    public static final int FLAG_BLOCK = 1;
    public static final int FLAG_SLAB = 2;
    public static final int FLAG_STAIRS = 3;
    public static final int FLAG_STAIRS_TOP = 301;
    public static final int FLAG_LEAVES = 4;
    public static final int FLAG_GRASS = 5;
    public static final int FLAG_GRASS_LARGE = 501;
    public static final int FLAG_FARMLAND = 6;
    public static final int FLAG_CUSTOM = 999;


    public static Set<ChunkPos> dirtyList = new HashSet<>();

    //  unload some
    public static void unloadLevel(Level level) {
        // updateLock = true;
        List<ChunkInfoMap> orDefault = getMapsList(level);
        synchronized (orDefault) {
            orDefault.clear();
        }
        REGION_LIST_COLLECTOR.remove(level);

        dirtyList.clear();
        // updateLock = false;
    }

    public static boolean unloadChunk(Level level, ChunkPos chunkPos) {
        int x0 = chunkPos.getMinBlockX();
        int x1 = chunkPos.getMaxBlockX();
        int z0 = chunkPos.getMinBlockZ();
        int z1 = chunkPos.getMaxBlockZ();

        int x = blockToSectionCoord(x0);
        int z = blockToSectionCoord(z0);
        ChunkInfoMap map = getChunkMap(level, x, z);

        if (map != null) {
            for (int i = x0; i < x1 + 1; i++) {
                for (int j = z0; j < z1 + 1; j++) {
                    map.updateHeight(i, j, map.minY);
                }
            }
            dirtyList.remove(chunkPos);
            return true;
        }


        return false;
    }


    // 获取chunk位置
    public static int blockToSectionCoord(int i) {
        return i >> ChunkSizeAxis;
    }

    public static void addDirtyChunk(ChunkPos chunkPos) {
        dirtyList.add(chunkPos);
    }

    public static boolean isChunkDirty(ChunkPos chunkPos) {
        return dirtyList.contains(chunkPos);
    }

    public static boolean removeDirtyChunk(ChunkPos chunkPos) {
        return dirtyList.remove(chunkPos);
    }


    public static List<ChunkInfoMap> getMapsList(Level level) {
        List<ChunkInfoMap> chunkInfoMaps = REGION_LIST_COLLECTOR.computeIfAbsent(level, level1 -> new ArrayList<>());
        if (level.isClientSide()) CLIENT_REGION_LIST = chunkInfoMaps;
        return chunkInfoMaps;
    }

    public static ChunkInfoMap getChunkMap(Level level, BlockPos pos) {
        int x = blockToSectionCoord(pos.getX());
        int z = blockToSectionCoord(pos.getZ());
        return getChunkMap(level, x, z);
    }


    public static ChunkInfoMap getChunkMap(Level level, int regionX, int regionZ) {
        return getChunkMap(
                level.isClientSide ? CLIENT_REGION_LIST :
                        getMapsList(level), regionX, regionZ);
    }

    public static ChunkInfoMap getChunkMap(List<ChunkInfoMap> orDefault, int regionX, int regionZ) {
        ChunkInfoMap map = null;
        // try{
        // while (updateLock) {
        //     try {
        //         Thread.sleep(1);
        //     } catch (InterruptedException e) {
        //     }
        // }
        for (int i = 0; i < orDefault.size(); i++) {
            var chunkHeightMap = orDefault.get(i);
            if (chunkHeightMap.x == regionX && chunkHeightMap.z == regionZ) {
                map = chunkHeightMap;
                break;
            }
        }
        return map;
    }

    public static int getMCHeightWithCheck(Level level, BlockPos pos) {
        if (level.getChunkAt(pos) instanceof LevelChunk levelChunk) {
            if (levelChunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)
                    && levelChunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER) instanceof SnowyRemover snowyRemover) {
                if (snowyRemover.notSnowyAt(pos)) {
                    return level.getMaxBuildHeight() + 1;
                }
            }
        }
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() - 1;
    }

    public static int getHeight(Level levelNull, BlockPos pos) {
        return getHeightOrUpdate(levelNull, pos, false);
    }

    public static int getHeightOrUpdate(Level levelNull, BlockPos pos, boolean forceUpdate) {
        return getSurfaceOrUpdate(levelNull, pos, forceUpdate, ChunkInfoMap.TYPE_HEIGHT);
    }


    // Level is not Nullable but we can not sure
    public static int getSurfaceOrUpdate(Level level, BlockPos pos, boolean forceUpdate, int type) {
        if (level == null) return 0;
        if (!isValidDimension(level)) {
            switch (type) {
                case ChunkInfoMap.TYPE_BIOME -> {
                    return 0;
                }
                case ChunkInfoMap.TYPE_HEIGHT -> {
                    return level.getMinBuildHeight() - 1;
                }
            }
        }

        int x = blockToSectionCoord(pos.getX());
        int z = blockToSectionCoord(pos.getZ());
        List<ChunkInfoMap> mapsList = getMapsList(level);
        ChunkInfoMap map = getChunkMap(mapsList, x, z);

        int value = 0;
        if (map != null) {
            if (type == ChunkInfoMap.TYPE_HEIGHT) {
                value = map.getHeight(pos);
                if (value <= map.minY || forceUpdate) {
                    var rh = getMCHeightWithCheck(level, pos);
                    map.updateHeight(pos, rh);
                    value = rh;
                }
            } else if (type == ChunkInfoMap.TYPE_BIOME) {
                value = map.getBiome(pos);
                if (value == -1 || forceUpdate) {
                    if (level.isLoaded(pos)) {
                        var rh = level.registryAccess().registryOrThrow(Registries.BIOME).getId(level.getBiome(pos).value());
                        map.updateBiome(pos, rh);
                        value = rh;
                    } else {
                        value = level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID);
                    }
                }
            }
        } else {
            // updateLock = true;
            synchronized (mapsList) {
                boolean hasBuild = false;
                for (ChunkInfoMap chunkHeightMap : mapsList) {
                    if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                        hasBuild = true;
                        map = chunkHeightMap;
                        break;
                    }
                }
                if (!hasBuild) {
                    // level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID)
                    map = new ChunkInfoMap(x, z, level.getMinBuildHeight() - 1, level.isClientSide);
                    mapsList.add(map);
                }
            }
            // updateLock = false;

            if (type == ChunkInfoMap.TYPE_HEIGHT) {
                value = getMCHeightWithCheck(level, pos);
                map.updateHeight(pos, value);
            } else if (type == ChunkInfoMap.TYPE_BIOME) {
                if (level.isLoaded(pos)) {
                    value = level.registryAccess().registryOrThrow(Registries.BIOME).getId(level.getBiome(pos).value());
                    map.updateBiome(pos, value);
                }
            }
        }
        return value;
    }

    public static void updatePosForce(Level level, BlockPos setPos, int y) {
        ChunkInfoMap map = getChunkMap(level, setPos);
        if (map != null)
            map.updateHeight(setPos, y);
    }


    public static boolean notLightAbove(Level level, BlockPos pos, int times) {
        var abovePos = pos.above();
        if (level.isLoaded(abovePos)) {
            BlockState stateAbove = null;
            // TODO: add this for I'm not know if we will crash for logic world change but not render section change
            try {
                stateAbove = level.getBlockState(abovePos);
            } catch (Exception e) {
                EclipticSeasons.LOGGER.error("Logic thread change the block in render thread with {}", pos);
                return true;
            }
            if (stateAbove.getBlock() instanceof LightBlock) {
                if (stateAbove.getValue(LightBlock.LEVEL) == 0)
                    return false;
            } else if (!stateAbove.isAir() && !stateAbove.blocksMotion()) {
                if (times > 0)
                    return notLightAbove(level, abovePos, (times - 1));
            }
        }
        return true;
    }


    public static boolean shouldSnowAt(Level level, BlockPos pos, BlockState state, RandomSource random, long seed) {
        var biomeHolder = getSurfaceBiome(level, pos);
        boolean isSnowy = false;
        if (WeatherManager.getSnowDepthAtBiome(level, biomeHolder.value()) > Math.abs(seed % 100)) {
            if (ServerConfig.Debug.notLightAbove.get()) {
                isSnowy = notLightAbove(level, pos, 4);
            } else isSnowy = true;
        }
        return isSnowy;
    }


    public static boolean shouldSnowAt(Level level, BlockPos pos, int biomeId, BlockState state, RandomSource random, long seed) {
        boolean isSnowy = false;
        if (WeatherManager.getBiomeList(level).get(biomeId).snowDepth > Math.abs(seed % 100)) {
            if (ServerConfig.Debug.notLightAbove.get()) {
                isSnowy = notLightAbove(level, pos, 4);
            } else isSnowy = true;
        }
        return isSnowy;
    }

    public static boolean shouldSnowAtBiome(Level level, Biome biome, BlockState state, RandomSource random, long seed) {
        if (WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100)) {
            return true;
        }
        return false;
    }

    public static boolean isSmallBiome(Holder<Biome> biomeHolder) {
        // return biomeHolder.is(Tags.Biomes.IS_RIVER)
        //         || biomeHolder.is(Tags.Biomes.IS_BEACH)
        // || biomeHolder.is(Tags.Biomes.IS_OCEAN)
        ;
        return BiomeClimateManager.SMALL_BIOME_MAP.containsKey(biomeHolder.value());
    }

    public static Holder<Biome> idToBiome(Level level, int id) {
        var list = WeatherManager
                .getBiomeList(level);
        return
                list != null ?
                        list.get(id).biomeHolder :
                        level.registryAccess()
                                .registry(Registries.BIOME)
                                .flatMap(registry -> registry.getHolder(id))
                                .orElse(null);
    }

    // TODO：检查污染情况
    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos) {
        // fix the pos to surface
        int y = getHeight(level, pos) + 1;
        if (y > level.getMaxBuildHeight()) {
            y = (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()));
        }
        if (y != pos.getY()) {
            pos = new BlockPos(pos.getX(), y, pos.getZ());
        }
        int bid = getSurfaceOrUpdate(level, pos, false, ChunkInfoMap.TYPE_BIOME);
        var biome = idToBiome(level, bid);
        int i = 0;
        while (isSmallBiome(biome)) {
            i += 1;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                bid = getSurfaceOrUpdate(level, pos.relative(direction, i), false, ChunkInfoMap.TYPE_BIOME);
                biome = idToBiome(level, bid);
                if (!isSmallBiome(biome)) {
                    ChunkInfoMap chunkMap = getChunkMap(level, pos);
                    if (chunkMap != null) {
                        chunkMap.updateBiome(pos, bid);
                    }
                    break;
                }
            }
        }

        // var biome = level.getBiome(pos);
        // int i = 0;
        // while (isSmallBiome(biome)) {
        //     i += 1;
        //     for (Direction direction : Direction.Plane.HORIZONTAL) {
        //         // if (level.isLoaded(pos.relative(direction, i)))
        //         {
        //             biome = level.getBiome(pos.relative(direction, i));
        //             if (!isSmallBiome(biome)) {
        //                 break;
        //             }
        //         }
        //     }
        // }
        return biome;
    }

    // 注意这个写法可能会导致重复
    public static Map<BlockState, Integer> blockTypeCache = new IdentityHashMap<>(4096);

    // TODO: 注意全部加上缓存
    public static int getBlockType(BlockState state, BlockGetter level, BlockPos pos) {
        int flag = FLAG_NONE;
        // 不知道为啥这里会有null

        Block onBlock = state.getBlock();
        if (!ServerConfig.Debug.snowOverlayGlowingBlock.getAsBoolean()
                && state.getLightEmission(level, pos) > 0) {
            flag = FLAG_NONE;
        } else if (onBlock instanceof LeavesBlock) {
            flag = FLAG_LEAVES;
        } else if (onBlock == Blocks.GRASS_BLOCK ||
                onBlock == Blocks.DIRT ||
                onBlock == Blocks.STONE ||
                onBlock == Blocks.SAND) {
            flag = FLAG_BLOCK;
        } else if (onBlock == Blocks.SHORT_GRASS || onBlock == Blocks.FERN) {
            flag = FLAG_GRASS;
        } else if (onBlock == Blocks.TALL_GRASS || onBlock == Blocks.LARGE_FERN) {
            flag = FLAG_GRASS_LARGE;
        } else if ((
                onBlock instanceof FarmBlock ||
                        onBlock instanceof DirtPathBlock)) {
            flag = FLAG_FARMLAND;
        } else if (onBlock instanceof TrapDoorBlock ||
                (onBlock instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) ||
                onBlock instanceof FenceBlock ||
                onBlock instanceof FenceGateBlock ||
                onBlock instanceof WallBlock ||
                onBlock instanceof BellBlock ||
                onBlock instanceof ComposterBlock ||
                (onBlock instanceof CampfireBlock && !state.getValue(CampfireBlock.LIT)) ||
                // onBlock instanceof CauldronBlock ||
                // onBlock instanceof DaylightDetectorBlock||
                // onBlock instanceof AnvilBlock||
                // onBlock instanceof BasePressurePlateBlock||
                onBlock instanceof HoneyBlock ||
                onBlock instanceof IronBarsBlock ||
                onBlock instanceof LightningRodBlock ||
                // onBlock instanceof LecternBlock ||
                onBlock instanceof SlimeBlock ||
                onBlock instanceof AzaleaBlock) {
            flag = FLAG_CUSTOM;
        } else {
            Integer realFlag = blockTypeCache.getOrDefault(state, FLAG_NONE - 1);
            if (realFlag == null) {
                EclipticSeasons.logger("Null number get from %s".formatted(state));
                blockTypeCache.remove(state);
            } else {
                flag = realFlag;
            }
            if (flag < FLAG_NONE) {
                flag = FLAG_NONE;

                ResourceLocation blockName = BuiltInRegistries.BLOCK.getKey(onBlock);
                if (!ServerConfig.Debug.disableSnowOverlayControlTag.getAsBoolean()
                        && state.is(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)) {
                    flag = FLAG_NONE;
                } else if ((
                        (ServerConfig.Debug.snowyFullCollisionShape.getAsBoolean() ?
                                Block.isShapeFullBlock(state.getCollisionShape(level, pos)) :
                                state.isSolidRender(level, pos))
                                // state.isSolid()
                                || onBlock instanceof LeavesBlock
                )) {
                    flag = FLAG_BLOCK;
                } else if (onBlock instanceof SlabBlock) {
                    SlabType value = state.getValue(SlabBlock.TYPE);
                    if (value == SlabType.TOP) {
                        flag = FLAG_STAIRS_TOP;
                    } else if (value == SlabType.BOTTOM) {
                        flag = FLAG_SLAB;
                    } else flag = FLAG_BLOCK;
                    // flag = FLAG_CUSTOM;
                } else if (onBlock instanceof StairBlock) {
                    if (state.getValue(StairBlock.HALF) == Half.TOP)
                        flag = FLAG_STAIRS_TOP;
                    else flag = FLAG_STAIRS;
                    // flag = FLAG_CUSTOM;
                } else {
                    if ((
                            blockName.getPath().endsWith("wall")
                                    || blockName.getPath().endsWith("table")
                                    || blockName.getPath().endsWith("aqueduct")
                                    || blockName.getPath().endsWith("field")
                                    || blockName.getPath().endsWith("lattice")
                                    // || blockName.getPath().endsWith("_trellis")
                                    || blockName.getPath().endsWith("_vine")
                                    || blockName.getPath().endsWith("fence")
                                    || blockName.getPath().startsWith("ramp")
                    )
                    ) {
                        flag = FLAG_CUSTOM;
                    }
                }

                if (blockName.toString().equals("xkdeco:dirt_path_slab"))
                    flag = FLAG_CUSTOM;

                Integer otherFlag = blockTypeCache.putIfAbsent(state, flag);
                if (otherFlag != null && otherFlag != flag) {
                    EclipticSeasons.logger("WARNING state %s expected %s but found %s".formatted(state, flag, otherFlag));
                }
            }
        }
        return flag;
    }

    public static int getSnowOffset(BlockState state, int flag) {
        int offset = 0;
        if (flag == FLAG_GRASS || flag == FLAG_GRASS_LARGE) {
            if (flag == FLAG_GRASS) {
                offset = 1;
            }
            // 这里不忽略这个警告，因为后续会有优化
            else if (flag == FLAG_GRASS_LARGE) {
                if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    offset = 1;
                } else {
                    offset = 2;
                }
            }
        } else if (flag == FLAG_CUSTOM) {
            if (state.getBlock() instanceof AzaleaBlock)
                offset = 1;
        }
        return offset;
    }

    public static List<Holder<Biome>> getBiomes(Level level, BlockPos pos) {
        var mPos = new BlockPos.MutableBlockPos(pos.getX(),
                level.getMaxBuildHeight(),
                // level.getHeight(Heightmap.Types.MOTION_BLOCKING,pos.getX(),pos.getZ()),
                pos.getZ());

        var list = new ArrayList<Holder<Biome>>();
        while (mPos.getY() >= level.getMinBuildHeight()) {
            list.add(level.getBiome(mPos));
            mPos = mPos.move(Direction.DOWN);
        }
        return list;
    }


    public static boolean isValidDimension(@Nullable Level level) {
        return level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime()
                && ServerConfig.Season.validDimensions.get().contains(level.dimension().location().toString());
    }

    public static void sendChunkInfo(LevelChunk chunk, ChunkPos chunkPos, ServerPlayer player, List<Integer> section_y, List<BlockPos> clickedPos) {
        byte[] bytes = new byte[256];
        // var section_y = new HashSet<Integer>(chunk.getSectionsCount());
        // var section_y=new HashSet<Integer>();

        if (chunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)
                && chunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER) instanceof SnowyRemover snowyRemover) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 64, chunkPos.getMinBlockZ());
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    bytes[i * 16 + j] = (byte) snowyRemover.blockWatcher()[i][j];

                    // if (forceChunkRender) {
                    //     mutableBlockPos.set(chunkPos.getMinBlockX() + i, 64, chunkPos.getMinBlockZ() + j);
                    //     section_y.add(SectionPos.blockToSectionCoord(getHeightOrUpdate(chunk.getLevel(), mutableBlockPos)));
                    // }
                }
            }
        }
        SimpleNetworkHandler.send(player, new ChunkUpdateMessage(bytes, chunk.getPos().x, chunk.getPos().z, section_y, clickedPos));
    }


}

package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapChecker {

    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 + 5;

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

    public static final List<Level> validDimension = new ArrayList<>();

    public static List<Block> LowerPlant = Stream.of(Blocks.GRASS, Blocks.FERN).collect(Collectors.toList());
    public static List<Block> LARGE_GRASS = Stream.of(Blocks.TALL_GRASS, Blocks.LARGE_FERN).collect(Collectors.toList());


    public static final ArrayList<ChunkInfoMap> RegionList = new ArrayList<>(4);

    private static boolean updateLock;


    public static boolean isSmallBiome(Holder<Biome> biomeHolder) {
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

    public static int biomeToId(Level level, Biome b) {
        return level.registryAccess().registryOrThrow(Registries.BIOME).getId(b);
    }

    public static final SimplePair<Direction, Direction>[] SMALL_OFFSET_DIRECTIONS = new SimplePair[]{
            SimplePair.of(Direction.NORTH, null),
            SimplePair.of(Direction.NORTH, Direction.EAST),
            SimplePair.of(Direction.EAST, null),
            SimplePair.of(Direction.EAST, Direction.SOUTH),
            SimplePair.of(Direction.SOUTH, null),
            SimplePair.of(Direction.SOUTH, Direction.WEST),
            SimplePair.of(Direction.WEST, null),
            SimplePair.of(Direction.WEST, Direction.NORTH)
    };


    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos) {
        // fix the pos to surface
        ChunkInfoMap chunkMap1 = getChunkMap(level, pos);

        Holder<Biome> biome = null;
        int bid = 0;
        int y = 0;
        if (chunkMap1 != null) {
            bid = chunkMap1.getBiome(pos);
            if (bid > -1) {
                biome = idToBiome(level, bid);
                if (isSmallBiome(biome)) {
                    y = getHeight(level, pos) + 1;
                    if (y > level.getMaxBuildHeight()) {
                        y = (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()));
                    }
                }
            }
        }

        if (biome == null) {
            y = getHeight(level, pos) + 1;
            if (y > level.getMaxBuildHeight()) {
                y = (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()));
            }
            pos = new BlockPos(pos.getX(), y, pos.getZ());
            bid = getSurfaceOrUpdate(level, pos, false, ChunkInfoMap.TYPE_BIOME);
            biome = idToBiome(level, bid);
        }

        if (biome == null)
            biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.THE_VOID);

        BlockPos.MutableBlockPos relative = null;

        int i = 0;
        int last_ii = 0;
        boolean shouldBreak = false;
        while (isSmallBiome(biome)) {
            // if(true)break;
            if (relative == null) {
                relative = new BlockPos.MutableBlockPos(
                        pos.getX(), y, pos.getZ()
                );
            }
            i += 1;
            for (SimplePair<Direction, Direction> pair : SMALL_OFFSET_DIRECTIONS) {
                // BlockPos relative = pos.relative(pair.getKey(), i);

                if (pair.getValue() != null) {
                    // relative = relative.relative(pair.getValue(), i);
                    // 这里需要是1，否则锯齿
                    int ii;
                    // ii = (int) Mth.sqrt(i) + 1;
                    // ii=i*3/4;
                    ii = i - 1;
                    if (
                        // i == 1 ||
                            ii == last_ii)
                        continue;
                    relative.move(pair.getKey(), ii);
                    relative.move(pair.getValue(), ii);
                    last_ii = ii;
                } else {
                    relative.move(pair.getKey(), i);
                }
                if (chunkMap1 != null) {
                    int x = blockToRegionCoord(relative.getX());
                    int z = blockToRegionCoord(relative.getZ());
                    if (chunkMap1.getX() == x && chunkMap1.getZ() == z)
                        bid = chunkMap1.getBiome(relative);
                }
                if (bid < 0) {
                    y = getHeight(level, relative) + 1;
                    if (y > level.getMaxBuildHeight()) {
                        y = (level.getHeight(Heightmap.Types.MOTION_BLOCKING, relative.getX(), relative.getZ()));
                    }
                    relative.setY(y);
                    bid = getSurfaceOrUpdate(level, relative, false, ChunkInfoMap.TYPE_BIOME);
                }
                biome = idToBiome(level, bid);
                if (!isSmallBiome(biome)) {
                    // 不再保存，避免累进。
                    // ChunkInfoMap chunkMap = getChunkMap(level, pos);
                    // if (chunkMap != null) {
                    //     if (isLoadNearBy(level, relative))
                    //         chunkMap.updateBiome(pos, bid);
                    // }
                    shouldBreak = true;
                    break;
                } else {
                    relative.setX(pos.getX());
                    relative.setZ(pos.getZ());
                }
            }

            if (shouldBreak || i > 128) break;
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

        // if(biome.is(Biomes.PLAINS)){
        //     EclipticSeasons.logger(level.getBiome(pos));
        // }
        return biome;
    }

    /**
     * 由于Minecraft区块由噪声确定QuartPos里的每个BlockPos的准确群系，因此需要判断临近区块是否加载。
     */
    public static boolean isLoadNearBy(Level level, BlockPos pos) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        if (level.isOutsideBuildHeight(pos))
            return false;
        // for (int i = -1; i < 2; i++) {
        //     for (int j = -1; j < 2; j++) {
        //         if (!level.getChunkSource()
        //                 .hasChunk(chunkX, chunkZ))
        //             return false;
        //     }
        // }

        // TODO:似乎都是+1，那么就是+1两个方向查询即可
        int i1 = (pos.getX() & 15) - 2;
        int l1 = (pos.getZ() & 15) - 2;
        int xe = ((i1) >> 2) > 2 ? 1 : 0;
        int ze = ((l1) >> 2) > 2 ? 1 : 0;
        int xs = i1 < 2 ? -1 : 0;
        int zs = l1 < 2 ? -1 : 0;
        ChunkSource chunkSource = level.getChunkSource();
        for (int i = xs; i <= xe; i++) {
            for (int j = zs; j <= ze; j++) {
                if (!chunkSource
                        .hasChunk(chunkX + i, chunkZ + j))
                    return false;
            }
        }
        // if(level.getBiome(pos).is(Biomes.PLAINS)){
        //     EclipticSeasons.logger(pos);
        // }
        return true;
    }


    public static boolean isValidDimension(@Nullable Level level) {
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        if (result) {
            // fori faster than enhanced for
            for (int i = 0; i < validDimension.size(); i++) {
                if (validDimension.get(i) == level) return true;
            }
            // for (Level value : validDimension) {
            //     if (value == level) return true;
            // }
        }
        return false;
    }

    public static boolean checkLightAbove(Level level, BlockPos pos, int times) {
        var abovePos = pos.above();
        if (level.isLoaded(abovePos)) {
            var stateAbove = level.getBlockState(abovePos);
            if (stateAbove.getBlock() instanceof LightBlock) {
                if (stateAbove.getValue(LightBlock.LEVEL) == 0)
                    return true;
            } else if (!stateAbove.isAir() && !stateAbove.blocksMotion()) {
                if (times > 0)
                    return checkLightAbove(level, pos, (times - 1));
            }
        }
        return false;
    }

    public static boolean shouldSnowAt(Level level, BlockPos pos, BlockState state, RandomSource random, long seed) {
        var biomeHolder = getSurfaceBiome(level, pos);
        if (WeatherManager.getSnowDepthAtBiome(level, biomeHolder.value()) > Math.abs(seed % 100)) {
            if (CommonConfig.Debug.notLightAbove.get()) {
                // 这里检查三次
                if (checkLightAbove(level, pos, 4)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean shouldSnowAtBiome(Level level, Biome biome, BlockState state, RandomSource random, long seed) {
        if (WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100)) {
            return true;
        }
        return false;
    }

    public static void unloadLevel(Level level) {
        updateLock = true;
        synchronized (RegionList) {
            RegionList.clear();
        }
        updateLock = false;
    }

    // 获取chunk位置
    public static int blockToRegionCoord(int i) {
        return i >> ChunkSizeAxis;
    }

    public static int getHeight(Level levelNull, BlockPos pos) {
        return getHeightOrUpdate(levelNull, pos, false);
    }

    public static int getHeightOrUpdate(Level levelNull, BlockPos pos, boolean forceUpdate) {
        return getSurfaceOrUpdate(levelNull, pos, forceUpdate, ChunkInfoMap.TYPE_HEIGHT);
    }

    public static boolean unloadChunk(Level level, ChunkPos chunkPos) {
        int x0 = chunkPos.getMinBlockX();
        int x1 = chunkPos.getMaxBlockX();
        int z0 = chunkPos.getMinBlockZ();
        int z1 = chunkPos.getMaxBlockZ();

        int x = blockToRegionCoord(x0);
        int z = blockToRegionCoord(z0);
        ChunkInfoMap map = getChunkMap(level, x, z);

        if (map != null) {
            for (int i = x0; i < x1 + 1; i++) {
                for (int j = z0; j < z1 + 1; j++) {
                    map.updateHeight(i, j, map.minY);
                }
            }
            return true;
        }
        return false;

    }

    public static ChunkInfoMap getChunkMap(Level level, BlockPos pos) {
        int x = blockToRegionCoord(pos.getX());
        int z = blockToRegionCoord(pos.getZ());
        return getChunkMap(level, x, z);
    }

    public static ChunkInfoMap getChunkMap(Level level, int regionX, int regionZ) {
        ChunkInfoMap map = null;
        // try{
        while (updateLock) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        // map = RegionList.stream()
        //         .filter(chunkHeightMap -> chunkHeightMap.regionX == regionX && chunkHeightMap.regionZ == regionZ)
        //         .findFirst()
        //         .orElse(null);
        // size add is dangerous
        for (int i = 0; i < RegionList.size(); i++) {
            var chunkHeightMap = RegionList.get(i);
            if (chunkHeightMap.x == regionX && chunkHeightMap.z == regionZ) {
                map = chunkHeightMap;
                break;
            }
        }
        return map;
    }

    private static int getHeightWithCheck(Level level, BlockPos pos) {
        // if (level.getChunkAt(pos) instanceof LevelChunk levelChunk) {
        //     if (levelChunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)
        //             && levelChunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER) instanceof SnowyRemover snowyRemover) {
        //         if (snowyRemover.notSnowyAt(pos)) {
        //             return level.getMaxBuildHeight() + 1;
        //         }
        //     }
        // }
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() - 1;
    }

    public static int getSurfaceOrUpdate(Level level, BlockPos pos, boolean forceUpdate, int type) {
        if (level == null)
            return 0;

        int x = blockToRegionCoord(pos.getX());
        int z = blockToRegionCoord(pos.getZ());
        ChunkInfoMap map = getChunkMap(level,x, z);

        int value = 0;
        if (map != null) {
            if (type == ChunkInfoMap.TYPE_HEIGHT) {
                value = map.getHeight(pos);
                if (value <= map.minY || forceUpdate) {
                    var rh = getHeightWithCheck(level, pos);
                    map.updateHeight(pos, rh);
                    value = rh;
                }
            } else if (type == ChunkInfoMap.TYPE_BIOME) {
                value = map.getBiome(pos);
                if (value == -1 || forceUpdate) {
                    value = biomeToId(level,level.getBiome(pos).value());
                    if (isLoadNearBy(level, pos)) {
                        map.updateBiome(pos, value);
                    }
                }
            }
        } else {
            updateLock = true;
            synchronized (RegionList) {
                boolean hasBuild = false;
                for (ChunkInfoMap chunkHeightMap : RegionList) {
                    if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                        hasBuild = true;
                        map = chunkHeightMap;
                        break;
                    }
                }
                if (!hasBuild) {
                    // level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID)
                    map = new ChunkInfoMap(x, z, level.getMinBuildHeight() - 1);
                    RegionList.add(map);
                }
            }
            updateLock = false;

            if (type == ChunkInfoMap.TYPE_HEIGHT) {
                value = getHeightWithCheck(level, pos);
                map.updateHeight(pos, value);
            } else if (type == ChunkInfoMap.TYPE_BIOME) {
                value = biomeToId(level,level.getBiome(pos).value());
                if (isLoadNearBy(level, pos)) {
                    map.updateBiome(pos, value);
                }
            }
        }
        return value;
    }


    // 注意这个写法可能会导致重复
    public static Map<BlockState, Integer> blockTypeCache = new IdentityHashMap<>();

    public static int getBlockType(BlockState state, BlockGetter level, BlockPos pos) {
        int flag = FLAG_NONE;
        // 不知道为啥这里会有null
        Integer realFlag = blockTypeCache.getOrDefault(state, FLAG_NONE - 1);
        if (realFlag == null) {
            if(CommonConfig.Debug.logIllegalUse.get())
                EclipticSeasons.logger("Null number get from %s".formatted(state));
            blockTypeCache.remove(state);
        } else {
            flag = realFlag;
        }
        if (flag < FLAG_NONE) {
            flag = FLAG_NONE;
            var onBlock = state.getBlock();
            if (!CommonConfig.Debug.snowOverlayGlowingBlock.get()
                    && state.getLightEmission(level, pos) > 0) {
                flag = FLAG_NONE;
            } else if (onBlock instanceof LeavesBlock) {
                flag = FLAG_LEAVES;
            } else if ((
                    (CommonConfig.Debug.snowyFullCollisionShape.get() ?
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
            } else if (onBlock instanceof StairBlock) {
                if (state.getValue(StairBlock.HALF) == Half.TOP)
                    flag = FLAG_STAIRS_TOP;
                else flag = FLAG_STAIRS;
            } else if (LowerPlant.contains(onBlock)) {
                flag = FLAG_GRASS;
            } else if (LARGE_GRASS.contains(onBlock)) {
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
                    onBlock instanceof IronBarsBlock ||
                    onBlock instanceof LightningRodBlock ) {
                flag = FLAG_CUSTOM;
            }
            Integer otherFlag = blockTypeCache.putIfAbsent(state, flag);
            if (otherFlag != null && otherFlag != flag) {
                EclipticSeasons.logger("WARNING state %s expected %s but found %s".formatted(state, flag, otherFlag));
            }
        }
        return flag;
    }


    public static boolean isReplacedType(BlockState state, int flag) {
        return flag == FLAG_GRASS
                || flag == FLAG_GRASS_LARGE;
    }

    public static int getSnowOffset(BlockState state, int flag) {
        int offset = 0;
        if (flag == MapChecker.FLAG_GRASS || flag == MapChecker.FLAG_GRASS_LARGE) {
            if (flag == MapChecker.FLAG_GRASS) {
                offset = 1;
            }
            // 这里不忽略这个警告，因为后续会有优化
            else if (flag == MapChecker.FLAG_GRASS_LARGE) {
                if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    offset = 1;
                } else {
                    offset = 2;
                }
            }
        }
        return offset;
    }

    public static void updatePosForce(Level level, BlockPos setPos, int y) {
        int x = MapChecker.blockToRegionCoord(setPos.getX());
        int z = MapChecker.blockToRegionCoord(setPos.getZ());
        ChunkInfoMap map = MapChecker.getChunkMap(level,x, z);
        if (map != null)
            map.updateHeight(setPos, y);
    }

}

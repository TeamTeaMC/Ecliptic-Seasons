package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import com.teamtea.eclipticseasons.common.network.message.ChunkUpdateMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MapChecker {
    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 + 5;

    public static final List<Level> validDimension = new ArrayList<>();
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
        validDimension.removeIf(level1 -> level1 == level);
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
                    if (isLoadNearBy(level, pos)) {
                        // TODO:这里是因为客户端level.getUncachedNoiseBiome的获取问题
                        var biomeHolder = level.getBiome(pos);
                        // TODO: 调查清楚两者区别
                        // var biomeHolder = level.getChunk(pos).getNoiseBiome(pos.getX(),pos.getY(),pos.getZ());
                        value = level.registryAccess().registryOrThrow(Registries.BIOME).getId(biomeHolder.value());
                        map.updateBiome(pos, value);
                    } else {
                        // value = level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID);
                        var biomeHolder = level.getBiome(pos);
                        value = level.registryAccess().registryOrThrow(Registries.BIOME).getId(biomeHolder.value());
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
                if (isLoadNearBy(level, pos)) {
                    var biomeHolder = level.getBiome(pos);
                    value = level.registryAccess().registryOrThrow(Registries.BIOME).getId(biomeHolder.value());
                    map.updateBiome(pos, value);
                } else {
                    // value = level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID);
                    var biomeHolder = level.getBiome(pos);
                    value = level.registryAccess().registryOrThrow(Registries.BIOME).getId(biomeHolder.value());
                }
            }
        }
        // if (type == ChunkInfoMap.TYPE_BIOME && idToBiome(level, value).is(Biomes.PLAINS)) {
        //     // return 0;
        //     EclipticSeasons.logger(pos, isLoadNearBy(level, pos), WorldRenderer.isSectionLoad(SectionPos.of(pos), 2));
        // }

        return value;
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

    public static boolean isSmallBiome(@Nonnull Holder<Biome> biomeHolder) {
        // return BiomeClimateManager.SMALL_BIOME_MAP.containsKey(biomeHolder.value());
        return ((IBiomeTagHolder) (Object) biomeHolder.value()).eclipticSeasons$isSmallBiome();
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

    // TODO：检查污染情况，这里使用生成时内容
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
            biome = level.registryAccess().holderOrThrow(Biomes.THE_VOID);

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
                    ii=i-1;
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
                    int x = blockToSectionCoord(relative.getX());
                    int z = blockToSectionCoord(relative.getZ());
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
        boolean result = level != null
                && level.dimensionType().natural()
                && !level.dimensionType().hasFixedTime();
        if (result) {
            for (int i = 0; i < validDimension.size(); i++) {
                if (validDimension.get(i) == level) return true;
            }
            // for (Level value : validDimension) {
            //     if (value == level) return true;
            // }
        }
        return false;
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

    public static void sendChunkLoginInfo(ServerLevel serverLevel, LevelChunk chunk, ChunkPos chunkPos, ServerPlayer player) {
        int[] bytes = new int[256];
        // if (chunk.hasData(EclipticSeasons.ModContents.BIOME_HOLDER)
        //         && chunk.getData(EclipticSeasons.ModContents.BIOME_HOLDER) instanceof BiomeHolder biomeHolder) {
        //     biomeHolder.fillArray(bytes, serverLevel, chunkPos);
        // }
        boolean filled = new BiomeHolder(bytes, false).fillArray(bytes, serverLevel, chunkPos);
        if (filled)
            SimpleNetworkHandler.send(player, new ChunkBiomeUpdateMessage(bytes, chunk.getPos().x, chunk.getPos().z));

        // send others
        sendChunkInfo(chunk, chunkPos, player, List.of(), List.of());
    }
}

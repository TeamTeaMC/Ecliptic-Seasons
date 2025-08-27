package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapChecker {
    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 + 5;

    public static final List<Level> validDimension = new ArrayList<>();
    public static final Map<Level, List<ChunkInfoMap>> REGION_LIST_COLLECTOR = new IdentityHashMap<>();
    public static List<ChunkInfoMap> CLIENT_REGION_LIST = new ArrayList<>();
    public static final int FLAG_IGNORE = -1;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_BLOCK = 1;
    public static final int FLAG_SLAB = 2;
    public static final int FLAG_STAIRS = 3;
    public static final int FLAG_STAIRS_TOP = 301;
    public static final int FLAG_LEAVES = 4;
    public static final int FLAG_GRASS = 5;
    public static final int FLAG_GRASS_LARGE = 501;
    public static final int FLAG_FARMLAND = 6;
    public static final int FLAG_VINE = 7;
    public static final int FLAG_CUSTOM = 999;

    // change model by blockstate, one block state with only one model
    public static final int FLAG_CUSTOM_JSON = 1000;
    // but plants which would swaying in shaders
    public static final int FLAG_CUSTOM_JSON_PLANTS = 1001;
    // change model by blockstate and if in top or lower, take two model
    // due to snow passable
    public static final int FLAG_CUSTOM_JSON_WITH_TOP = 1100;
    // due to snow passable, but leaves
    public static final int FLAG_CUSTOM_JSON_WITH_TOP_LEAVES = 1101;
    // change model if ignore offset, only one model
    public static final int FLAG_CUSTOM_JSON_VINE_LIKE = 1200;


    //  unload some
    public static void unloadLevel(Level level) {
        // updateLock = true;
        List<ChunkInfoMap> orDefault = getMapsListOrCreate(level);
        synchronized (orDefault) {
            orDefault.clear();
        }
        REGION_LIST_COLLECTOR.remove(level);

        // updateLock = false;
        validDimension.removeIf(level1 -> level1 == level);

        LEVEL_PARAMETER_LIST_MAP.remove(level);
    }

    public static boolean unloadChunk(Level level, ChunkPos chunkPos) {
        // int x0 = chunkPos.getMinBlockX();
        // int x1 = chunkPos.getMaxBlockX();
        // int z0 = chunkPos.getMinBlockZ();
        // int z1 = chunkPos.getMaxBlockZ();
        //
        // int x = blockToRegionCoord(x0);
        // int z = blockToRegionCoord(z0);
        // ChunkInfoMap map = getChunkMap(level, x, z);
        //
        // if (map != null) {
        //     // for (int i = x0; i < x1 + 1; i++) {
        //     //     for (int j = z0; j < z1 + 1; j++) {
        //     //         map.updateHeight(i, j, map.minY);
        //     //         map.updateBiome(i, j, -1);
        //     //     }
        //     // }
        //
        // }
        return false;
    }

    public static void tickLevel(Level level) {
        List<ChunkInfoMap> mapsList = MapChecker.getMapsListOrCreate(level);
        if (level.isClientSide()) CLIENT_REGION_LIST = mapsList;

        if (CommonConfig.Debug.disableChunkCacheCleaner.get()) return;
        List<ChunkInfoMap> chunkInfoMaps = null;
        if (mapsList != null && level.getRandom().nextInt(100) == 0) {
            for (int zz = 0; zz < mapsList.size(); zz++) {
                boolean shouldRemove = true;
                ChunkInfoMap map = mapsList.get(zz);
                if (map != null) {
                    int x0 = regionCoordToChunkStart(map.getX());
                    int z0 = regionCoordToChunkStart(map.getZ());
                    int mapChunkSize = mapChunkSize();

                    loopCheckMapIfEmpty:
                    for (int i = 0; i < mapChunkSize; i++) {
                        for (int j = 0; j < mapChunkSize; j++) {
                            if ((level instanceof ServerLevel serverLevel && !(serverLevel.getChunkSource().hasChunk(i + x0, j + z0)))
                                    || (level.isClientSide() && MapChecker.isLoaded(level, i + x0, j + z0))) {
                                shouldRemove = false;
                                break loopCheckMapIfEmpty;
                            }
                        }
                    }
                }
                if (shouldRemove) {
                    chunkInfoMaps = chunkInfoMaps == null ? new ArrayList<>() : chunkInfoMaps;
                    chunkInfoMaps.add(map);
                }
            }
            if (chunkInfoMaps != null) {
                for (ChunkInfoMap chunkInfoMap : chunkInfoMaps) {
                    EclipticSeasons.extraLogger(true, String.format("Remove the empty Height Map [%s, %s]", chunkInfoMap.getX(), chunkInfoMap.getZ()));
                }
                synchronized (mapsList) {
                    mapsList.removeAll(chunkInfoMaps);
                }
            }
        }
    }

    // 获取chunk位置
    public static int blockToRegionCoord(int i) {
        return i >> ChunkSizeAxis;
    }

    public static int regionCoordToChunkStart(int i) {
        return SectionPos.blockToSectionCoord(i << MapChecker.ChunkSizeAxis);
    }

    public static int mapChunkSize() {
        return MapChecker.ChunkSize / 16;
    }


    public static List<ChunkInfoMap> getMapsListOrCreate(Level level) {
        return REGION_LIST_COLLECTOR.computeIfAbsent(level, level1 -> new ArrayList<>());
    }

    public static List<ChunkInfoMap> getMapsList(Level level) {
        return level.isClientSide ?
                (CLIENT_REGION_LIST == null ? new ArrayList<>() : CLIENT_REGION_LIST) :
                getMapsListOrCreate(level);
    }

    public static ChunkInfoMap getChunkMap(Level level, BlockPos pos) {
        int x = blockToRegionCoord(pos.getX());
        int z = blockToRegionCoord(pos.getZ());
        return getChunkMap(level, x, z);
    }


    public static ChunkInfoMap getChunkMap(Level level, int regionX, int regionZ) {
        return getChunkMap(getMapsList(level), regionX, regionZ);
    }

    public static ChunkInfoMap getChunkMap(List<ChunkInfoMap> orDefault, int regionX, int regionZ) {
        ChunkInfoMap map = null;
        for (int i = 0; i < orDefault.size(); i++) {
            var chunkHeightMap = orDefault.get(i);
            if (chunkHeightMap != null && chunkHeightMap.x == regionX && chunkHeightMap.z == regionZ) {
                map = chunkHeightMap;
                break;
            }
        }
        return map;
    }

    public static @Nullable ChunkAccess getChunkView(Level level, BlockPos pos) {
        return level.getChunk(SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.SURFACE, false);
    }

    public static int getVanillaSolidHeightOrSelf(Level level, BlockPos pos) {
        ChunkAccess biomeChunk = getChunkView(level, pos);
        return biomeChunk != null ?
                biomeChunk.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) + 1 :
                pos.getY();
    }

    public static int getMCHeightWithCheck(Level level, BlockPos pos) {
        return getMCHeightWithCheck(level, pos, null);
    }

    public static int getMCHeightWithCheck(Level level, BlockPos pos, @Nullable Integer oldY) {
        ChunkAccess chunkAt = getChunkView(level, pos);
        // SnowyRemover snowyRemover = null;
        // if (chunkAt != null && chunkAt.hasData(AttachmentRegistry.SNOWY_REMOVER)) {
        //     snowyRemover = chunkAt.getData(AttachmentRegistry.SNOWY_REMOVER);
        // }
        return chunkAt == null ? pos.getY() :
                getMCHeightWithCheck(level, pos, chunkAt, null, null, oldY);
    }

    public static int getMCHeightWithCheck(Level level, BlockPos pos,
                                           @Nonnull ChunkAccess chunkAt,
                                           @Nullable SnowyRemover snowyRemover,
                                           @Nullable BlockPos.MutableBlockPos checkPos,
                                           @Nullable Integer oldHeight) {
        if (oldHeight != null
                && (oldHeight <= level.getMaxBuildHeight()
                && oldHeight >= level.getMinBuildHeight())) {
            if (pos.getY() <= oldHeight - 2) return oldHeight;
        }
        if (snowyRemover != null && snowyRemover.notSnowyAt(pos)) {
            return level.getMaxBuildHeight() + 1;
        }
        int posX = pos.getX();
        int posZ = pos.getZ();
        int height = chunkAt.getHeight(
                level.isClientSide ?
                        Heightmap.Types.MOTION_BLOCKING : Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, posX, posZ);
        if (checkPos == null) checkPos = new BlockPos.MutableBlockPos(posX, height, posZ);
        else checkPos.setY(height);
        // else checkPos = checkPos;
        while (height >= chunkAt.getMinBuildHeight()) {
            BlockState state = chunkAt.getBlockState(checkPos);
            if (!(extraSnowPassable(state)) &&
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(state)) {
                break;
            }
            height--;
            checkPos.setY(height);
        }
        if (height < chunkAt.getMinBuildHeight()) {
            height = chunkAt.getMinBuildHeight();
        }
        return height;
    }

    public static boolean solidTest(BlockState state) {
        return Heightmap.Types.MOTION_BLOCKING.isOpaque().test(state)
                && !MapChecker.extraSnowPassable(state);
    }

    public static boolean extraSnowPassable(BlockState state) {
        SnowDefinition.Info snow = SnowChecker.getUncacheSnow(state);
        if (snow != SnowDefinition.Info.EMPTY) {
            return snow.isSnowPassable()
                    || snow.getFlag() == FLAG_CUSTOM_JSON_WITH_TOP_LEAVES;
        }
        Block onBlock = state.getBlock();
        return ((
                onBlock instanceof LeavesBlock ||
                        onBlock instanceof TrapDoorBlock ||
                        onBlock instanceof DoorBlock ||
                        onBlock instanceof FenceBlock ||
                        onBlock instanceof FenceGateBlock ||
                        onBlock instanceof WallBlock ||
                        onBlock instanceof BellBlock ||
                        onBlock instanceof ComposterBlock ||
                        onBlock instanceof CampfireBlock ||
                        // onBlock instanceof AbstractCauldronBlock ||
                        // onBlock instanceof DaylightDetectorBlock ||
                        onBlock instanceof AnvilBlock ||
                        onBlock instanceof BasePressurePlateBlock ||
                        // onBlock instanceof HoneyBlock ||
                        onBlock instanceof IronBarsBlock ||
                        onBlock instanceof LightningRodBlock ||
                        onBlock instanceof LecternBlock ||
                        // onBlock instanceof SlimeBlock ||
                        onBlock instanceof BambooStalkBlock
        )
        );
    }

    public static int getHeightSafe(@NotNull Level level, BlockPos pos) {
        ChunkInfoMap chunkMap = getChunkMap(level, pos);
        if (chunkMap != null) {
            return chunkMap.getHeight(pos);
        }
        return level.getMinBuildHeight() - 1;
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
        // Note 这里存在一个设计问题，即维度有效否。考虑到切换问题，我们不应该阻止
        // if (!isValidDimension(level)) {
        //     switch (type) {
        //         case ChunkInfoMap.TYPE_BIOME -> {
        //             return 0;
        //         }
        //         case ChunkInfoMap.TYPE_HEIGHT -> {
        //             return level.getMinBuildHeight() - 1;
        //         }
        //     }
        // }

        int x = blockToRegionCoord(pos.getX());
        int z = blockToRegionCoord(pos.getZ());
        List<ChunkInfoMap> mapsList = getMapsList(level);
        ChunkInfoMap map = getChunkMap(mapsList, x, z);

        int value = 0;
        if (map != null) {
            if (type == ChunkInfoMap.TYPE_HEIGHT) {
                value = map.getHeight(pos);
                if (value <= map.minY || forceUpdate) {
                    var rh = getMCHeightWithCheck(level, pos, value);
                    map.updateHeight(pos, rh);
                    value = rh;
                }
            } else if (type == ChunkInfoMap.TYPE_BIOME) {
                value = map.getBiome(pos);
                if (value == -1 || forceUpdate) {
                    Holder<Biome> biome = level.getBiome(pos);
                    Holder<Biome> biome2 = fixSmallBiome(level, pos, biome, null, pos.getY(), map, level.getMaxBuildHeight(), level.getMinBuildHeight());
                    if (biome2 != null) {
                        if (biome2 != biome) {
                            biome = biome2;
                        }
                        value = biomeToId(level, biome.value());
                        if (isLoadNearBy(level, pos)) map.updateBiome(pos, value);
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
                Holder<Biome> biome = level.getBiome(pos);
                Holder<Biome> biome2 = fixSmallBiome(level, pos, biome, null, pos.getY(), map, level.getMaxBuildHeight(), level.getMinBuildHeight());
                if (biome2 != null) {
                    if (biome2 != biome) {
                        biome = biome2;
                    }
                    value = biomeToId(level, biome.value());
                    if (isLoadNearBy(level, pos)) map.updateBiome(pos, value);
                }
            }
        }
        // if (type == ChunkInfoMap.TYPE_BIOME && idToBiome(level, value).is(Biomes.PLAINS)) {
        //     // return 0;
        //     EclipticSeasons.logger(pos, isLoadNearBy(level, pos), WorldRenderer.isSectionLoad(SectionPos.of(pos), 2));
        // }

        return value;
    }

    public static @Nullable ChunkInfoMap getChunkInfoMapOrCreate(Level level, BlockPos pos) {
        if (level == null)
            return null;

        int x = blockToRegionCoord(pos.getX());
        int z = blockToRegionCoord(pos.getZ());
        List<ChunkInfoMap> mapsList = getMapsListOrCreate(level);
        ChunkInfoMap map = getChunkMap(mapsList, x, z);

        if (map != null) {
            return map;
        } else {
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
        }
        return map;
    }

    /**
     * Since Minecraft handles chunk retrieval and status checks differently on the server side,
     * we need special methods to determine whether a chunk is loaded.
     * This is especially important because when using Forgified Fabric API,
     * there are known issues with its chunk loading event mechanism.
     **/
    public static boolean isLoaded(Level level, BlockPos pos) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        return !level.isOutsideBuildHeight(pos) && isLoaded(level, chunkX, chunkZ);
    }

    public static boolean isLoaded(Level level, int chunkX, int chunkZ) {
        if (level.getChunkSource() instanceof ServerChunkCache serverChunkCache) {
            ChunkHolder visibleChunkIfPresent =
                    serverChunkCache.getVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ));
            if (visibleChunkIfPresent == null) return false;
            return visibleChunkIfPresent.getFullChunk() != null;
            // return visibleChunkIfPresent.getFullStatus().isOrAfter(FullChunkStatus.ENTITY_TICKING);
        }
        return level.getChunkSource().hasChunk(chunkX, chunkZ);
    }

    public static boolean isLoadedOnlyServer(Level level, BlockPos pos) {
        return !(level instanceof ServerLevel) || isLoaded(level, pos);
    }

    public static boolean isLoadNearByOnlyServer(Level level, BlockPos pos) {
        return !(level instanceof ServerLevel) || isLoadNearBy(level, pos);
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

        int i1 = (pos.getX() & 15) - 2;
        int l1 = (pos.getZ() & 15) - 2;
        int xe = ((i1) >> 2) > 2 ? 1 : 0;
        int ze = ((l1) >> 2) > 2 ? 1 : 0;
        int xs = i1 < 2 ? -1 : 0;
        int zs = l1 < 2 ? -1 : 0;
        // ChunkSource chunkSource = level.getChunkSource();
        for (int i = xs; i <= xe; i++) {
            for (int j = zs; j <= ze; j++) {
                if (!isLoaded(level, chunkX + i, chunkZ + j))
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
            BlockState stateAbove;
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
            if (CommonConfig.Debug.notLightAbove.get()) {
                isSnowy = notLightAbove(level, pos, 4);
            } else isSnowy = true;
        }
        return isSnowy;
    }


    public static boolean shouldSnowAt(Level level, BlockPos pos, int biomeId, BlockState state, RandomSource random, long seed) {
        boolean isSnowy = false;

        ArrayList<WeatherManager.BiomeWeather> biomeList = WeatherManager.getBiomeList(level);
        if (biomeList != null && WeatherManager.getSnowDepthAtBiome(level, idToBiome(level, biomeId).value()) > Math.abs(seed % 100)) {
            if (CommonConfig.Debug.notLightAbove.get()) {
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
        return biomeHolder != null && isSmallBiome(biomeHolder.value());
    }

    public static boolean isSmallBiome(@Nonnull Biome biomeHolder) {
        return ((IBiomeTagHolder) (Object) biomeHolder).eclipticseasons$isSmallBiome();
    }

    public static Holder<Biome> idToBiome(Level level, int id) {
        var list = WeatherManager
                .getBiomeList(level);
        if (list != null && id < list.size()) {
            Holder<Biome> biomeHolder =
                    list.get(id).biomeHolder;
            if (biomeHolder != null) return biomeHolder;
        }
        Optional<Registry<Biome>> biomeRegistry = level.registryAccess().registry(Registries.BIOME);
        if (biomeRegistry.isPresent()) {
            Optional<Holder.Reference<Biome>> holder = biomeRegistry.get().getHolder(id);
            if (holder.isPresent()) return holder.get();
            EclipticSeasons.extraLogger(true, "Unknown id with level", level, id);
            return biomeRegistry.get().getHolder(Biomes.PLAINS).orElse(null);
        }
        EclipticSeasons.extraLogger(true, "Unknown id with level", level, id);
        return null;
    }

    public static int biomeToId(Level level, Biome b) {
        Object o = b;
        if (o instanceof IBiomeTagHolder iBiomeTagHolder) {
            int id = iBiomeTagHolder.eclipticseasons$getBindId();
            if (id > -1) return id;
        }
        return level.registryAccess().registryOrThrow(Registries.BIOME).getId(b);
    }

    public static int biomeToId(Registry<Biome> biomes, Biome b) {
        return biomes.getId(b);
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
        int maxBuildHeight = level.getMaxBuildHeight();
        int minBuildHeight = level.getMinBuildHeight();

        // fix the pos to surface
        ChunkInfoMap chunkMap = getChunkMap(level, pos);

        Holder<Biome> biome = null;
        int bid = 0;
        int y = 0;
        if (chunkMap != null) {
            bid = chunkMap.getBiome(pos);
            if (bid > -1) {
                biome = idToBiome(level, bid);
                if (isSmallBiome(biome)) {
                    y = chunkMap.getHeight(pos) + 1;
                    if (y > maxBuildHeight || y <= minBuildHeight) {
                        y = getVanillaSolidHeightOrSelf(level, pos);
                    }
                }
            }
        }

        if (biome == null) {
            if (chunkMap != null) y = chunkMap.getHeight(pos) + 1;
            if (y > maxBuildHeight || y <= minBuildHeight) {
                y = getVanillaSolidHeightOrSelf(level, pos);
            }
            pos = new BlockPos(pos.getX(), y, pos.getZ());
            bid = getSurfaceOrUpdate(level, pos, false, ChunkInfoMap.TYPE_BIOME);
            biome = idToBiome(level, bid);
        }

        if (biome == null)
            biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);


        var biome2 = fixSmallBiome(level, pos, biome, null, y, chunkMap, maxBuildHeight, minBuildHeight);
        if (biome2 != null && biome2 != biome) {
            chunkMap = chunkMap == null ?
                    getChunkInfoMapOrCreate(level, pos) :
                    chunkMap;
            biome = biome2;
            if (chunkMap != null) chunkMap.updateBiome(pos, biomeToId(level, biome.value()));
        }
        return biome;
    }

    public static final Map<Level, Climate.ParameterList<Holder<Biome>>> LEVEL_PARAMETER_LIST_MAP = new IdentityHashMap<>();

    private static @Nullable Holder<Biome> fixSmallBiome(Level level, BlockPos pos, Holder<Biome> biome, @Nullable BlockPos.MutableBlockPos relative, int y, ChunkInfoMap chunkMap1, int maxBuildHeight, int minBuildHeight) {
        int i = 0;
        int last_ii = 0;
        boolean shouldBreak = false;
        while (isSmallBiome(biome)) {
            // if (level instanceof ServerLevel serverLevel) {
            //     biome = fixBiomeOnServer(serverLevel, pos, biome, chunkMap1);
            //     return biome;
            // }

            // if(true)break;
            if (relative == null) {
                relative = new BlockPos.MutableBlockPos(pos.getX(), y, pos.getZ());
            }
            i += 4;
            for (SimplePair<Direction, Direction> pair : SMALL_OFFSET_DIRECTIONS) {
                if (pair.getValue() != null) {
                    int ii = i - 1;
                    if (ii == last_ii) continue;
                    relative.move(pair.getKey(), ii);
                    relative.move(pair.getValue(), ii);
                    last_ii = ii;
                } else {
                    relative.move(pair.getKey(), i);
                }
                // if (chunkMap1 != null) {
                //     int x = blockToRegionCoord(relative.getX());
                //     int z = blockToRegionCoord(relative.getZ());
                //     if (chunkMap1.getX() == x && chunkMap1.getZ() == z)
                //         bid = chunkMap1.getBiome(relative);
                // }
                if (chunkMap1 != null) y = chunkMap1.getHeight(relative) + 1;
                if (y > maxBuildHeight || y <= minBuildHeight) {
                    // y = getVanillaSolidHeightOrSelf(level, relative);
                    y = pos.getY();
                }
                relative.setY(y);

                biome = CropGrowthHandler.getCropBiome(level,relative);

                // if (bid < 0) {
                //     y = getHeightSafe(level, relative) + 1;
                //     if (y > maxBuildHeight || y <= minBuildHeight) {
                //         y = getVanillaSolidHeightOrSelf(level, relative);
                //     }
                //     relative.setY(y);
                //     bid = getSurfaceOrUpdate(level, relative, false, ChunkInfoMap.TYPE_BIOME);
                // }
                // if (bid > -1) biome = idToBiome(level, bid);
                if (!isSmallBiome(biome)) {
                    shouldBreak = true;
                    break;
                } else {
                    relative.setX(pos.getX());
                    relative.setZ(pos.getZ());
                }
            }

            if (shouldBreak || i > 128) break;
        }
        return biome;
    }

    public static Holder<Biome> fixBiomeOnServer(ServerLevel level, BlockPos pos, Holder<Biome> biome, ChunkInfoMap map) {
        Climate.ParameterList<Holder<Biome>> parameters = LEVEL_PARAMETER_LIST_MAP.get(level);
        if (parameters == null) {
            BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
            if (biomeSource instanceof MultiNoiseBiomeSource multiNoiseBiomeSource) {
                Climate.ParameterList<Holder<Biome>> parameters2 = multiNoiseBiomeSource.parameters();
                List<Pair<Climate.ParameterPoint, Holder<Biome>>> list = parameters2.values().stream().filter(p -> !isSmallBiome(p.getSecond())).toList();
                parameters = new Climate.ParameterList<>(list);
                LEVEL_PARAMETER_LIST_MAP.put(level, parameters);
            }
        }
        if (parameters != null) {
            int biomeId = map == null ? -1 :
                    map.getBiome(QuartPos.toBlock(QuartPos.fromBlock(pos.getX())), QuartPos.toBlock(QuartPos.fromBlock(pos.getZ())));
            if (biomeId > -1) {
                biome = idToBiome(level, biomeId);
            } else {
                Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
                Climate.TargetPoint sample = sampler.sample(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
                biome = parameters.findValue(sample);
            }
        }
        return biome;
    }

    // 注意这个写法可能会导致重复
    public static Map<BlockState, Integer> blockTypeCache = new IdentityHashMap<>(4096);
    public static List<Block> LowerPlant = Stream.of(Blocks.GRASS, Blocks.FERN).collect(Collectors.toList());
    public static List<Block> LARGE_GRASS = Stream.of(Blocks.TALL_GRASS, Blocks.LARGE_FERN).collect(Collectors.toList());

    public static int getBlockType(BlockState state, BlockGetter level, BlockPos pos) {
        int flag = FLAG_NONE;
        // 不知道为啥这里会有null
        Integer realFlag = blockTypeCache.getOrDefault(state, FLAG_NONE - 1);
        if (realFlag == null) {
            if (CommonConfig.Debug.logIllegalUse.get())
                EclipticSeasons.logger("Null number get from %s".formatted(state));
            blockTypeCache.remove(state);
        } else {
            flag = realFlag;
        }
        if (flag < FLAG_NONE) {
            flag = FLAG_NONE;
            var onBlock = state.getBlock();
            SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state); // es patch

            if (CommonConfig.getForceBlocksNotSnowy().contains(state.getBlock())) {
                flag = FLAG_NONE;
            } else if (uncacheSnow.isValid()) {
                flag = uncacheSnow.getFlag();
            } else if (!CommonConfig.Debug.snowOverlayGlowingBlock.get()
                    && state.getLightEmission(level, pos) > 0) {
                flag = FLAG_NONE;
            } else if (state.is(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)) {
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
                    onBlock instanceof LightningRodBlock) {
                flag = FLAG_CUSTOM;
            }
            Integer otherFlag = blockTypeCache.putIfAbsent(state, flag);
            if (otherFlag != null && otherFlag != flag) {
                EclipticSeasons.logger("WARNING state %s expected %s but found %s".formatted(state, flag, otherFlag));
            }
        }
        return flag;
    }

    public static int getSnowOffset(BlockState state, int flag) {

        // es patch start
        SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state);
        if (uncacheSnow.isValid()) {
            return uncacheSnow.getOffset();
        }
        // es patch end

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
                // && level.dimensionType().natural()
                // && !level.dimensionType().hasFixedTime()
                ;
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

    public static void sendChunkLoginInfo(ServerLevel serverLevel, LevelChunk chunk, ChunkPos chunkPos, ServerPlayer player) {

        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        ChunkInfoMap chunkMap = getChunkMap(serverLevel, blockToRegionCoord(minBlockX), blockToRegionCoord(minBlockZ));

        if (chunkMap != null) {
            int[] biomes = new int[256];
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    biomes[i * 16 + j] = chunkMap.getBiome(minBlockX + i, minBlockZ + j);
                }
            }
            SimpleNetworkHandler.send(player, new ChunkBiomeUpdateMessage(biomes, chunk.getPos().x, chunk.getPos().z, 0));
        }

    }

    public static void forceChunkUpdateHeight(Level level, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos middleBlockPosition = new BlockPos.MutableBlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());
        // MapChecker.updatePosForce(level, middleBlockPosition, level.getMinBuildHeight() - 1);
        // getHeightOrUpdate(level, middleBlockPosition, false);
        ChunkInfoMap chunkMap = getChunkInfoMapOrCreate(level, middleBlockPosition);
        // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, middleBlockPosition);
        // BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        ChunkBiomeUpdateMessage chunkBiomeUpdateMessage = null;
        boolean shouldset = false;
        int bv = SolarHolders.getSaveDataLazy(level).map(SolarDataManager::getBiomeDataVersion).orElse(0);

        if (level instanceof ServerLevel) {
            if (chunk instanceof LevelChunk levelChunk) {
                Optional<ChunkBiomeUpdateMessage> resolve = levelChunk.getCapability(ChunkBiomeUpdateMessage.CHUNK_BIOME_UPDATE_MESSAGE_CAPABILITY).resolve();
                if (resolve.isPresent()) {
                    chunkBiomeUpdateMessage = resolve.get();
                    ChunkBiomeUpdateMessage mesageInWg = null;
                    if (chunk instanceof IChunkBiomeHolder iChunkBiomeHolder) {
                        mesageInWg = iChunkBiomeHolder.eclipticseasons$getBiomeHolder();
                    }
                    if (mesageInWg != null) {
                        chunkBiomeUpdateMessage.biomes = mesageInWg.biomes;
                        chunkBiomeUpdateMessage.version = mesageInWg.version;
                        chunk.setUnsaved(true);
                    }

                    if (chunkBiomeUpdateMessage.version == ChunkBiomeUpdateMessage.FLAG_NEED_VERSION) {
                        chunkBiomeUpdateMessage.version = bv;
                    }
                }
            }
        }

        if (chunkMap != null) {
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    middleBlockPosition.setX(i);
                    middleBlockPosition.setZ(j);
                    int k = getMCHeightWithCheck(level, middleBlockPosition, chunk, null, middleBlockPosition, null);

                    chunkMap.updateHeight(i, j, k);

                    // due to
                    if (level instanceof ServerLevel serverLevel) {
                        if (chunkBiomeUpdateMessage != null) {
                            int indexInArray = (i - chunkPos.getMinBlockX()) * 16 + (j - chunkPos.getMinBlockZ());
                            if (chunkBiomeUpdateMessage.version < ChunkBiomeUpdateMessage.FLAG_NEED_VERSION
                                    || (chunkBiomeUpdateMessage.version > ChunkBiomeUpdateMessage.FLAG_NEED_VERSION
                                    && chunkBiomeUpdateMessage.version != bv)) {
                                middleBlockPosition.set(i, k + 1, j);
                                if (chunkBiomeUpdateMessage.version == ChunkBiomeUpdateMessage.FLAG_FILL_SMALL) {
                                    Holder<Biome> biome = idToBiome(level, chunkBiomeUpdateMessage.biomes[indexInArray]);
                                    if (isSmallBiome(biome)) {
                                        Holder<Biome> biome2 = fixSmallBiome(serverLevel, middleBlockPosition, biome,
                                                middleBlockPosition,middleBlockPosition.getY(),chunkMap,level.getMaxBuildHeight(),level.getMinBuildHeight());
                                        int biomedToId = biomeToId(level, biome2.value());
                                        middleBlockPosition.set(i, k + 1, j);
                                        chunkMap.updateBiome(middleBlockPosition, biomedToId);
                                        chunkBiomeUpdateMessage.biomes[indexInArray] = biomedToId;
                                        shouldset = true;
                                    }
                                } else {
                                    Holder<Biome> biome = serverLevel.getBiome(middleBlockPosition);
                                    int biomedToId = biomeToId(level, biome.value());
                                    Holder<Biome> biome2 = fixSmallBiome(serverLevel, middleBlockPosition, biome,
                                            middleBlockPosition,middleBlockPosition.getY(),chunkMap,level.getMaxBuildHeight(),level.getMinBuildHeight());
                                    if (biome2 != null) {
                                        if (biome2 != biome) {biomedToId = biomeToId(level, biome2.value());}
                                        middleBlockPosition.set(i, k + 1, j);
                                        chunkMap.updateBiome(middleBlockPosition, biomedToId);
                                        chunkBiomeUpdateMessage.biomes[indexInArray] = biomedToId;
                                        shouldset = true;
                                    }
                                }
                            } else {
                                chunkMap.updateBiome(i, j, chunkBiomeUpdateMessage.biomes[indexInArray]);
                            }
                        }
                    }

                }
            }
        }

        if (shouldset && chunkBiomeUpdateMessage != null) {
            chunk.setUnsaved(true);
        }
    }


    // it means the block would have surface layer and below
    public static boolean leaveLike(int flag) {
        return flag == FLAG_LEAVES
                || flag == FLAG_CUSTOM_JSON_WITH_TOP
                || flag == FLAG_CUSTOM_JSON_WITH_TOP_LEAVES;
    }

    public static boolean vineLike(int flag) {
        return flag == FLAG_VINE || flag == FLAG_CUSTOM_JSON_VINE_LIKE;
    }

    public static boolean solidBlockLike(int flag) {
        return flag == FLAG_BLOCK
                || flag == FLAG_CUSTOM_JSON;
    }
}

package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.map.river.RiverBiomeResolver;
import com.teamtea.eclipticseasons.common.core.map.stub.PlainsStubHolder;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

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
    public static final int FLAG_CUSTOM_AO = 998;

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
                            if (level instanceof ServerLevel serverLevel) {
                                if (serverLevel.getChunkSource().hasChunk(i + x0, j + z0)) {
                                    shouldRemove = false;
                                    break loopCheckMapIfEmpty;
                                }
                            } else {
                                if (MapChecker.isLoaded(level, i + x0, j + z0)) {
                                    shouldRemove = false;
                                    break loopCheckMapIfEmpty;
                                }
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

    public static int chunkToRegionCoord(int chunkI) {
        return chunkI >> (ChunkSizeAxis - 4);
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

    // public static @Nullable ChunkAccess getChunkView(Level level, BlockPos pos) {
    //    return level.getChunk(SectionPos.blockToSectionCoord(pos.getX()),
    //            SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.SURFACE, false);
    //}

    public static @Nullable ChunkAccess getChunkView(Level level, BlockPos pos) {
        if (level == null) return null;
        int cx = SectionPos.blockToSectionCoord(pos.getX());
        int cz = SectionPos.blockToSectionCoord(pos.getZ());
        ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);
        return chunk != null && chunk.getStatus().isOrAfter(ChunkStatus.SURFACE) ?
                chunk : null;
    }

    public static @Nullable ChunkAccess getChunkView(Level level, int cx, int cz) {
        if (level == null) return null;
        ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);
        return chunk != null && chunk.getStatus().isOrAfter(ChunkStatus.SURFACE) ?
                chunk : null;
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

    @SuppressWarnings("removal")
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
        Heightmap.Types typesUse = level.isClientSide || !CommonConfig.Snow.snowyTree.get() ?
                Heightmap.Types.MOTION_BLOCKING : Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
        int height = chunkAt.getHeight(typesUse, posX, posZ);
        if (checkPos == null) checkPos = new BlockPos.MutableBlockPos(posX, height, posZ);
        else checkPos.setY(height);
        // else checkPos = checkPos;
        while (height >= chunkAt.getMinBuildHeight()) {
            BlockState state = chunkAt.getBlockState(checkPos);
            if (solidTest(state)) {
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
                (onBlock instanceof LeavesBlock && CommonConfig.Snow.snowyTree.get()) ||
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
                    value = biomeToId(level, level.getBiome(pos).value());
                    if (isLoadNearBy(level, pos)) {
                        map.updateBiome(pos, value);
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
                value = biomeToId(level, level.getBiome(pos).value());
                if (isLoadNearBy(level, pos)) {
                    map.updateBiome(pos, value);
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
        return getChunkInfoMapOrCreate(level, x, z);
    }

    public static @Nullable ChunkInfoMap getChunkInfoMapOrCreate(Level level, ChunkPos pos) {
        if (level == null)
            return null;

        int x = chunkToRegionCoord(pos.x);
        int z = chunkToRegionCoord(pos.z);
        return getChunkInfoMapOrCreate(level, x, z);
    }

    public static @NotNull ChunkInfoMap getChunkInfoMapOrCreate(@NotNull Level level, int regionX, int regionZ) {

        List<ChunkInfoMap> mapsList = getMapsListOrCreate(level);
        ChunkInfoMap map = getChunkMap(mapsList, regionX, regionZ);

        if (map != null) {
            return map;
        } else {
            synchronized (mapsList) {
                boolean hasBuild = false;
                for (ChunkInfoMap chunkHeightMap : mapsList) {
                    if (chunkHeightMap.x == regionX && chunkHeightMap.z == regionZ) {
                        hasBuild = true;
                        map = chunkHeightMap;
                        break;
                    }
                }
                if (!hasBuild) {
                    // level.registryAccess().registry(Registries.BIOME).get().getId(Biomes.THE_VOID)
                    map = new ChunkInfoMap(regionX, regionZ, level.getMinBuildHeight() - 1, level.isClientSide);
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
        if (!CommonConfig.Debug.notLightAbove.get()) return true;
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
            } else if (!stateAbove.isAir() && !solidTest(stateAbove)) {
                if (times > 0)
                    return notLightAbove(level, abovePos, (times - 1));
            }
        }
        return true;
    }


    public static boolean isAboveSnowLine(@NotNull Level level, Biome biome, BlockPos pos) {
        return isAboveSnowLine(biome, pos.getY(), level instanceof ServerLevel);
    }

    public static boolean isAboveSnowLine(Biome biome, int pos, boolean isServer) {
        return pos > BiomeClimateManager.getSnowLine(biome, isServer);
    }

    public static boolean shouldSnowAt(@Nonnull Level level, BlockPos pos, BlockState state, RandomSource random, long seed) {
        if (SnowyMapChecker.shouldCheckSnowyStatus(level, pos) && notWater(state)) {
            return SnowyMapChecker.isSnowyBlock(level, pos);
        }

        var biomeHolder = getSurfaceBiome(level, pos);
        Biome biome = biomeHolder.value();
        boolean isSnowy = WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100);
        if (!isSnowy) {
            isSnowy = isAboveSnowLine(level, biome, pos);
        }
        if (isSnowy) isSnowy = notLightAbove(level, pos, 4);
        return isSnowy;
    }


    public static boolean shouldSnowAt(@Nonnull Level level, BlockPos pos, int biomeId, BlockState state, @Nullable RandomSource random, long seed) {
        if (SnowyMapChecker.shouldCheckSnowyStatus(level, pos) && notWater(state)) {
            return SnowyMapChecker.isSnowyBlock(level, pos);
        }

        Biome biome = idToBiome(level, biomeId).value();
        ArrayList<WeatherManager.BiomeWeather> biomeList = WeatherManager.getBiomeList(level);
        boolean isSnowy = biomeList != null && WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100);
        if (!isSnowy) {
            isSnowy = isAboveSnowLine(level, biome, pos);
        }
        if (isSnowy) isSnowy = notLightAbove(level, pos, 4);
        return isSnowy;
    }

    public static boolean notWater(BlockState state) {
        return state == null || !state.is(Blocks.WATER);
    }

    public static boolean shouldSnowAtBiome(@Nonnull Level level, Biome biome, BlockState state, RandomSource random, long seed, BlockPos mcPos) {
        if (isAboveSnowLine(level, biome, mcPos)) {
            return true;
        }
        return WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100);
    }

    public static boolean isSmallBiome(@Nonnull Holder<Biome> biomeHolder) {
        return biomeHolder != null && isSmallBiome(biomeHolder.value());
    }

    public static boolean isSmallBiome(@Nonnull Biome biomeHolder) {
        return ((IBiomeTagHolder) (Object) biomeHolder).eclipticseasons$isSmallBiome();
    }

    public static @NotNull Holder<Biome> idToBiome(Level level, int id) {
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
            EclipticSeasons.extraLogger(false, "Failed to resolve biome ID in level",
                    id, level.dimension().location(), level.getClass().getName(), level);
            return biomeRegistry.get().getHolder(Biomes.PLAINS).orElse(PlainsStubHolder.PLAINS);
        }
        EclipticSeasons.extraLogger(false, "Registry access for BIOME is missing in abnormal level context",
                id, level.getClass().getName(), level);
        return PlainsStubHolder.VOID;
    }

    public static Holder<Biome> idToBiome(Registry<Biome> biomes, int id) {
        Optional<Holder.Reference<Biome>> holder = biomes.getHolder(id);
        if (holder.isPresent()) return holder.get();
        EclipticSeasons.extraLogger(false, "Unknown ID for biome within registry",
                id, biomes.getClass().getName(), biomes);
        return biomes.getHolder(Biomes.PLAINS).orElse(PlainsStubHolder.PLAINS);
    }

    public static int biomeToId(Level level, Biome b) {
        Object o = b;
        if (o instanceof IBiomeTagHolder iBiomeTagHolder) {
            int id = iBiomeTagHolder.eclipticseasons$getBindId();
            if (id > -1) return id;
        }
        return biomeToId(level.registryAccess().registryOrThrow(Registries.BIOME), b);
    }

    public static int biomeToId(Registry<Biome> biomes, Biome b) {
        int id = biomes.getId(b);
        if (id < 0) {
            Biome plainsBiome = biomes.get(Biomes.PLAINS);
            id = biomes.getId(plainsBiome);
        }
        return id;
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
        // int x = SectionPos.blockToSectionCoord(pos.getX());
        // int z = SectionPos.blockToSectionCoord(pos.getZ());
        ChunkAccess chunkAt = getChunkView(level, pos);
        if (chunkAt instanceof IChunkBiomeHolder iChunkBiomeHolder) {
            BiomeHolder biomeHolder = iChunkBiomeHolder.eclipticseasons$getBiomeHolder();
            if (biomeHolder != null
                    && biomeHolder.version() == EclipticUtil.getBiomeDataVersion(level)) {
                // BiomeHolder biomeHolder = chunkAt.getData(ModContents.BIOME_HOLDER);
                return getSurfaceBiome(level, pos, biomeHolder);
            }
        }
        return getUnCachedSurfaceBiome(level, pos);
    }

    public static Holder<Biome> getSurfaceBiomeByChunk(Level level, LevelChunk chunkAt, BlockPos pos) {
        if (chunkAt instanceof IChunkBiomeHolder iChunkBiomeHolder) {
            BiomeHolder biomeHolder = iChunkBiomeHolder.eclipticseasons$getBiomeHolder();
            if (biomeHolder != null
                    && biomeHolder.version() == EclipticUtil.getBiomeDataVersion(level)) {
                // BiomeHolder biomeHolder = chunkAt.getData(ModContents.BIOME_HOLDER);
                return getSurfaceBiome(level, pos, biomeHolder);
            }
        }
        return getUnCachedSurfaceBiome(level, pos);
    }

    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos, @Nonnull BiomeHolder biomeHolder) {
        int biomeId = biomeHolder.getBiomeId(pos);
        return biomeId > -1 ? idToBiome(level, biomeId) :
                getUnCachedSurfaceBiome(level, pos);
    }

    public static Holder<Biome> getUnCachedSurfaceBiome(Level level, BlockPos pos) {
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

        if (isSmallBiome(biome)
                && level instanceof ServerLevel serverLevel) {
            Climate.TargetPoint sample = RiverBiomeResolver.getClimateTargetPoint(serverLevel.getChunkSource().randomState(), pos.mutable());
            ResourceKey<Biome> biomeResourceKey = RiverBiomeResolver.getClimateBiome(sample);
            biome = level.registryAccess().registry(Registries.BIOME).map(r->r.getHolder(biomeResourceKey)).map(Optional::get).map(bh -> (Holder) bh).orElse(biome);
        }

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
            i += 4;
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


                if (chunkMap != null) {
                    int x = blockToRegionCoord(relative.getX());
                    int z = blockToRegionCoord(relative.getZ());
                    if (chunkMap.getX() == x && chunkMap.getZ() == z)
                        bid = chunkMap.getBiome(relative);
                    if (bid > -1) biome = idToBiome(level, bid);
                }
                if (i > 20 && level instanceof ServerLevel serverLevel && !isLoadNearBy(level, relative)) {
                    BiomeSource biomeSource = serverLevel.getChunkSource().getGenerator().getBiomeSource();
                    int qx = QuartPos.fromBlock(relative.getX());
                    int qy = QuartPos.fromBlock(relative.getY());
                    int qz = QuartPos.fromBlock(relative.getZ());
                    biome = biomeSource.getNoiseBiome(qx, qy, qz, serverLevel.getChunkSource().randomState().sampler());
                } else if (bid < 0) {
                    y = getHeightSafe(level, relative) + 1;
                    if (y > maxBuildHeight || y <= minBuildHeight) {
                        y = getVanillaSolidHeightOrSelf(level, relative);
                    }
                    relative.setY(y);
                    bid = getSurfaceOrUpdate(level, relative, false, ChunkInfoMap.TYPE_BIOME);
                    biome = idToBiome(level, bid);
                }


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

                biome = CropGrowthHandler.getCropBiome(level, relative);

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

    public static int getBlockType(BlockState state, BlockGetter level, BlockPos pos) {
        int flag = FLAG_NONE;
        // 不知道为啥这里会有null
        Block onBlock = state.getBlock();
        if (!CommonConfig.Debug.snowOverlayGlowingBlock.get()
                && state.getLightEmission(level, pos) > 0) {
            flag = FLAG_NONE;
        } else if (state.is(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)) {
            flag = FLAG_NONE;
        } else if (state.getBlock().builtInRegistryHolder().key().location().getNamespace().equals("snowrealmagic"))
            return MapChecker.FLAG_NONE;
        else if (onBlock instanceof LeavesBlock) {
            flag = FLAG_LEAVES;
        } else if (onBlock == Blocks.GRASS_BLOCK ||
                onBlock == Blocks.DIRT ||
                onBlock == Blocks.STONE ||
                onBlock == Blocks.SAND) {
            flag = FLAG_BLOCK;
        } else if (onBlock == Blocks.GRASS || onBlock == Blocks.FERN) {
            flag = FLAG_GRASS;
        } else if (onBlock == Blocks.TALL_GRASS || onBlock == Blocks.LARGE_FERN) {
            flag = FLAG_GRASS_LARGE;
        } else if (onBlock instanceof VineBlock) {
            flag = FLAG_VINE;
        } else if ((onBlock instanceof FarmBlock || onBlock instanceof DirtPathBlock)) {
            flag = FLAG_FARMLAND;
        } else if (onBlock instanceof TrapDoorBlock ||
                (onBlock instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) ||
                onBlock instanceof FenceBlock ||
                onBlock instanceof FenceGateBlock ||
                onBlock instanceof WallBlock ||
                onBlock instanceof BellBlock ||
                onBlock instanceof ComposterBlock ||
                (onBlock instanceof CampfireBlock && !state.getValue(CampfireBlock.LIT)) ||
                onBlock == Blocks.IRON_BARS ||
                onBlock instanceof LightningRodBlock
            //||
            // onBlock instanceof AzaleaBlock
        ) {
            flag = FLAG_CUSTOM;
        } else {
            ResourceLocation blockName = onBlock.builtInRegistryHolder().key().location();
            if (state.isSolidRender(level, pos)) {
                flag = FLAG_BLOCK;
            } else if (onBlock instanceof SlabBlock) {
                SlabType value = state.getValue(SlabBlock.TYPE);
                if (value == SlabType.TOP) {
                    flag = FLAG_STAIRS_TOP;
                } else if (value == SlabType.BOTTOM) {
                    flag = FLAG_SLAB;
                } else flag = FLAG_BLOCK;
                if (blockName.toString().equals("xkdeco:dirt_path_slab"))
                    flag = FLAG_CUSTOM;
            } else if (onBlock instanceof StairBlock) {
                // if (state.getValue(StairBlock.HALF) == Half.TOP)
                //     flag = FLAG_STAIRS_TOP;
                // else flag = FLAG_STAIRS;
                flag = FLAG_STAIRS;
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
        } else if (customBuiltin(flag)) {
            if (state.getBlock() instanceof AzaleaBlock)
                offset = 1;
        }
        return offset;
    }

    public static int getDefaultBlockTypeFlag(BlockState state) {
        IBlockStateFlagger flagger = (IBlockStateFlagger) state;
        int flag = flagger.getBlockTypeFlag();
        if (flag < 0) {

            SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state); // es patch

            if (CommonConfig.getForceBlocksNotSnowy().contains(state.getBlock())) {
                flag = FLAG_NONE;
            } else {
                if (uncacheSnow.isValid()) flag = uncacheSnow.getFlag();
                else {
                    try {
                        flag = getBlockType(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                    } catch (Exception e) {
                        flag = FLAG_NONE;
                        EclipticSeasons.logger(e);
                    }
                }
            }
            flagger.setBlockTypeFlag(flag);
        }
        return flag;
    }

    @Deprecated(forRemoval = true)
    public static int getBlockTypeFlag(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        IBlockStateFlagger flagger = (IBlockStateFlagger) state;
        int flag;
        if (CommonConfig.getForceBlocksNotSnowy().contains(state.getBlock())) {
            flag = FLAG_NONE;
        } else {
            SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state); // es patch
            flag = uncacheSnow.isValid() ?
                    uncacheSnow.getFlag() : getBlockType(state, blockGetter, pos);
        }
        return flag;
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

        BiomeHolder biomeHolder = getOrUpdateChunkBiomeData(serverLevel, (IChunkBiomeHolder) chunk, chunkPos);

        if (biomeHolder != null && biomeHolder.hasUpdated()) {
            SimpleNetworkHandler.send(player, new ChunkBiomeUpdateMessage(biomeHolder.biomes(), chunkPos, biomeHolder.version()));
        }
        //
        // if (EclipticUtil.canSnowyBlockInteract()) {
        //     SnowyStatusKeeper snowyStatusKeeper = SnowyMapChecker.getSnowyStatusKeeper(chunk);
        //     SimpleNetworkHandler.send(player, new SnowyStatusHandler(true, snowyStatusKeeper, chunk.getPos()));
        // }
    }

    public static @NotNull BiomeHolder getOrUpdateChunkBiomeData(ServerLevel serverLevel, IChunkBiomeHolder
            chunk, ChunkPos chunkPos) {
        int biomeDataVersion = EclipticUtil.getBiomeDataVersion(serverLevel);
        BiomeHolder biomeHolder = chunk.eclipticseasons$getBiomeHolder();
        if (biomeHolder == null) {
            biomeHolder = BiomeHolder
                    .prepareBiomes(serverLevel, (ChunkAccess) chunk, chunkPos, biomeDataVersion, false);
            chunk.eclipticseasons$setBiomeHolder(biomeHolder);
        } else {
            if (biomeHolder.hasUpdated() && biomeHolder.version() == BiomeHolder.FLAG_FILL_SMALL) {
                biomeHolder = BiomeHolder
                        .fillSmallBiomes(serverLevel, (ChunkAccess) chunk, biomeHolder, biomeDataVersion);
                chunk.eclipticseasons$setBiomeHolder(biomeHolder);
            } else if (!biomeHolder.hasUpdated() || biomeHolder.version() != biomeDataVersion) {
                biomeHolder = BiomeHolder
                        .prepareBiomes(serverLevel, (ChunkAccess) chunk, chunkPos, biomeDataVersion, biomeHolder.version() != biomeDataVersion);
                chunk.eclipticseasons$setBiomeHolder(biomeHolder);
            }
        }
        return biomeHolder;
    }

    public static void resetBiomeHolder(ServerLevel serverLevel, BlockPos pos) {
        int biomeDataVersion = EclipticUtil.getBiomeDataVersion(serverLevel);
        ChunkAccess chunk = serverLevel.getChunk(pos);
        if (chunk instanceof IChunkBiomeHolder chunkBiomeHolder) {
            ChunkPos chunkPos = new ChunkPos(pos);
            var biomeHolder = BiomeHolder
                    .prepareBiomes(serverLevel, serverLevel.getChunk(pos), chunkPos, biomeDataVersion, true);
            chunkBiomeHolder.eclipticseasons$setBiomeHolder(biomeHolder);

            if (chunk instanceof LevelChunk levelChunk) {
                for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
                    MapChecker.sendChunkLoginInfo(serverLevel, levelChunk, chunkPos, player);
                }
            }
        }
    }

    public static ChunkInfoMap forceChunkUpdateHeight(Level level, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());        // MapChecker.updatePosForce(level, checkPos , level.getMinBuildHeight() - 1);
        // getHeightOrUpdate(level, checkPos , false);
        ChunkInfoMap chunkMap = getChunkInfoMapOrCreate(level, checkPos);
        // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, checkPos );
        // BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        if (chunkMap != null) {
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    checkPos.setX(i);
                    checkPos.setZ(j);
                    int k = getMCHeightWithCheck(level, checkPos, chunk, null, checkPos, null);
                    chunkMap.updateHeight(i, j, k);
                }
            }
        }

        return chunkMap;
    }

    public static void setNewChunk(ServerLevel serverLevel, ChunkAccess chunk) {
        if (chunk instanceof IChunkBiomeHolder chunkBiomeHolder)
        // if (chunk.hasData(AttachmentRegistry.BIOME_HOLDER))
        {
            BiomeHolder biomeHolder = chunkBiomeHolder.eclipticseasons$getBiomeHolder();
            if (biomeHolder != null) {
                SolarDataManager data = SolarHolders.getSaveData(serverLevel);
                if (data != null && biomeHolder.hasUpdated()
                        && (biomeHolder.version() == BiomeHolder.FLAG_NEED_VERSION)) {
                    chunkBiomeHolder.eclipticseasons$setBiomeHolder(new BiomeHolder(biomeHolder.biomes(),
                            true,
                            data.getBiomeDataVersion()));
                }
            }
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

    public static boolean customBuiltin(int flag) {
        return flag == FLAG_CUSTOM_AO || flag == FLAG_CUSTOM;
    }
}

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
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import com.teamtea.eclipticseasons.common.network.message.ChunkUpdateMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
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
        //     boolean shouldRemove = true;
        //     // loopCheckMapIfEmpty:
        //     // for (int i = 0; i < MapChecker.ChunkSize; i++) {
        //     //     for (int j = 0; j < MapChecker.ChunkSize; j++) {
        //     //         if (map.getHeight(i, j) != map.minY) {
        //     //             shouldRemove = false;
        //     //             break loopCheckMapIfEmpty;
        //     //         }
        //     //     }
        //     // }
        //     // if (shouldRemove) {
        //     //     if (level.isClientSide()) {
        //     //         CLIENT_REGION_LIST.remove(map);
        //     //     } else {
        //     //         List<ChunkInfoMap> mapsList = getMapsList(level);
        //     //         if (mapsList != null) mapsList.remove(map);
        //     //     }
        //     //     EclipticSeasons.extraLogger(true,String.format("Remove the empty Height Map [%s, %s]", x, z));
        //     // }
        //
        //     return shouldRemove;
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

    public static int chunkToRegionCoord(int chunkI) {
        return chunkI >> (ChunkSizeAxis - 4);
    }

    public static int regionCoordToChunkStart(int i) {
        return SectionPos.blockToSectionCoord(i << MapChecker.ChunkSizeAxis);
    }

    public static int mapChunkSize() {
        return MapChecker.ChunkSize >> 4;
    }

    public static List<ChunkInfoMap> getMapsListOrCreate(Level level) {
        return REGION_LIST_COLLECTOR.computeIfAbsent(level, level1 -> new ArrayList<>());
    }

    public static List<ChunkInfoMap> getMapsList(Level level) {
        return level.isClientSide() ?
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

    //public static @Nullable ChunkAccess getChunkView(Level level, BlockPos pos) {
    //    return level.getChunk(SectionPos.blockToSectionCoord(pos.getX()),
    //            SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.SURFACE, false);
    //}

    public static @Nullable ChunkAccess getChunkView(Level level, BlockPos pos) {
        if (level == null) return null;
        int cx = SectionPos.blockToSectionCoord(pos.getX());
        int cz = SectionPos.blockToSectionCoord(pos.getZ());
        ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);
        return chunk != null && chunk.getPersistedStatus().isOrAfter(ChunkStatus.SURFACE) ?
                chunk : null;
    }

    public static @Nullable ChunkAccess getChunkView(Level level, int cx, int cz) {
        if (level == null) return null;
        ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);
        return chunk != null && chunk.getPersistedStatus().isOrAfter(ChunkStatus.SURFACE) ?
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
        SnowyRemover snowyRemover = null;
        if (chunkAt != null && chunkAt.hasData(AttachmentRegistry.SNOWY_REMOVER)) {
            snowyRemover = chunkAt.getData(AttachmentRegistry.SNOWY_REMOVER);
        }
        return chunkAt == null ? pos.getY() :
                getMCHeightWithCheck(level, pos, chunkAt, snowyRemover, null, oldY);
    }

    public static int getMCHeightWithCheck(Level level, BlockPos pos,
                                           @Nonnull ChunkAccess chunkAt,
                                           @Nullable SnowyRemover snowyRemover,
                                           @Nullable BlockPos.MutableBlockPos checkPos,
                                           @Nullable Integer oldHeight) {
        if (oldHeight != null
                && (oldHeight <= level.getMaxY()
                && oldHeight >= level.getMinY())) {
            if (pos.getY() <= oldHeight - 2) return oldHeight;
        }
        if (snowyRemover != null && snowyRemover.notSnowyAt(pos)) {
            return level.getMaxY() + 1;
        }
        int posX = pos.getX();
        int posZ = pos.getZ();

        Heightmap.Types typesUse = level.isClientSide() || !CommonConfig.Snow.snowyTree.get() ?
                Heightmap.Types.MOTION_BLOCKING : Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
        int height = chunkAt.getHeight(typesUse, posX, posZ);
        if (checkPos == null) checkPos = new BlockPos.MutableBlockPos(posX, height, posZ);
        else checkPos.setY(height);
        // else checkPos = checkPos;
        while (height >= chunkAt.getMinY()
                && chunkAt.getBlockState(checkPos) instanceof BlockState state
                && (!solidTest(state))) {
            height--;
            checkPos.setY(height);
        }
        if (height < chunkAt.getMinY()) {
            height = chunkAt.getMinY();
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
        return (state.getBlock() instanceof Block onBlock
                && (
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
        return level.getMinY() - 1;
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
        //             return level.getMinY() - 1;
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
                    map = new ChunkInfoMap(x, z, level.getMinY() - 1, level.isClientSide());
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

        int x = chunkToRegionCoord(pos.x());
        int z = chunkToRegionCoord(pos.z());
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
                    map = new ChunkInfoMap(regionX, regionZ, level.getMinY() - 1, level.isClientSide());
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
                    serverChunkCache.getVisibleChunkIfPresent(ChunkPos.pack(chunkX, chunkZ));
            if (visibleChunkIfPresent == null) return false;
            var completablefuture = visibleChunkIfPresent.getFullChunkFuture();
            var either = completablefuture.getNow(null);
            LevelChunk o = either == null ? null : either.orElse(null);
            return o != null;
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

        // TODO:似乎都是+1，那么就是+1两个方向查询即可
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
        return true;
    }

    public static void updatePosForce(Level level, BlockPos setPos, int y) {
        ChunkInfoMap map = getChunkMap(level, setPos);
        if (map != null)
            map.updateHeight(setPos, y);
    }

    public static boolean withoutSaltWandAffect(Level level, BlockPos pos) {
        if (isLoadedOnlyServer(level, pos)) {
            LevelChunk chunkAt = level.getChunkAt(pos);
            NoneSnowArea snowArea = chunkAt.getExistingDataOrNull(AttachmentRegistry.NONE_SNOW_AREA);
            return !(snowArea != null && snowArea.neverSnowyAt(pos));
        }
        return true;
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

    /**
     * Checks whether snow can cover this block.
     * Used only for regular queries.
     */
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

        if (isSnowy) isSnowy = withoutSaltWandAffect(level, pos);
        return isSnowy;
    }

    /**
     * Checks whether this block can be covered with snow.
     * Used only during accelerated rendering.
     */
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


    /**
     * Checks whether this block can be covered with snow.
     * Used only when positional information is unavailable.
     */
    public static boolean shouldSnowAtBiome(@Nonnull Level level, Biome biome, BlockState state, RandomSource random, long seed, BlockPos mcPos) {
        if (isAboveSnowLine(level, biome, mcPos)) {
            return true;
        }
        return WeatherManager.getSnowDepthAtBiome(level, biome) > Math.abs(seed % 100);
    }

    public static boolean isSmallBiome(@Nonnull Holder<Biome> biomeHolder) {
        // return BiomeClimateManager.SMALL_BIOME_MAP.containsKey(biomeHolder.value());
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
        Optional<Registry<Biome>> biomeRegistry = level.registryAccess().get(Registries.BIOME).map(Holder::value);
        if (biomeRegistry.isPresent()) {
            Optional<Holder.Reference<Biome>> holder = biomeRegistry.get().get(id);
            if (holder.isPresent()) return holder.get();
            EclipticSeasons.extraLogger(true, "Unknown id with level", level, id);
            return biomeRegistry.get().get(Biomes.PLAINS).orElse(null);
        }
        EclipticSeasons.extraLogger(true, "Unknown id with level", level, id);
        return null;
    }

    public static Holder<Biome> idToBiome(Registry<Biome> biomes, int id) {
        Optional<Holder.Reference<Biome>> holder = biomes.get(id);
        if (holder.isPresent()) return holder.get();
        EclipticSeasons.extraLogger(true, "Unknown id for biome", id);
        return biomes.get(Biomes.PLAINS).orElse(null);
    }

    public static int biomeToId(Level level, Biome b) {
        Object o = b;
        if (o instanceof IBiomeTagHolder iBiomeTagHolder) {
            int id = iBiomeTagHolder.eclipticseasons$getBindId();
            if (id > -1) return id;
        }
        return biomeToId(level.registryAccess().lookupOrThrow(Registries.BIOME), b);
    }

    public static int biomeToId(Registry<Biome> biomes, Biome b) {
        int id = biomes.getId(b);
        if (id < 0) {
            Biome plainsBiome = biomes.getValueOrThrow(Biomes.PLAINS);
            id = biomes.getId(plainsBiome);
        }
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


    //
    // 重写getSurfaceBiome，死锁主要来自于level.getChunk(x, z, ChunkStatus.FULL, false)
    // 使用ChunkStatus.BIOME替代看看,level.getBiome(pos)
    // level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.BIOMES, false)
    // level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) 也会卡死，因为要full了
    // 继续优化缓存
    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos) {
        //int x = SectionPos.blockToSectionCoord(pos.getX());
        //int z = SectionPos.blockToSectionCoord(pos.getZ());
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

    public static Holder<Biome> getSurfaceBiome(Level level, BlockPos pos, BiomeHolder biomeHolder) {
        int biomeId = biomeHolder == null ? -1 : biomeHolder.getBiomeId(pos);
        return biomeId > -1 ? idToBiome(level, biomeId) : getUnCachedSurfaceBiome(level, pos);
    }

    public static Holder<Biome> getUnCachedSurfaceBiome(Level level, BlockPos pos) {
        int maxBuildHeight = level.getMaxY();
        int minBuildHeight = level.getMinY();
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
            biome = level.registryAccess().holderOrThrow(Biomes.PLAINS);

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

    private static Holder<Biome> fixBiomeOnServer(ServerLevel level, BlockPos
            pos, Holder<Biome> biome, ChunkInfoMap map) {
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
            // int biomeId = map == null ? -1 :
            //         map.getBiome(QuartPos.toBlock(QuartPos.fromBlock(pos.getX())), QuartPos.toBlock(QuartPos.fromBlock(pos.getZ())));
            // if (biomeId > -1) {
            //     biome = idToBiome(level, biomeId);
            // }
            {
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
        if (!CommonConfig.Debug.snowOverlayGlowingBlock.getAsBoolean()
                && state.getLightEmission(level, pos) > 0) {
            flag = FLAG_NONE;
        } else if (state.is(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)) {
            flag = FLAG_NONE;
        } else if (state.getBlock().builtInRegistryHolder().getKey().identifier().getNamespace().equals("snowrealmagic"))
            return MapChecker.FLAG_NONE;
        else if (onBlock instanceof LeavesBlock) {
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
        } else if (onBlock instanceof VineBlock) {
            flag = FLAG_VINE;
        } else if ((onBlock instanceof FarmlandBlock || onBlock instanceof DirtPathBlock)) {
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
                onBlock instanceof LightningRodBlock ||
                onBlock instanceof AzaleaBlock) {
            flag = FLAG_CUSTOM;
        } else {
            Identifier blockName = onBlock.builtInRegistryHolder().key().identifier();
            if (state.isSolidRender()) {
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
                if (state.getValue(StairBlock.HALF) == Half.TOP)
                    flag = FLAG_STAIRS_TOP;
                else flag = FLAG_STAIRS;
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
        } else if (MapChecker.customBuiltin(flag)) {
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
                level.getMaxY(),
                pos.getZ());

        var list = new ArrayList<Holder<Biome>>();
        while (mPos.getY() >= level.getMinY()) {
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

    public static void sendChunkInfo(LevelChunk chunk, ChunkPos chunkPos, ServerPlayer
            player, List<Integer> section_y, List<BlockPos> clickedPos) {
        byte[] bytes = new byte[256];
        // var section_y = new HashSet<Integer>(chunk.getSectionsCount());
        // var section_y=new HashSet<Integer>();

        if (chunk.hasData(AttachmentRegistry.SNOWY_REMOVER)
                && chunk.getData(AttachmentRegistry.SNOWY_REMOVER) instanceof SnowyRemover snowyRemover) {
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
        SimpleNetworkHandler.send(player, new ChunkUpdateMessage(bytes, chunk.getPos().x(), chunk.getPos().z(), section_y, clickedPos));
    }

    public static void sendChunkLoginInfo(ServerLevel serverLevel, LevelChunk chunk, ChunkPos
            chunkPos, ServerPlayer player) {
        // if (event.getLevel() instanceof ServerLevel serverLevel)
        BiomeHolder biomeHolder = getOrUpdateChunkBiomeData(serverLevel, chunk, chunkPos);

        if (biomeHolder != null && biomeHolder.hasUpdated()) {
            SimpleNetworkHandler.send(player, new ChunkBiomeUpdateMessage(biomeHolder.biomes(), chunk.getPos().x(), chunk.getPos().z(), biomeHolder.version()));
        }
        // chunk.removeData(ModContents.BIOME_HOLDER);

        // send others
        sendChunkInfo(chunk, chunkPos, player, List.of(), List.of());
    }

    // todo 这里注意用接口走set
    public static @NotNull BiomeHolder getOrUpdateChunkBiomeData(ServerLevel serverLevel, ChunkAccess
            chunk, ChunkPos chunkPos) {
        int biomeDataVersion = EclipticUtil.getBiomeDataVersion(serverLevel);
        BiomeHolder biomeHolder;
        if (!chunk.hasData(AttachmentRegistry.BIOME_HOLDER)) {
            biomeHolder = BiomeHolder
                    .prepareBiomes(serverLevel, chunkPos, biomeDataVersion, false);
            chunk.setData(AttachmentRegistry.BIOME_HOLDER, biomeHolder);
        } else {
            biomeHolder = chunk.getData(AttachmentRegistry.BIOME_HOLDER);
            if (biomeHolder.hasUpdated() && biomeHolder.version() == BiomeHolder.FLAG_FILL_SMALL) {
                biomeHolder = BiomeHolder
                        .fillSmallBiomes(serverLevel, chunkPos, biomeHolder, biomeDataVersion);
                chunk.setData(AttachmentRegistry.BIOME_HOLDER, biomeHolder);
            } else if (!biomeHolder.hasUpdated() || biomeHolder.version() != biomeDataVersion) {
                biomeHolder = BiomeHolder
                        .prepareBiomes(serverLevel, chunkPos, biomeDataVersion, biomeHolder.version() != biomeDataVersion);
                chunk.setData(AttachmentRegistry.BIOME_HOLDER, biomeHolder);
            }
        }
        return biomeHolder;
    }

    public static void resetBiomeHolder(ServerLevel serverLevel, BlockPos pos) {
        int biomeDataVersion = EclipticUtil.getBiomeDataVersion(serverLevel);
        ChunkPos chunkPos = ChunkPos.containing(pos);
        var biomeHolder = BiomeHolder
                .prepareBiomes(serverLevel, chunkPos, biomeDataVersion, true);
        ChunkAccess chunk = serverLevel.getChunk(pos);
        chunk.setData(AttachmentRegistry.BIOME_HOLDER, biomeHolder);
        if (chunk instanceof IChunkBiomeHolder chunkBiomeHolder) {
            chunkBiomeHolder.eclipticseasons$resetBiomeHolder();
        }
        if (chunk instanceof LevelChunk levelChunk) {
            for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
                MapChecker.sendChunkLoginInfo(serverLevel, levelChunk, chunkPos, player);
            }
        }
    }


    public static ChunkInfoMap forceChunkUpdateHeight(Level level,
                                                      ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        ChunkInfoMap chunkMap = getChunkInfoMapOrCreate(level, checkPos);

        if (chunkMap != null) {
            SnowyRemover existingData = chunk.getExistingData(AttachmentRegistry.SNOWY_REMOVER).orElse(null);
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    checkPos.setX(i);
                    checkPos.setZ(j);
                    int k = getMCHeightWithCheck(level, checkPos, chunk, existingData, checkPos, null);
                    chunkMap.updateHeight(i, j, k);
                }
            }
        }
        return chunkMap;
    }


    public static void setNewChunk(ServerLevel serverLevel, ChunkAccess chunk) {
        if (chunk.hasData(AttachmentRegistry.BIOME_HOLDER)) {
            BiomeHolder biomeHolder = chunk.getData(AttachmentRegistry.BIOME_HOLDER);
            SolarDataManager data = SolarHolders.getSaveData(serverLevel);
            if (data != null && biomeHolder.hasUpdated()
                    && (biomeHolder.version() == BiomeHolder.FLAG_NEED_VERSION)) {
                chunk.setData(AttachmentRegistry.BIOME_HOLDER,
                        new BiomeHolder(biomeHolder.biomes(), true,
                                data.getBiomeDataVersion()));
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

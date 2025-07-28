package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;


// TODO:有空的时候要做假如搞完的话清理reset
@Mixin({LevelSlice.class})
public abstract class MixinLevelSlice implements IMapSlice {

    @Unique
    private static final int MAP_BLOCK_COUNT = 16 * 16;

    @Unique
    private static int MAP_ARRAY_SIZE;

    @Unique
    private int[][] HEIGHT_MAP;

    @Unique
    private int[][] SOLID_HEIGHT_MAP;

    @Unique
    private int[][] BIOME_MAP;

    @Unique
    private int[][] SNOWY_MAP;

    @Unique
    private int[][] SNOW_DEPTH_MAP;

    // @Shadow
    // @Final
    // private static int SECTION_ARRAY_SIZE;

    @Shadow
    private int originBlockX;

    @Shadow
    @Final
    private ClientLevel level;

    @Shadow
    private int originBlockZ;

    @Shadow
    @Final
    private static int SECTION_ARRAY_LENGTH;

    @Shadow
    private BoundingBox volume;


    @Shadow
    @Final
    private static int NEIGHBOR_CHUNK_RADIUS;

    @Shadow
    public static int getLocalSectionIndex(int sectionX, int sectionY, int sectionZ) {
        return 0;
    }

    @Shadow
    private int originBlockY;

    @Shadow
    @Final
    @Nullable
    private DataLayer[][] lightArrays;

    @Inject(
            remap = false,
            method = "<clinit>",
            at = @At(value = "TAIL")
    )
    private static void eclipticseasons$clinit(CallbackInfo ci) {
        MAP_ARRAY_SIZE = SECTION_ARRAY_LENGTH * SECTION_ARRAY_LENGTH;
    }

    @Inject(
            remap = false,
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$init(ClientLevel level, CallbackInfo ci) {
        HEIGHT_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        SOLID_HEIGHT_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        BIOME_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        SNOWY_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        SNOW_DEPTH_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
    }


    // 这里群系查询还有奇怪的零星错误
    @Inject(
            remap = false,
            method = "copyData",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$copySectionData(ChunkRenderContext context,
                                                 CallbackInfo ci) {
        // 注意别切到没有的维度了
        if (MapChecker.isValidDimension(level)) {
            int maxH = level.getMaxBuildHeight();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            // SnowyRemover snowyRemover = level.getChunk(context.getOrigin().x(), context.getOrigin().z()).getData(EclipticSeasons.ModContents.SNOWY_REMOVER.get());

            for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
                for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                    ISnowyGetter snowyGetter = (ISnowyGetter) context.getSections()[getLocalSectionIndex(sectionX, 0, sectionZ)];
                    SnowyRemover snowyRemover = snowyGetter.getSnowyRemover();
                    BiomeHolder biomeHolder = snowyGetter.getBiomeHolder();
                    int localSectionIndex = eclipticseasons$getLocalSectionIndex(sectionX, sectionZ);
                    int[] heights = HEIGHT_MAP[localSectionIndex];
                    int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];
                    int[] biomes = BIOME_MAP[localSectionIndex];
                    int[] snowys = SNOWY_MAP[localSectionIndex];

                    int startX = originBlockX + sectionX * 16;
                    int startZ = originBlockZ + sectionZ * 16;

                    mutableBlockPos.setX(startX);
                    mutableBlockPos.setZ(startZ);
                    ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                    if (chunkMap == null) {
                        MapChecker.getHeight(level, mutableBlockPos);
                        chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                    }
                    // 注意这里有个问题是，假如到不同的维度，可能会无法创建新map
                    if (chunkMap != null) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                int index = x * 16 + z;
                                mutableBlockPos.setX(startX + x);
                                mutableBlockPos.setZ(startZ + z);
                                int y = chunkMap.getHeight(mutableBlockPos);
                                heights[index] = y > chunkMap.getMinY() ? y :
                                        MapChecker.getHeight(level, mutableBlockPos);
                                // we need to get new biome
                                mutableBlockPos.setY(heights[index] + 1);
                                if (mutableBlockPos.getY() > maxH) {
                                    mutableBlockPos.setY(level.getHeight(Heightmap.Types.MOTION_BLOCKING, mutableBlockPos.getX(), mutableBlockPos.getZ()));
                                }

                                int biomeId = biomeHolder.getBiomeId(mutableBlockPos);
                                biomes[index] = biomeId > -1 ? biomeId :
                                        MapChecker.biomeToId(level, MapChecker.getUnCachedSurfaceBiome(level, mutableBlockPos).value());

                                snowys[index] = snowyRemover.blockWatcher()[x][z];

                                solidHeights[index]=snowyGetter.getSolidHeightMap().getHighestTaken(x,z);
                            }
                        }
                    } else {
                        EclipticSeasons.logger("Warning, now try create slice for invalid level", level, context.getOrigin());
                    }
                    // CompilerCollector.add(chunkPos, List.of(heights, biomes));
                }
            }

            boolean snowTransitionBlend = ClientConfig.Renderer.snowTransitionBlend.get();
            if (snowTransitionBlend) {
                Map<Holder<Biome>, Integer> snowDepthCache = new IdentityHashMap<>();
                BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
                for (int sectionX = 1; sectionX < SECTION_ARRAY_LENGTH - 1; ++sectionX) {
                    for (int sectionZ = 1; sectionZ < SECTION_ARRAY_LENGTH - 1; ++sectionZ) {
                        int localSectionIndex = eclipticseasons$getLocalSectionIndex(sectionX, sectionZ);
                        int[] snowDepths = SNOW_DEPTH_MAP[localSectionIndex];
                        int startX = originBlockX + sectionX * 16;
                        int startZ = originBlockZ + sectionZ * 16;
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                int index = x * 16 + z;
                                mutableBlockPos.setX(startX + x);
                                mutableBlockPos.setZ(startZ + z);
                                int snowDepth = 0;
                                int cc = 0;
                                for (int dx = -5; dx <= 5; dx++) {
                                    for (int dz = -5; dz <= 5; dz++) {
                                        checkPos.set(mutableBlockPos.getX() + dx, mutableBlockPos.getY(), mutableBlockPos.getZ() + dz);
                                        Holder<Biome> otherBiome = MapChecker.idToBiome(level, getSurfaceFaceBiomeId(checkPos));
                                        int neighborSnowDepth = snowDepthCache.computeIfAbsent(otherBiome, b -> WeatherManager.getSnowDepthAtBiome(level, b.value()));
                                        snowDepth += neighborSnowDepth;
                                        cc++;
                                    }
                                }
                                snowDepths[index] = snowDepth / cc;
                                snowDepthCache.clear();
                            }
                        }

                    }
                }
            }
        }
    }


    @Unique
    private static int eclipticseasons$getLocalSectionIndex(int sectionX, int sectionZ) {
        return sectionZ * SECTION_ARRAY_LENGTH + sectionX;
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return level.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.HEIGHT_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    @Override
    public int getSolidBlockHeight(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return level.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.SOLID_HEIGHT_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        // if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
        //     return 0;
        // } else
        {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.BIOME_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    // @Override
    // public int getSurfaceFaceBiomeId(BlockPos blockPos) {
    //     return MapChecker.getSurfaceOrUpdate(level, blockPos, false, ChunkInfoMap.TYPE_BIOME);
    // }

    @Override
    public int getSnowyStatus(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return SnowyRemover.SNOWY;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.SNOWY_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    @Override
    public int getSnowDepth(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return -1;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.SNOW_DEPTH_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    // /**
    //  * @author jianzoushihu
    //  * @reason For test snow transition if enable SnowTransitionBlend.
    //  */
    // @Overwrite
    // @Override
    // public int getBrightness(@NotNull LightLayer type, @NotNull BlockPos pos) {
    //     if (!ClientConfig.Renderer.snowTransitionBlend.get()
    //             && !this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
    //         return 0;
    //     } else {
    //         int relBlockX = pos.getX() - this.originBlockX;
    //         int relBlockY = pos.getY() - this.originBlockY;
    //         int relBlockZ = pos.getZ() - this.originBlockZ;
    //         DataLayer lightArray = this.lightArrays[getLocalSectionIndex(relBlockX >> 4, relBlockY >> 4, relBlockZ >> 4)][type.ordinal()];
    //         return lightArray == null ? 0 : lightArray.get(relBlockX & 15, relBlockY & 15, relBlockZ & 15);
    //     }
    // }
}

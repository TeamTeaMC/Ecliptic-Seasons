package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.client.render.chunk.CompilerCollector;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


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
    private int[][] BIOME_MAP;

    @Unique
    private int[][] SNOWY_MAP;
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
        BIOME_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        SNOWY_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
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
            int maxH=level.getMaxBuildHeight();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            // SnowyRemover snowyRemover = level.getChunk(context.getOrigin().x(), context.getOrigin().z()).getData(EclipticSeasons.ModContents.SNOWY_REMOVER.get());

            for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
                for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                    SnowyRemover snowyRemover=((ISnowyGetter)context.getSections()[getLocalSectionIndex(sectionX,0,sectionZ)]).getSnowyRemover();
                    int localSectionIndex = eclipticSeasons$getLocalSectionIndex(sectionX, sectionZ);
                    int[] heights = HEIGHT_MAP[localSectionIndex];
                    int[] biomes = BIOME_MAP[localSectionIndex];
                    int[] snowys = SNOWY_MAP[localSectionIndex];
                    int startX = originBlockX + sectionX * 16;
                    int startZ = originBlockZ + sectionZ * 16;
                    // If we have compiled the chunk
                    // ChunkPos chunkPos = new ChunkPos(
                    //         c_x + sectionX,
                    //         c_z + sectionZ);
                    // List<int[]> ints = CompilerCollector.get(chunkPos);
                    // if (ints != null) {
                    //     // System.arraycopy(ints, 0, heights, 0, heights.length);
                    //     HEIGHT_MAP[localSectionIndex] = ints.getFirst();
                    //     BIOME_MAP[localSectionIndex] = ints.get(1);
                    //     continue;
                    // }


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

                                // TODO：需要检查为啥这里总存在查询问题。有时候会查到一个默认值平原
                                // int biomeId = chunkMap.getBiome(mutableBlockPos);
                                // biomes[index] = biomeId > -1 ? biomeId :
                                //         MapChecker.getSurfaceOrUpdate(level, mutableBlockPos, false, ChunkInfoMap.TYPE_BIOME);

                                snowys[index]=snowyRemover.blockWatcher()[x][z];
                            }
                        }
                    } else {
                        EclipticSeasons.logger("Warning, now try create slice for invalid level", level, context.getOrigin());
                    }
                    // CompilerCollector.add(chunkPos, List.of(heights, biomes));
                }
            }
        }
    }


    @Unique
    private static int eclipticSeasons$getLocalSectionIndex(int sectionX, int sectionZ) {
        return sectionZ * SECTION_ARRAY_LENGTH + sectionX;
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return level.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.HEIGHT_MAP[eclipticSeasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    // TODO：存在缓存问题
    // @Override
    // public int getSurfaceFaceBiomeId(BlockPos pos) {
    //     if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
    //         return 0;
    //     } else {
    //         int relBlockX = pos.getX() - this.originBlockX;
    //         int relBlockZ = pos.getZ() - this.originBlockZ;
    //         int[] lightArrays = this.BIOME_MAP[eclipticSeasons$getLocalSectionIndex(
    //                 relBlockX >> 4,
    //                 relBlockZ >> 4)];
    //         int localBlockX = relBlockX & 15;
    //         int localBlockZ = relBlockZ & 15;
    //         return lightArrays[localBlockX * 16 + localBlockZ];
    //     }
    // }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos blockPos) {
        return MapChecker.getSurfaceOrUpdate(level, blockPos, false, ChunkInfoMap.TYPE_BIOME);
    }

    @Override
    public int getSnowyStatus(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return SnowyRemover.SNOWY;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.SNOWY_MAP[eclipticSeasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }
}

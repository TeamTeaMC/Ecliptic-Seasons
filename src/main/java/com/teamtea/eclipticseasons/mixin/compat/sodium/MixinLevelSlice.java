package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.render.chunk.CompilerCollector;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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


    @Shadow @Final private static int NEIGHBOR_CHUNK_RADIUS;

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
    }


    @Inject(
            remap = false,
            method = "copyData",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$copySectionData(ChunkRenderContext context,
                                                 CallbackInfo ci) {

        int c_x=context.getOrigin().getX() - NEIGHBOR_CHUNK_RADIUS;
        int c_z=context.getOrigin().getZ() - NEIGHBOR_CHUNK_RADIUS;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
            for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                int localSectionIndex = eclipticSeasons$getLocalSectionIndex(sectionX, sectionZ);
                int[] heights = HEIGHT_MAP[localSectionIndex];
                int[] biomes = BIOME_MAP[localSectionIndex];
                int startX = originBlockX + sectionX * 16;
                int startZ = originBlockZ + sectionZ * 16;
                // If we have compiled the chunk
                ChunkPos chunkPos = new ChunkPos(
                        c_x+sectionX,
                        c_z+sectionZ);
                List<int[]> ints = CompilerCollector.get(chunkPos);
                if (ints != null) {
                    // System.arraycopy(ints, 0, heights, 0, heights.length);
                    HEIGHT_MAP[localSectionIndex] = ints.getFirst();
                    BIOME_MAP[localSectionIndex] = ints.get(1);
                    continue;
                }


                mutableBlockPos.setX(startX);
                mutableBlockPos.setZ(startZ);
                ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                if (chunkMap == null) {
                    MapChecker.getHeight(level, mutableBlockPos);
                    chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                }
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int index = x * 16 + z;
                        mutableBlockPos.setX(startX + x);
                        mutableBlockPos.setZ(startZ + z);
                        int y = chunkMap.getHeight(mutableBlockPos);
                        heights[index] = y > chunkMap.getMinY() ? y :
                                MapChecker.getHeight(level, mutableBlockPos);
                        // we need to get new biome
                        mutableBlockPos.setY(heights[index]+1);
                        int biomeId=chunkMap.getBiome(mutableBlockPos);
                        biomes[index] = biomeId > -1 ? biomeId :
                                MapChecker.getSurfaceOrUpdate(level, mutableBlockPos, false, ChunkInfoMap.TYPE_BIOME);
                    }
                }
                CompilerCollector.add(chunkPos, List.of(heights,biomes));
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

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return level.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originBlockX;
            int relBlockZ = pos.getZ() - this.originBlockZ;
            int[] lightArrays = this.BIOME_MAP[eclipticSeasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }
}

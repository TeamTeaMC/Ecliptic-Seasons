package com.teamtea.eclipticseasons.mixin.client.render.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderRegionCache.class)
public class MixinRenderRegionCache {

    @Inject(
            method = "createRegion",
            at = @At(value = "RETURN", ordinal = 1
            )
    )
    private void eclipticseasons$compile_init_chunk(Level level,
                                                    BlockPos pStart,
                                                    BlockPos pEnd,
                                                    int pPadding,
                                                    CallbackInfoReturnable<RenderChunkRegion> cir,
                                                    @Local RenderChunk[][] arenderchunk) {
        int SIZE_X = 0;
        int SIZE_Z = 0;
        if (cir.getReturnValue() instanceof IMapSlice iMapSlice) {
            int[][] HEIGHT_MAP = null;
            int[][] SOLID_HEIGHT_MAP = null;
            int[][] BIOME_MAP = null;
            SnowyStatusKeeper[] SNOWY_STATUS_MAP = null;
            // if (MapChecker.isValidDimension(level))
            {
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                int maxH = level.getMaxBuildHeight();

                int minChunkX = SectionPos.blockToSectionCoord(pStart.getX() - pPadding);
                int minChunkZ = SectionPos.blockToSectionCoord(pStart.getZ() - pPadding);
                int maxChunkX = SectionPos.blockToSectionCoord(pEnd.getX() + pPadding);
                int maxChunkZ = SectionPos.blockToSectionCoord(pEnd.getZ() + pPadding);
                SIZE_X = maxChunkX - minChunkX + 1;
                SIZE_Z = maxChunkZ - minChunkZ + 1;
                HEIGHT_MAP = new int[SIZE_X * SIZE_Z][16 * 16];
                SOLID_HEIGHT_MAP = new int[SIZE_X * SIZE_Z][16 * 16];
                BIOME_MAP = new int[SIZE_X * SIZE_Z][16 * 16];
                SNOWY_STATUS_MAP = new SnowyStatusKeeper[SIZE_X * SIZE_Z];

                for (int sectionX = minChunkX; sectionX < minChunkX + SIZE_X; ++sectionX) {
                    for (int sectionZ = minChunkZ; sectionZ < minChunkZ + SIZE_Z; ++sectionZ) {
                        int localSectionIndex =
                                sectionX - minChunkX + (sectionZ - minChunkZ) * SIZE_X;
                        LevelChunk wrapped = arenderchunk[sectionX - minChunkX][sectionZ - minChunkZ].wrapped;
                        Heightmap heightmap = wrapped.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
                        ChunkPos chunkPos = wrapped.getPos();
                        BiomeHolder biomeHolder = ((IChunkBiomeHolder) wrapped).eclipticseasons$getBiomeHolder();
                        int[] heights = HEIGHT_MAP[localSectionIndex];
                        int[] biomes = BIOME_MAP[localSectionIndex];
                        int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];
                        SNOWY_STATUS_MAP[localSectionIndex] = SnowyMapChecker.getSnowyStatusKeeperCopy(wrapped);
                        int startX = chunkPos.getMinBlockX();
                        int startZ = chunkPos.getMinBlockZ();
                        mutableBlockPos.setX(startX);
                        mutableBlockPos.setZ(startZ);
                        ChunkInfoMap chunkMap = MapChecker.getChunkInfoMapOrCreate(level, mutableBlockPos);
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
                                    solidHeights[index] = heightmap.getHighestTaken(x, z);
                                }
                            }
                        }
                    }
                }
            }
            iMapSlice.forceMapSliceUpdate(HEIGHT_MAP, SOLID_HEIGHT_MAP, BIOME_MAP, SIZE_X, SIZE_Z,SNOWY_STATUS_MAP);
        }
    }
}

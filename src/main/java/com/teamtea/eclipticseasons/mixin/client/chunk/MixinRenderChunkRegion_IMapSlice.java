package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderChunkRegion.class})
public abstract class MixinRenderChunkRegion_IMapSlice implements IMapSlice {

    @Shadow
    @Final
    protected Level level;
    @Shadow
    @Final
    public static int SIZE;

    @Shadow
    public static int index(int minX, int minZ, int x, int z) {
        return 0;
    }

    @Shadow
    @Final
    private int minChunkX;
    @Shadow
    @Final
    private int minChunkZ;

    @Shadow
    protected abstract RenderChunk getChunk(int x, int z);

    @Unique
    private static final int MAP_BLOCK_COUNT = 16 * 16;

    @Unique
    private int[][] HEIGHT_MAP;

    @Unique
    private int[][] BIOME_MAP;

    @Unique
    private int[][] SNOWY_MAP;

    @Inject(
            remap = false,
            method = "<init>(Lnet/minecraft/world/level/Level;II[Lnet/minecraft/client/renderer/chunk/RenderChunk;Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)V",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$init(Level level, int minChunkX, int minChunkZ, RenderChunk[] chunks, it.unimi.dsi.fastutil.longs.Long2ObjectFunction<net.neoforged.neoforge.client.model.data.ModelData> modelDataSnapshot, CallbackInfo ci) {
        HEIGHT_MAP = new int[SIZE * SIZE][MAP_BLOCK_COUNT];
        BIOME_MAP = new int[SIZE * SIZE][MAP_BLOCK_COUNT];
        SNOWY_MAP = new int[SIZE * SIZE][MAP_BLOCK_COUNT];


    }

    @Override
    public void forceMapSliceUpdate() {
        if (MapChecker.isValidDimension(level)) {
            int maxH = level.getMaxBuildHeight();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int sectionX = minChunkX; sectionX < minChunkX + SIZE; ++sectionX) {
                for (int sectionZ = minChunkZ; sectionZ < minChunkZ + SIZE; ++sectionZ) {
                    int localSectionIndex = index(minChunkX, minChunkZ, sectionX, sectionZ);
                    LevelChunk wrapped = getChunk(sectionX, sectionZ).wrapped;
                    ChunkPos chunkPos = wrapped.getPos();
                    SnowyRemover snowyRemover = wrapped.getData(AttachmentRegistry.SNOWY_REMOVER);
                    BiomeHolder biomeHolder = wrapped.getData(AttachmentRegistry.BIOME_HOLDER);
                    int[] heights = HEIGHT_MAP[localSectionIndex];
                    int[] biomes = BIOME_MAP[localSectionIndex];
                    int[] snowys = SNOWY_MAP[localSectionIndex];
                    int startX = chunkPos.getMinBlockX();
                    int startZ = chunkPos.getMinBlockZ();

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
                                        MapChecker.biomeToId(level,MapChecker.getUnCachedSurfaceBiome(level, mutableBlockPos).value());

                                snowys[index] = snowyRemover.blockWatcher()[x][z];
                            }
                        }
                    } else {
                        EclipticSeasons.logger("Warning, now try create slice for invalid level", level);
                    }
                    // CompilerCollector.add(chunkPos, List.of(heights, biomes));
                }
            }
        }
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());
        int[] lightArrays = this.HEIGHT_MAP[index(minChunkX, minChunkZ, relBlockX, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
        // return MapChecker.getHeight(level, pos);
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());
        int[] lightArrays = this.BIOME_MAP[index(minChunkX, minChunkZ, relBlockX, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getSnowyStatus(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());

        int[] lightArrays = this.SNOWY_MAP[index(minChunkX, minChunkZ, relBlockX, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
        // return level.getChunk(pos) instanceof ChunkAccess chunkAccess
        //         ? chunkAccess.getData(EclipticSeasons.ModContents.SNOWY_REMOVER).getSnowyFlag(pos).ordinal() : SnowyRemover.SNOWY;
    }
}

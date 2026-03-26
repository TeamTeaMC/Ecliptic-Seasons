package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.context.ExtraRendererContext;
import com.teamtea.eclipticseasons.common.core.map.*;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderSectionRegion.class})
public abstract class MixinRenderChunkRegion_IMapSlice implements IMapSlice, IExtraRendererContextOwner {

    @Shadow
    @Final
    private ClientLevel level;
    @Shadow
    @Final
    public static int SIZE;
    @Shadow
    @Final
    private int minSectionX;
    @Shadow
    @Final
    private int minSectionY;
    @Shadow
    @Final
    private int minSectionZ;

    @Shadow
    public static int index(int minSectionX, int minSectionY, int minSectionZ, int sectionX, int sectionY, int sectionZ) {
        return 0;
    }

    @Shadow
    protected abstract SectionCopy getSection(int sectionX, int sectionY, int sectionZ);


    @Unique
    private static final int MAP_BLOCK_COUNT = 16 * 16;

    @Unique
    private int[][] HEIGHT_MAP;

    @Unique
    private int[][] SOLID_HEIGHT_MAP;

    @Unique
    private int[][] BIOME_MAP;

    @Unique
    private int[][] SNOWY_MAP;

    @Unique
    private SnowyStatusKeeper[] SNOWY_STATUS_MAP;

    @Unique
    private NoneSnowArea[] NONE_SNOW_AREA_MAP;

    @Inject(
            remap = false,
            method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;III[Lnet/minecraft/client/renderer/chunk/SectionCopy;Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)V",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$init(ClientLevel level, int minSectionX, int minSectionY, int minSectionZ, SectionCopy[] sections, Long2ObjectFunction modelDataSnapshot, CallbackInfo ci) {
        HEIGHT_MAP = new int[SIZE * SIZE * SIZE][MAP_BLOCK_COUNT];
        BIOME_MAP = new int[SIZE * SIZE * SIZE][MAP_BLOCK_COUNT];
        SNOWY_MAP = new int[SIZE * SIZE * SIZE][MAP_BLOCK_COUNT];
        SOLID_HEIGHT_MAP = new int[SIZE * SIZE * SIZE][MAP_BLOCK_COUNT];
        SNOWY_STATUS_MAP = new SnowyStatusKeeper[SIZE * SIZE * SIZE];
        NONE_SNOW_AREA_MAP = new NoneSnowArea[SIZE * SIZE * SIZE];
    }

    @Override
    public void forceMapSliceUpdate() {
        // if (MapChecker.isValidDimension(level))
        {
            int maxH = level.getMaxY();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int regionSectionZ = minSectionZ; regionSectionZ < minSectionZ + SIZE; regionSectionZ++) {
                for (int regionSectionY = minSectionY; regionSectionY < minSectionY + SIZE; regionSectionY++) {
                    for (int regionSectionX = minSectionX; regionSectionX < minSectionX + SIZE; regionSectionX++) {
                        int localSectionIndex = RenderSectionRegion.index(minSectionX, minSectionY, minSectionZ, regionSectionX, regionSectionY, regionSectionZ);
                        LevelChunk wrapped = getSection(regionSectionX, regionSectionY, regionSectionZ).wrapped;
                        Heightmap heightmap = wrapped.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
                        ChunkPos chunkPos = wrapped.getPos();
                        SnowyRemover snowyRemover = wrapped.getData(AttachmentRegistry.SNOWY_REMOVER);
                        BiomeHolder biomeHolder = wrapped.getData(AttachmentRegistry.BIOME_HOLDER);
                        int[] heights = HEIGHT_MAP[localSectionIndex];
                        int[] biomes = BIOME_MAP[localSectionIndex];
                        int[] snowys = SNOWY_MAP[localSectionIndex];
                        int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];
                        SNOWY_STATUS_MAP[localSectionIndex] = SnowyMapChecker.getSnowyStatusKeeperCopy(wrapped);
                        NONE_SNOW_AREA_MAP[localSectionIndex] = wrapped.getData(AttachmentRegistry.NONE_SNOW_AREA);

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
                                            MapChecker.biomeToId(level, MapChecker.getUnCachedSurfaceBiome(level, mutableBlockPos).value());

                                    snowys[index] = snowyRemover.blockWatcher()[x][z];

                                    solidHeights[index] = heightmap.getHighestTaken(x, z);

                                }
                            }
                        } else {
                            EclipticSeasons.logger("Warning, now try create slice for invalid level", level);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockY = SectionPos.blockToSectionCoord(pos.getY());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());
        int[] lightArrays = this.HEIGHT_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
        // return MapChecker.getHeight(level, pos);
    }

    @Override
    public int getSolidBlockHeight(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockY = SectionPos.blockToSectionCoord(pos.getY());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());
        int[] lightArrays = this.SOLID_HEIGHT_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];

        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockY = SectionPos.blockToSectionCoord(pos.getY());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());

        int[] lightArrays = this.BIOME_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getSnowyStatus(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockY = SectionPos.blockToSectionCoord(pos.getY());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());

        NoneSnowArea lightArrays0 = this.NONE_SNOW_AREA_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];
        if (lightArrays0 != null && lightArrays0.neverSnowyAt(pos))
            return SnowyRemover.NONE_SNOWY;

        int[] lightArrays = this.SNOWY_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public boolean isSnowyBlock(BlockPos pos) {
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX());
        int relBlockY = SectionPos.blockToSectionCoord(pos.getY());
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ());
        SnowyStatusKeeper lightArrays = this.SNOWY_STATUS_MAP[index(minSectionX, minSectionY, minSectionZ, relBlockX, relBlockY, relBlockZ)];


        return lightArrays != null && lightArrays.isSnowyBlock(pos);
    }

    /* ======================================== MODEL PART ===================================== */


    @Unique
    private ExtraRendererContext eclipticseasons$rendererHolder = new ExtraRendererContext();

    @Override
    public ExtraRendererContext eclipticseasons$getContext() {
        return eclipticseasons$rendererHolder;
    }

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$checkPos = new BlockPos.MutableBlockPos();

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$checkPos;
    }

}

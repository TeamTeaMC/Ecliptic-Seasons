package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetterProvider;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(WorldSlice.class)
public abstract class MixinChunkSlice implements IMapSlice, IExtendBlockView {

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

    @Shadow(remap = false)
    @Final
    private static int SECTION_ARRAY_LENGTH;

    @Shadow(remap = false)
    private BoundingBox volume;

    @Shadow(remap = false)
    @Final
    private static int NEIGHBOR_CHUNK_RADIUS;

    @Shadow(remap = false)
    public static int getLocalSectionIndex(int sectionX, int sectionY, int sectionZ) {
        return 0;
    }

    @Shadow(remap = false)
    @Final
    public ClientLevel world;

    @Shadow(remap = false)
    private int originX;

    @Shadow(remap = false)
    private int originZ;

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
    }


    @Inject(
            remap = false,
            method = "copyData",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$copySectionData(ChunkRenderContext context, CallbackInfo ci) {
        if (MapChecker.isValidDimension(world)) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int maxH = world.getMaxBuildHeight();
            for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
                for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                    ClonedChunkSection contextSection = context.getSections()[getLocalSectionIndex(sectionX, 0, sectionZ)];
                    ISnowyGetterProvider snowyGetter = (ISnowyGetterProvider) contextSection;
                    int localSectionIndex = eclipticseasons$getLocalSectionIndex(sectionX, sectionZ);
                    ChunkPos chunkPos = contextSection.getPosition().chunk();
                    int startX = chunkPos.getMinBlockX();
                    int startZ = chunkPos.getMinBlockZ();
                    mutableBlockPos.setX(startX);
                    mutableBlockPos.setZ(startZ);
                    int[] heights = HEIGHT_MAP[localSectionIndex];
                    int[] biomes = BIOME_MAP[localSectionIndex];
                    int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];
                    mutableBlockPos.setX(startX);
                    mutableBlockPos.setZ(startZ);
                    ChunkInfoMap chunkMap = snowyGetter.getChunkInfoMap();

                    if (chunkMap != null) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                int index = x * 16 + z;

                                mutableBlockPos.setX(startX + x);
                                mutableBlockPos.setZ(startZ + z);
                                int y = chunkMap.getHeight(mutableBlockPos);
                                heights[index] = y > chunkMap.getMinY() ? y :
                                        MapChecker.getHeight(world, mutableBlockPos);
                                // we need to get new biome
                                mutableBlockPos.setY(heights[index] + 1);
                                if (mutableBlockPos.getY() > maxH) {
                                    mutableBlockPos.setY(world.getHeight(Heightmap.Types.MOTION_BLOCKING, mutableBlockPos.getX(), mutableBlockPos.getZ()));
                                }

                                int biomeId = chunkMap.getBiome(mutableBlockPos);
                                biomes[index] = biomeId > -1 ? biomeId :
                                        MapChecker.biomeToId(world, MapChecker.getSurfaceBiome(world, mutableBlockPos).value());

                                solidHeights[index] = snowyGetter.getSolidHeightMap().getHighestTaken(x, z);

                            }
                        }
                    } else {
                        EclipticSeasons.logger("Warning, now try create slice for invalid level", world, context.getOrigin());
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
    public int getSolidBlockHeight(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return world.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originX;
            int relBlockZ = pos.getZ() - this.originZ;
            int[] lightArrays = this.SOLID_HEIGHT_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return world.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originX;
            int relBlockZ = pos.getZ() - this.originZ;
            int[] lightArrays = this.HEIGHT_MAP[eclipticseasons$getLocalSectionIndex(
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
            return world.getMaxBuildHeight() + 1;
        } else {
            int relBlockX = pos.getX() - this.originX;
            int relBlockZ = pos.getZ() - this.originZ;
            int[] lightArrays = this.BIOME_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    /* ======================================== MODEL PART ===================================== */

    @Inject(
            remap = false,
            method = "reset",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$release(CallbackInfo ci) {
        eclipticseasons$setSnowModel(null);
    }

    @Unique
    BakedModel eclipticseasons$bakedModelSnow = null;

    @Override
    public BakedModel eclipticseasons$getSnowModel() {
        return this.eclipticseasons$bakedModelSnow;
    }

    @Override
    public void eclipticseasons$setSnowModel(BakedModel bakedModel) {
        this.eclipticseasons$bakedModelSnow = bakedModel;
    }

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$checkPos = new BlockPos.MutableBlockPos();

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$checkPos;
    }
}

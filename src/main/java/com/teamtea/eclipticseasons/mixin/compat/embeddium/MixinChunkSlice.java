package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.embeddedt.embeddium.impl.world.WorldSlice;
import org.embeddedt.embeddium.impl.world.cloned.ChunkRenderContext;
import org.embeddedt.embeddium.impl.world.cloned.ClonedChunkSection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Pseudo
@Mixin(WorldSlice.class)
public abstract class MixinChunkSlice implements IMapSlice, IExtraRendererContextOwner {
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
    private SnowyStatusKeeper[] SNOWY_STATUS_MAP;

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
    @Final
    public ClientLevel world;

    @Shadow
    private int originX;

    @Shadow
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
        SNOWY_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
        SNOWY_STATUS_MAP = new SnowyStatusKeeper[MAP_ARRAY_SIZE];
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
                    ISnowyGetter snowyGetter = (ISnowyGetter) contextSection;
                    BiomeHolder biomeHolder = snowyGetter.getBiomeHolder();
                    SnowyRemover snowyRemover = snowyGetter.getSnowyRemover();
                    int localSectionIndex = eclipticseasons$getLocalSectionIndex(sectionX, sectionZ);
                    ChunkPos chunkPos = contextSection.getPosition().chunk();
                    int startX = chunkPos.getMinBlockX();
                    int startZ = chunkPos.getMinBlockZ();
                    mutableBlockPos.setX(startX);
                    mutableBlockPos.setZ(startZ);
                    int[] heights = HEIGHT_MAP[localSectionIndex];
                    int[] biomes = BIOME_MAP[localSectionIndex];
                    int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];
                    int[] snowys = SNOWY_MAP[localSectionIndex];
                    SNOWY_STATUS_MAP[localSectionIndex] = snowyGetter.getSnowyStatusKeeper();
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

                                int biomeId = biomeHolder.getBiomeId(mutableBlockPos);
                                biomes[index] = biomeId > -1 ? biomeId :
                                        MapChecker.biomeToId(world, MapChecker.getUnCachedSurfaceBiome(world, mutableBlockPos).value());

                                snowys[index] = snowyRemover.blockWatcher()[x][z];

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


    @Override
    public int getSnowyStatus(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return SnowyRemover.SNOWY;
        } else {
            int relBlockX = pos.getX() - this.originX;
            int relBlockZ = pos.getZ() - this.originZ;
            int[] lightArrays = this.SNOWY_MAP[eclipticseasons$getLocalSectionIndex(
                    relBlockX >> 4,
                    relBlockZ >> 4)];
            int localBlockX = relBlockX & 15;
            int localBlockZ = relBlockZ & 15;
            return lightArrays[localBlockX * 16 + localBlockZ];
        }
    }

    @Override
    public boolean isSnowyBlock(BlockPos pos) {
        if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
            return false;
        }
        int relBlockX = pos.getX() - this.originX;
        int relBlockZ = pos.getZ() - this.originZ;
        SnowyStatusKeeper lightArrays = this.SNOWY_STATUS_MAP[eclipticseasons$getLocalSectionIndex(
                relBlockX >> 4,
                relBlockZ >> 4)];
        return lightArrays.isSnowyBlock(pos);
    }

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$checkPos = new BlockPos.MutableBlockPos();

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$checkPos;
    }

    /* ======================================== MODEL PART ===================================== */

    @Inject(
            remap = false,
            method = "reset",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$release(CallbackInfo ci) {
        eclipticseasons$rendererHolder.resetAll();
        for (int sectionIndex = 0; sectionIndex < SECTION_ARRAY_LENGTH; ++sectionIndex) {
            this.SNOWY_STATUS_MAP[sectionIndex] = null;
        }
    }


    @Unique
    private ExtraRendererContext eclipticseasons$rendererHolder = new ExtraRendererContext();

    @Override
    public ExtraRendererContext eclipticseasons$getContext() {
        return eclipticseasons$rendererHolder;
    }

}

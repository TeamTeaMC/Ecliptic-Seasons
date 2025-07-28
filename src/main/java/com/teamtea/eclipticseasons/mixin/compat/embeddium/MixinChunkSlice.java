package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetterProvider;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.embeddedt.embeddium.impl.world.WorldSlice;
import org.embeddedt.embeddium.impl.world.cloned.ChunkRenderContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(WorldSlice.class)
public abstract class MixinChunkSlice implements IMapSliceProvider {
    @Unique
    private static final int MAP_BLOCK_COUNT = 16 * 16;

    @Unique
    private static int MAP_ARRAY_SIZE;

    @Unique
    private int[][] SOLID_HEIGHT_MAP;

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
        SOLID_HEIGHT_MAP = new int[MAP_ARRAY_SIZE][MAP_BLOCK_COUNT];
    }


    @Inject(
            remap = false,
            method = "copyData",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$copySectionData(ChunkRenderContext context,
                                                 CallbackInfo ci) {
        if (MapChecker.isValidDimension(world)) {
            for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
                for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                    ISnowyGetterProvider snowyGetter = (ISnowyGetterProvider) context.getSections()[getLocalSectionIndex(sectionX, 0, sectionZ)];
                    int localSectionIndex = eclipticseasons$getLocalSectionIndex(sectionX, sectionZ);
                    int[] solidHeights = SOLID_HEIGHT_MAP[localSectionIndex];

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int index = x * 16 + z;
                            solidHeights[index] = snowyGetter.getSolidHeightMap().getHighestTaken(x, z);
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


}

package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.api.misc.IMapSlice;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;copySectionData(Lnet/caffeinemc/mods/sodium/client/world/cloned/ChunkRenderContext;I)V")
    )
    private void eclipticseasons$copySectionData(ChunkRenderContext context,
                                                 CallbackInfo ci) {

        for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
            for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
                int localSectionIndex = eclipticSeasons$getLocalSectionIndex(sectionX, sectionZ);
                int[] heights = HEIGHT_MAP[localSectionIndex];


                int startX = originBlockX + sectionX * 16;
                int startZ = originBlockZ + sectionZ * 16;
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(startX, 0, startZ);
                ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                if (chunkMap == null) {
                    MapChecker.getHeight(level, mutableBlockPos);
                    chunkMap = MapChecker.getChunkMap(level, mutableBlockPos);
                }
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int id = x * 16 + z;
                        mutableBlockPos.set(startX + x, 0, startZ + z);
                        int y = chunkMap.getHeight(mutableBlockPos);
                        heights[id] = y > chunkMap.getMinY() ? y :
                                MapChecker.getHeight(level, mutableBlockPos);
                    }
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

    @Override
    public int getSurfaceFaceBiomeId(BlockPos blockPos) {
        return 0;
    }
}

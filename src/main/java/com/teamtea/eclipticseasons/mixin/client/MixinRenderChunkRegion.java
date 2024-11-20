package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({RenderChunkRegion.class})
public abstract class MixinRenderChunkRegion implements IMapSlice {

    @Shadow
    @Final
    protected Level level;
    @Unique
    private static final int MAP_BLOCK_COUNT = 16 * 16;

    @Unique
    private int[][] HEIGHT_MAP;

    @Unique
    private int[][] BIOME_MAP;


    // @Inject(
    //         remap = false,
    //         method = "<init>(Lnet/minecraft/world/level/Level;II[Lnet/minecraft/client/renderer/chunk/RenderChunk;Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)V",
    //         at = @At(value = "TAIL")
    // )
    // private void eclipticseasons$init(Level pLevel, int pMinChunkX, int pMinChunkZ, RenderChunk[] pChunks, Long2ObjectFunction modelDataSnapshot, CallbackInfo ci) {
    //     HEIGHT_MAP = new int[SECTION_ARRAY_SIZE][MAP_BLOCK_COUNT];
    //     BIOME_MAP = new int[SECTION_ARRAY_SIZE][MAP_BLOCK_COUNT];
    // }
    //
    // @Inject(
    //         remap = false,
    //         method = "copyData",
    //         at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;copySectionData(Lnet/caffeinemc/mods/sodium/client/world/cloned/ChunkRenderContext;I)V")
    // )
    // private void eclipticseasons$copySectionData(ChunkRenderContext context,
    //                                              CallbackInfo ci) {
    //
    //     for (int sectionX = 0; sectionX < SECTION_ARRAY_LENGTH; ++sectionX) {
    //         for (int sectionZ = 0; sectionZ < SECTION_ARRAY_LENGTH; ++sectionZ) {
    //             int localSectionIndex = eclipticSeasons$getLocalSectionIndex(sectionX, sectionZ);
    //             int[] heights = HEIGHT_MAP[localSectionIndex];
    //
    //             BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
    //             int startX = originBlockX + sectionX * 16;
    //             int startZ = originBlockZ + sectionZ * 16;
    //
    //             for (int x = 0; x < 16; x++) {
    //                 for (int z = 0; z < 16; z++) {
    //                     int id = x * 16 + z;
    //                     mutableBlockPos.set(startX + x, 0, startZ + z);
    //                     heights[id] = MapChecker.getHeight(level, mutableBlockPos);
    //                 }
    //             }
    //         }
    //     }
    // }


    @Override
    public int getBlockHeight(BlockPos pos) {
        // return 0;
        // if (!this.volume.isInside(pos.getX(), pos.getY(), pos.getZ())) {
        //     return level.getMaxBuildHeight() + 1;
        // } else {
        //     int relBlockX = pos.getX() - this.originBlockX;
        //     int relBlockZ = pos.getZ() - this.originBlockZ;
        //     int[] lightArrays = this.HEIGHT_MAP[eclipticSeasons$getLocalSectionIndex(
        //             relBlockX >> 4,
        //             relBlockZ >> 4)];
        //     int localBlockX = relBlockX & 15;
        //     int localBlockZ = relBlockZ & 15;
        //     return lightArrays[localBlockX*16+localBlockZ];
        // }
        return MapChecker.getHeight(level, pos);
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos blockPos) {
        return MapChecker.getSurfaceOrUpdate(level, blockPos, false, ChunkInfoMap.TYPE_BIOME);
    }
}

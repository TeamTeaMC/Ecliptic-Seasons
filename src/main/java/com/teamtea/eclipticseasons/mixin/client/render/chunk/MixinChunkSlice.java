package com.teamtea.eclipticseasons.mixin.client.render.chunk;

import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(RenderChunkRegion.class)
public abstract class MixinChunkSlice implements IMapSlice, IExtraRendererContextOwner {
    @Shadow
    @Final
    protected Level level;

    @Shadow
    @Final
    private int centerX;
    @Shadow
    @Final
    private int centerZ;

    @Unique
    private int[][] HEIGHT_MAP;

    @Unique
    private int[][] SOLID_HEIGHT_MAP;

    @Unique
    private int[][] BIOME_MAP;

    @Unique
    private int SIZE_X;
    @Unique
    private int SIZE_Z;

    @Unique
    private SnowyStatusKeeper[] SNOWY_STATUS_MAP;

    @Unique
    private NoneSnowArea[] NONE_SNOW_AREA_MAP;

    @Override
    public void forceMapSliceUpdate(int[][] heights, int[][] solidHeights, int[][] biomes, int sizex, int sizez, SnowyStatusKeeper[] statusKeepers, NoneSnowArea[] noneSnowAreaMap) {
        HEIGHT_MAP = heights;
        SOLID_HEIGHT_MAP = solidHeights;
        BIOME_MAP = biomes;
        SIZE_X = sizex;
        SIZE_Z = sizez;
        this.SNOWY_STATUS_MAP = statusKeepers;
        this.NONE_SNOW_AREA_MAP = noneSnowAreaMap;
    }

    @Override
    public int getSolidBlockHeight(BlockPos pos) {
        if (SOLID_HEIGHT_MAP == null || SOLID_HEIGHT_MAP[0] == null) return 0;
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ;
        int[] lightArrays = this.SOLID_HEIGHT_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getBlockHeight(BlockPos pos) {
        if (HEIGHT_MAP == null || HEIGHT_MAP[0] == null) return 0;
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ;
        int[] lightArrays = this.HEIGHT_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        if (BIOME_MAP == null || BIOME_MAP[0] == null) return 0;
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ;
        int[] lightArrays = this.BIOME_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    @Override
    public int getSnowyStatus(BlockPos pos) {
        if (NONE_SNOW_AREA_MAP == null || NONE_SNOW_AREA_MAP[0] == null)
            return IMapSlice.super.getSnowyStatus(pos);
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ;

        NoneSnowArea lightArrays0 = this.NONE_SNOW_AREA_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
        if (lightArrays0 != null && lightArrays0.neverSnowyAt(pos))
            return SnowyRemover.NONE_SNOWY;

        // int[] lightArrays = this.SNOWY_MAP[index(minChunkX, minChunkZ, relBlockX, relBlockZ)];
        // int localBlockX = pos.getX() & 15;
        // int localBlockZ = pos.getZ() & 15;
        // return lightArrays[localBlockX * 16 + localBlockZ];
        return SnowyRemover.SNOWY;
    }

    @Override
    public boolean isSnowyBlock(BlockPos pos) {
        if (SNOWY_STATUS_MAP == null || SNOWY_STATUS_MAP[0] == null) return false;
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ;
        SnowyStatusKeeper lightArrays = this.SNOWY_STATUS_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
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
    private BlockPos.MutableBlockPos eclipticseasons$mutableBlockPos = new BlockPos.MutableBlockPos();

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$mutableBlockPos;
    }
}

package com.teamtea.eclipticseasons.mixin.client.render.chunk;

import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(RenderChunkRegion.class)
public abstract class MixinChunkSlice implements IMapSlice, ExtendBlockView {
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
    private int[][] SOLID_HEIGHT_MAP = null;
    @Unique
    private int SIZE_X = 0;
    @Unique
    private int SIZE_Z = 0;

    @Override
    public void forceMapSliceUpdate(int[][] ints, int sizex, int sizez) {
        SOLID_HEIGHT_MAP = ints;
        SIZE_X = sizex;
        SIZE_Z = sizez;
    }

    @Override
    public int getSolidBlockHeight(BlockPos pos) {
        if (SOLID_HEIGHT_MAP == null || SOLID_HEIGHT_MAP[0] == null) return 0;
        int relBlockX = SectionPos.blockToSectionCoord(pos.getX()) - centerX ;
        int relBlockZ = SectionPos.blockToSectionCoord(pos.getZ()) - centerZ ;
        int[] lightArrays = this.SOLID_HEIGHT_MAP[
                relBlockX + (relBlockZ) * SIZE_X];
        int localBlockX = pos.getX() & 15;
        int localBlockZ = pos.getZ() & 15;
        return lightArrays[localBlockX * 16 + localBlockZ];
    }

    /* ======================================== MODEL PART ===================================== */


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
    private BlockPos.MutableBlockPos eclipticseasons$mutableBlockPos =new BlockPos.MutableBlockPos();

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return eclipticseasons$mutableBlockPos;
    }
}

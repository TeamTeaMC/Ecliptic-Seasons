package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.compat.optfine.IOFModelTaker;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@Pseudo
@Mixin(targets = "net.optifine.override.ChunkCacheOF")
// @Mixin(CompatModule.class)
public abstract class MixinChunkSlice implements IOFModelTaker, IMapSliceProvider, IExtendBlockView {

    @Final
    @Shadow(remap = false)
    private RenderChunkRegion chunkCache;

    @Unique
    private final Map<BakedModel, BakedModel> eclipticseasons$modelCache = new IdentityHashMap<>();

    @Unique
    private final Map<BakedModel, BakedModel> eclipticseasons$modelCache2 = new IdentityHashMap<>();

    @Inject(
            remap = false,
            method = "renderFinish",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$release(CallbackInfo ci) {
        eclipticseasons$setSnowModel(null);
        eclipticseasons$modelCache.clear();
        eclipticseasons$modelCache2.clear();
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

    @Override
    public BakedModel eclipticseasons$hasCache(BakedModel bakedModel, boolean special) {
        return (special ? eclipticseasons$modelCache2 :
                eclipticseasons$modelCache).getOrDefault(bakedModel, null);
    }

    @Override
    public void eclipticseasons$setCache(BakedModel bakedModel, BakedModel bakedModel2, boolean special) {
        (special ? eclipticseasons$modelCache2 :
                eclipticseasons$modelCache).put(bakedModel, bakedModel2);
    }


    @Override
    public int getBlockHeight(BlockPos pos) {
        return ((IMapSlice) chunkCache).getBlockHeight(pos);
    }

    @Override
    public int getSolidBlockHeight(BlockPos pos) {
        return ((IMapSliceProvider) chunkCache).getSolidBlockHeight(pos);
    }

    @Override
    public BlockPos.MutableBlockPos getModelCheckPos() {
        return ((IExtendBlockView) chunkCache).getModelCheckPos();
    }

    @Override
    public int getSurfaceFaceBiomeId(BlockPos pos) {
        return ((IMapSlice) chunkCache).getSurfaceFaceBiomeId(pos);
    }

}

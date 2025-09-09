package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.teamtea.eclipticseasons.api.misc.client.IESRendererHolder;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.client.core.ESRendererHolderImpl;
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
public abstract class MixinChunkSlice implements IOFModelTaker, IESRendererHolder {

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
        eclipticseasons$rendererHolder.resetAll();
        eclipticseasons$modelCache.clear();
        eclipticseasons$modelCache2.clear();
    }

    private ESRendererHolderImpl eclipticseasons$rendererHolder =new ESRendererHolderImpl();

    @Override
    public ESRendererHolderImpl eclipticseasons$getContext() {
        return eclipticseasons$rendererHolder;
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

    @Override
    public boolean isSnowyBlock(BlockPos pos) {
        return ((IMapSlice) chunkCache).isSnowyBlock(pos);
    }
}

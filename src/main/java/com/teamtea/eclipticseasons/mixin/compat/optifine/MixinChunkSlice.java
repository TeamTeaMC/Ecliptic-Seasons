package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.teamtea.eclipticseasons.compat.optfine.IOFModelTaker;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@Pseudo
@Mixin(targets = "net.optifine.override.ChunkCacheOF")
// @Mixin(CompatModule.class)
public abstract class MixinChunkSlice implements IOFModelTaker {


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
}

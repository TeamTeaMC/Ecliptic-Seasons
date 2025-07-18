package com.teamtea.eclipticseasons.mixin.client.model;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(net.minecraft.client.resources.model.ModelManager.class)
public abstract class MixinModelManager {

    // @Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;loadBlockModels(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    // private void eclipticseasons$loadBlockStates(
    //         PreparableReloadListener.PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
    //     ClientJsonCacheListener.modelDefCache.prepare(
    //             pResourceManager, pPreparationsProfiler
    //     );
    // }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;loadBlockStates(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> eclipticseasons$loadBlockStates_before_loadModelDef(
            CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> original,
            @Local(ordinal = 0, argsOnly = true) ProfilerFiller preparationsProfiler,
            @Local(argsOnly = true) ResourceManager resourceManager,
            @Local(ordinal = 0, argsOnly = true) Executor executor

    ) {
        return CompletableFuture.allOf(
                ClientJsonCacheListener.modelDefCache.prepareAsync(
                        resourceManager, preparationsProfiler, executor
                ),
                ClientJsonCacheListener.textureReMappingsCache.prepareAsync(
                        resourceManager, preparationsProfiler, executor
                ).thenRun(() -> {
                    ExtraModelManager.SEASONAL_TEXTURE_HASH_MAP.clear();
                    Map<ResourceLocation, SeasonalTexture> build = ClientJsonCacheListener.textureReMappingsCache.build(SeasonalTexture.CODEC);
                    build.forEach(
                            (resourceLocation, seasonalTexture) -> {
                                seasonalTexture = seasonalTexture.build(resourceLocation);
                                List<SeasonalTexture> seasonalTextures = ExtraModelManager.SEASONAL_TEXTURE_HASH_MAP.computeIfAbsent(
                                        seasonalTexture.getParent().orElseGet(() -> resourceLocation.withPrefix("block/")), (xx) -> new ArrayList<>());
                                seasonalTextures.add(seasonalTexture);
                            }
                    );
                })
        ).thenCompose(ignored -> original);
    }
}

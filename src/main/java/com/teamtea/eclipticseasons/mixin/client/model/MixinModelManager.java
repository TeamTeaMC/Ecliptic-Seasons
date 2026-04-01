package com.teamtea.eclipticseasons.mixin.client.model;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.Identifier;
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

    // @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BlockStateModelLoader;loadBlockStates(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    // private CompletableFuture<Map<Identifier, List<BlockStateModelLoader.LoadedModels>>> eclipticseasons$loadBlockStates_before_loadModelDef(
    //         CompletableFuture<Map<Identifier, List<BlockStateModelLoader.LoadedModels>>> original,
    //         @Local(name = "manager") ResourceManager resourceManager,
    //         @Local(ordinal = 0, argsOnly = true) Executor executor
    //
    // ) {
    //     return CompletableFuture.allOf(
    //             ClientJsonCacheListener.modelDefCache.prepareAsync(
    //                     resourceManager, executor
    //             ),
    //             ClientJsonCacheListener.textureReMappingsCache.prepareAsync(
    //                     resourceManager, executor
    //             ).thenRun(() -> {
    //                 ExtraModelManager.SEASONAL_TEXTURE_HASH_MAP.clear();
    //                 Map<Identifier, SeasonalTexture> build = ClientJsonCacheListener.textureReMappingsCache.build(SeasonalTexture.CODEC);
    //                 build.forEach(
    //                         (Identifier, seasonalTexture) -> {
    //                             seasonalTexture = seasonalTexture.build(Identifier);
    //                             if (seasonalTexture.getParent().isEmpty()) {
    //                                 List<SeasonalTexture> seasonalTextures = ExtraModelManager.SEASONAL_TEXTURE_HASH_MAP.computeIfAbsent(
    //                                         Identifier.withPrefix("block/"), (xx) -> new ArrayList<>());
    //                                 seasonalTextures.add(seasonalTexture);
    //                             } else {
    //                                 for (Identifier location : seasonalTexture.getParent()) {
    //                                     List<SeasonalTexture> seasonalTextures = ExtraModelManager.SEASONAL_TEXTURE_HASH_MAP.computeIfAbsent(
    //                                             location, (xx) -> new ArrayList<>());
    //                                     seasonalTextures.add(seasonalTexture);
    //                                 }
    //                             }
    //                         }
    //                 );
    //             })
    //     ).thenCompose(ignored -> original);
    // }


}

package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.client.model.MyResolver;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    //@Shadow
    // protected abstract void registerModelAndLoadDependencies(ModelIdentifier modelLocation, UnbakedModel model);
    //
    //
    //@Inject(at = {@At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")}, method = {"<init>"})
    // private void eclipticseasons$register_snowy_models(BlockColors blockColors,
    //                                                   ProfilerFiller profilerFiller,
    //                                                   Map<Identifier, BlockModel> modelResources,
    //                                                   Map<Identifier, List<BlockStateModelLoader.LoadedJson>> blockStateResources,
    //                                                   CallbackInfo ci) {
    //    ExtraModelManager.registerExtraSnowyModels(this::registerModelAndLoadDependencies);
    //}
    //
    // @Inject(at = {@At(value = "RETURN")}, method = {"loadBlockModel"}, cancellable = true)
    // private void eclipticseasons$loadBlockModel_remapping(Identifier location,
    //                                                       CallbackInfoReturnable<BlockModel> cir) {
    //    BlockModel blockModel = ExtraModelManager.remappingSeasonTextures(location, cir.getReturnValue());
    //    if (blockModel != null) {
    //        cir.setReturnValue(blockModel);
    //    }
    // }

    @Inject(at = {@At(value = "TAIL")}, method = {"<init>(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;Lnet/neoforged/neoforge/client/entity/animation/json/AnimationLoader$PendingAnimations;)V"})
    private void eclipticseasons$register_snowy_models(
            EntityModelSet entityModelSet,
            SpriteGetter sprites,
            PlayerSkinRenderCache playerSkinRenderCache,
            Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels,
            Map<Identifier, ClientItem> clientInfos,
            Map<Identifier, ResolvedModel> resolvedModels,
            ResolvedModel missingModel,
            StandaloneModelLoader.LoadedModels standaloneModels,
            AnimationLoader.PendingAnimations pendingAnimations,
            CallbackInfo ci) {
        MyResolver.INSTANCE.setBlockState(Blocks.AIR.defaultBlockState());
        unbakedBlockStateModels.forEach(
                (blockState, unbakedRoot) -> {
                    unbakedRoot.resolveDependencies(MyResolver.INSTANCE.setBlockState(blockState));
                }
        );
        MyResolver.INSTANCE.setBlockState(Blocks.AIR.defaultBlockState());
    }

}

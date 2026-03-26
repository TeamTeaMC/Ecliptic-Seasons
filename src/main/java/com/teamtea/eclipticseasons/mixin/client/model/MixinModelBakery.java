package com.teamtea.eclipticseasons.mixin.client.model;


import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    //@Shadow
    //protected abstract void registerModelAndLoadDependencies(ModelIdentifier modelLocation, UnbakedModel model);
    //
    //
    //@Inject(at = {@At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")}, method = {"<init>"})
    //private void eclipticseasons$register_snowy_models(BlockColors blockColors,
    //                                                   ProfilerFiller profilerFiller,
    //                                                   Map<Identifier, BlockModel> modelResources,
    //                                                   Map<Identifier, List<BlockStateModelLoader.LoadedJson>> blockStateResources,
    //                                                   CallbackInfo ci) {
    //    ExtraModelManager.registerExtraSnowyModels(this::registerModelAndLoadDependencies);
    //}
    //
    //@Inject(at = {@At(value = "RETURN")}, method = {"loadBlockModel"}, cancellable = true)
    //private void eclipticseasons$loadBlockModel_remapping(Identifier location,
    //                                            CallbackInfoReturnable<BlockModel> cir) {
    //    BlockModel blockModel = ExtraModelManager.remappingSeasonTextures(location, cir.getReturnValue());
    //    if (blockModel != null) {
    //        cir.setReturnValue(blockModel);
    //    }
    //}

}

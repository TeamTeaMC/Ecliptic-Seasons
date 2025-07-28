package com.teamtea.eclipticseasons.mixin.client.model;


import net.minecraft.client.resources.model.MultiPartBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiPartBakedModel.Builder.class)
public abstract class MixinMultiPart {

    // @Unique
    // boolean eclipticseasons$shouldReplace = false;
    //
    // @Inject(method = "add", at = @At("HEAD"))
    // private void eclipticseasons$bake(Predicate<BlockState> pPredicate, BakedModel pModel, CallbackInfo ci) {
    //     if (!eclipticseasons$shouldReplace) {
    //         if (pModel instanceof SeasonBiomeGoingModel<?>) {
    //             eclipticseasons$shouldReplace = true;
    //         }
    //     }
    // }
    //
    // @Inject(method = "build", at = @At("RETURN"), cancellable = true)
    // private void eclipticseasons$bake(CallbackInfoReturnable<BakedModel> cir) {
    //     if (eclipticseasons$shouldReplace) {
    //         BakedModel returnValue = cir.getReturnValue();
    //         if (returnValue instanceof MultiPartBakedModel multiPartBakedModel) {
    //             // cir.setReturnValue(new SeasonMultiPartModel<>(multiPartBakedModel));
    //         }
    //     }
    // }
}



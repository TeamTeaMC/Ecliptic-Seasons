package com.teamtea.eclipticseasons.mixin.client.model;


import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.teamtea.eclipticseasons.client.model.SeasonBiomeGoingModel;
import com.teamtea.eclipticseasons.client.model.SeasonMultiPartModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(MultiPartBakedModel.Builder.class)
public abstract class MixinMultiPart {

    @Unique
    boolean eclipticseasons$shouldReplace = false;

    @Inject(method = "add", at = @At("HEAD"))
    private void eclipticseasons$bake(Predicate<BlockState> pPredicate, BakedModel pModel, CallbackInfo ci) {
        if (!eclipticseasons$shouldReplace) {
            if (pModel instanceof SeasonBiomeGoingModel<?>) {
                eclipticseasons$shouldReplace = true;
            }
        }
    }

    @Inject(method = "build", at = @At("RETURN"), cancellable = true)
    private void eclipticseasons$bake(CallbackInfoReturnable<BakedModel> cir) {
        if (eclipticseasons$shouldReplace) {
            BakedModel returnValue = cir.getReturnValue();
            if (returnValue instanceof MultiPartBakedModel multiPartBakedModel) {
                cir.setReturnValue(new SeasonMultiPartModel<>(multiPartBakedModel));
            }
        }
    }
}



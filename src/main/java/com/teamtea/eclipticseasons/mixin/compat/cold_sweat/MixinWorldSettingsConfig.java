package com.teamtea.eclipticseasons.mixin.compat.cold_sweat;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.momosoftworks.coldsweat.config.spec.WorldSettingsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WorldSettingsConfig.class})
public abstract class MixinWorldSettingsConfig {

    // @WrapOperation(
    //         remap = false,
    //         method = "<clinit>",
    //         at = @At(value = "INVOKE", target = "Lcom/momosoftworks/coldsweat/util/compat/CompatManager;isSereneSeasonsLoaded()Z")
    // )
    // private static boolean ecliptic$clinit(Operation<Boolean> original) {
    //     return true;
    // }
}

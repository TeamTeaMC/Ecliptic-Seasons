package com.teamtea.eclipticseasons.mixin.compat.cold_sweat;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.momosoftworks.coldsweat.api.event.core.init.GatherDefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.UndergroundTempModifier;
import com.momosoftworks.coldsweat.api.util.Placement;
import com.momosoftworks.coldsweat.config.ConfigSettings;
import com.momosoftworks.coldsweat.config.spec.WorldSettingsConfig;
import com.momosoftworks.coldsweat.util.serialization.DynamicHolder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ConfigSettings.class})
public abstract class MixinConfigSettings {

    // @WrapOperation(
    //         remap = false,
    //         method = "<clinit>",
    //         at = @At(value = "INVOKE", target = "Lcom/momosoftworks/coldsweat/util/compat/CompatManager;isSereneSeasonsLoaded()Z")
    // )
    // private static boolean ecliptic$clinit(Operation<Boolean> original) {
    //     return true;
    // }
}

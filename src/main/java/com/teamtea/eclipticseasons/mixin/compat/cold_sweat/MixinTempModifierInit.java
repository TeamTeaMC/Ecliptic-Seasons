package com.teamtea.eclipticseasons.mixin.compat.cold_sweat;


import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.core.init.TempModifierInit;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.cold_sweat.ESTempModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TempModifierInit.class})
public abstract class MixinTempModifierInit {


    @Inject(
            remap = false,
            method = "registerTempModifiers",
            at = @At(value = "HEAD")
    )
    private static void ecliptic$registerTempModifiers(TempModifierRegisterEvent event,CallbackInfo ci) {
        event.register(EclipticSeasons.rl( "season"), ESTempModifier::new);
    }
}

package com.teamtea.eclipticseasons.mixin.compat.cold_sweat;


import com.momosoftworks.coldsweat.api.event.core.init.GatherDefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.UndergroundTempModifier;
import com.momosoftworks.coldsweat.api.util.Placement;
import com.momosoftworks.coldsweat.common.capability.handler.EntityTempManager;
import com.momosoftworks.coldsweat.core.init.TempModifierInit;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.cold_sweat.ESTempModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityTempManager.class})
public abstract class MixinEntityTempManager {


    @Inject(
            remap = false,
            method = "defineDefaultModifiers",
            at = @At(value = "HEAD")
    )
    private static void ecliptic$defineDefaultModifiers(GatherDefaultTempModifiersEvent event, CallbackInfo ci) {
        event.addModifierById(EclipticSeasons.rl( "season"),
                mod -> mod.tickRate(60),
                Placement.Duplicates.BY_CLASS,
                Placement.of(Placement.Mode.BEFORE, Placement.Order.FIRST, mod2 -> mod2 instanceof UndergroundTempModifier));
    }
}

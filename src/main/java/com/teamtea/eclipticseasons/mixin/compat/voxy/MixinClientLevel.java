package com.teamtea.eclipticseasons.mixin.compat.voxy;


import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;


@Mixin({ClientLevel.class})
public abstract class MixinClientLevel  {

    @Inject(at = {@At("HEAD")}, method = {"tick"})
    public void eclipticseasons$tick_refresh_voxy(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        VoxyTool.tryUpdate();
    }
}

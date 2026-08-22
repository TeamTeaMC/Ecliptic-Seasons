package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.client.VoxyClientTool;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinClientLevel {

    @Inject(method = "tick", at = @At("HEAD"))
    private void eclipticseasons$tickVoxyRefresh(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        VoxyClientTool.forceReloadAll();
    }
}

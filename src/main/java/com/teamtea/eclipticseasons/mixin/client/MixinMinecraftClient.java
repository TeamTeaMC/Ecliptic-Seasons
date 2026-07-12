package com.teamtea.eclipticseasons.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Shadow
    @Final
    private Window window;

    @Inject(
            method = "resizeGui",
            at = @At("RETURN")
    )
    private void eclipticseasons$resize(CallbackInfo ci) {
        //FogRenderer.INSTANCE.resize(window.getWidth(), window.getHeight());
    }
    @Inject(
            method = "setLevel",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$setLevel(ClientLevel level, CallbackInfo ci) {
        if (level != null) {
            ESEventHook.onSeasonalLevelLoad(level);
        }
    }
}

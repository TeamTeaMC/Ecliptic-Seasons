package com.teamtea.eclipticseasons.mixin.client;


import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {


    @Inject(at = {@At("HEAD")}, method = {"render"})
    private void eclipticseasons$init(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci) {
        // ClientRenderer.applyEffect((GameRenderer)(Object)this,EFFECTS,this.minecraft.player);
    }
}

package com.teamtea.eclipticseasons.mixin.client.render;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {


    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract void loadEffect(ResourceLocation pResourceLocation);

    @Shadow @Final private static ResourceLocation[] EFFECTS;

    @Inject(at = {@At("HEAD")}, method = {"render"})
    private void eclipticseasons$init(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci) {
        // ClientRenderer.applyEffect((GameRenderer)(Object)this,EFFECTS,this.minecraft.player);
    }
}

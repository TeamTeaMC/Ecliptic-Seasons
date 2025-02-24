package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.render.ClientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

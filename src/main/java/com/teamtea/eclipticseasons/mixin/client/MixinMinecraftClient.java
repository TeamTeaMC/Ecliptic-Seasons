package com.teamtea.eclipticseasons.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.teamtea.eclipticseasons.client.render.FogRenderer;
import net.minecraft.client.Minecraft;
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

    /*
    * 因为没有 resize 事件
    * 所以先 mixin 手动调用一下 FogRenderer 的 resize
    * */
    @Inject(
            method = "resizeDisplay",
            at = @At("RETURN")
    )
    private void eclipticseasons$resize(CallbackInfo ci) {
        FogRenderer.INSTANCE.resize(window.getWidth(), window.getHeight());
    }

}

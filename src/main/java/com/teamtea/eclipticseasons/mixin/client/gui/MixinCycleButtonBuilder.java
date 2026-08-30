package com.teamtea.eclipticseasons.mixin.client.gui;

import com.teamtea.eclipticseasons.client.gui.screen.widget.CustomButtonSprites;
import com.teamtea.eclipticseasons.client.gui.screen.widget.CycleButtonBuilderSprites;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CycleButton.Builder.class)
public abstract class MixinCycleButtonBuilder<T> implements CycleButtonBuilderSprites {

    @Unique
    @Nullable
    protected WidgetSprites eclipticseasons$sprites;

    @Override
    public void eclipticseasons$setSprites(WidgetSprites sprites) {
        eclipticseasons$sprites = sprites;
    }

    @Inject(
            method = "create(IIIILnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/CycleButton$OnValueChange;)Lnet/minecraft/client/gui/components/CycleButton;",
            at = @At("RETURN")
    )
    protected void eclipticseasons$applySprites(
            int x,
            int y,
            int width,
            int height,
            Component name,
            CycleButton.OnValueChange<T> onValueChange,
            CallbackInfoReturnable<CycleButton<T>> cir
    ) {
        if (eclipticseasons$sprites != null) {
            ((CustomButtonSprites) cir.getReturnValue())
                    .eclipticseasons$setSprites(eclipticseasons$sprites);
        }
    }
}
package com.teamtea.eclipticseasons.mixin.client.gui;

import com.teamtea.eclipticseasons.client.gui.screen.widget.CustomButtonSprites;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton implements CustomButtonSprites {

    @Unique
    @Nullable
    protected WidgetSprites eclipticseasons$overrideSprites;

    @Override
    public void eclipticseasons$setSprites(WidgetSprites sprites) {
        eclipticseasons$overrideSprites = sprites;
    }

    @Redirect(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/WidgetSprites;get(ZZ)Lnet/minecraft/resources/ResourceLocation;"
            )
    )
    protected ResourceLocation eclipticseasons$replaceSprites(
            WidgetSprites originalSprites,
            boolean active,
            boolean hoveredOrFocused
    ) {
        WidgetSprites sprites = eclipticseasons$overrideSprites == null
                ? originalSprites
                : eclipticseasons$overrideSprites;

        return sprites.get(active, hoveredOrFocused);
    }
}
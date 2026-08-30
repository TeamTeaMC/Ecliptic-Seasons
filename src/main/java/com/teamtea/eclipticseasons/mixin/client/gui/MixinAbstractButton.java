package com.teamtea.eclipticseasons.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.client.gui.screen.widget.CustomButtonSprites;
import com.teamtea.eclipticseasons.client.gui.screen.widget.WidgetSprites;
import com.teamtea.eclipticseasons.client.gui.screen.widget.WoodenButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton implements CustomButtonSprites {

    @Unique
    @Nullable
    protected WidgetSprites eclipticseasons$overrideSprites;

    @Override
    public void eclipticseasons$setSprites(WidgetSprites sprites) {
        eclipticseasons$overrideSprites = sprites;
    }

    @WrapOperation(
            method = "renderWidget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"
            )
    )
    protected void eclipticseasons$renderCustomSprite(
            GuiGraphics graphics,
            ResourceLocation originalTexture,
            int x,
            int y,
            int width,
            int height,
            int sliceWidth,
            int sliceHeight,
            int textureWidth,
            int textureHeight,
            int u,
            int v,
            Operation<Void> original
    ) {
        if (eclipticseasons$overrideSprites == null) {
            original.call(
                    graphics,
                    originalTexture,
                    x,
                    y,
                    width,
                    height,
                    sliceWidth,
                    sliceHeight,
                    textureWidth,
                    textureHeight,
                    u,
                    v
            );
            return;
        }

        AbstractButton button = (AbstractButton) (Object) this;
        ResourceLocation texture = eclipticseasons$overrideSprites.get(
                button.active,
                button.isHoveredOrFocused()
        );

        WoodenButtonWidget.renderHorizontalThreeSliced(
                graphics,
                texture,
                x,
                y,
                width,
                height,
                200,
                20,
                30,
                30
        );
    }
}
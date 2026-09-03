package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WoodenButtonWidget extends Button {

    @Setter
    protected boolean select;

    @Setter
    protected WidgetSprites overrideSprites;

    public WoodenButtonWidget(Builder builder) {
        super(builder);
    }

    protected WoodenButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component message,
            OnPress onPress,
            CreateNarration createNarration
    ) {
        super(x, y, width, height, message, onPress, createNarration);
    }

    public static WoodenButtonWidget simple(
            int width,
            Component message,
            OnPress onPress
    ) {
        return new WoodenButtonWidget(
                0,
                0,
                width,
                DEFAULT_HEIGHT,
                message,
                onPress,
                DEFAULT_NARRATION
        );
    }

    @Override
    protected void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        WidgetSprites sprites = overrideSprites != null
                ? overrideSprites
                : SpritesConstant.getClientSprites();

        ResourceLocation texture = sprites.get(
                active,
                isHoveredOrFocused() || select
        );

        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        renderHorizontalThreeSliced(
                graphics,
                texture,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                200,
                20,
                overrideSprites != null?30:12,
                overrideSprites != null?30:12
        );

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = getFGColor()
                | Mth.ceil(alpha * 255.0F) << 24;

        renderString(
                graphics,
                Minecraft.getInstance().font,
                color
        );
    }

    public static void renderHorizontalThreeSliced(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            int leftBorder,
            int rightBorder
    ) {
        int leftWidth = Math.min(leftBorder, width / 2);
        int rightWidth = Math.min(rightBorder, width - leftWidth);
        int centerWidth = Math.max(0, width - leftWidth - rightWidth);
        int centerTextureWidth = Math.max(
                0,
                textureWidth - leftBorder - rightBorder
        );

        if (leftWidth > 0) {
            graphics.blit(
                    texture,
                    x,
                    y,
                    leftWidth,
                    height,
                    0.0F,
                    0.0F,
                    leftBorder,
                    textureHeight,
                    textureWidth,
                    textureHeight
            );
        }

        if (centerWidth > 0 && centerTextureWidth > 0) {
            graphics.blit(
                    texture,
                    x + leftWidth,
                    y,
                    centerWidth,
                    height,
                    leftBorder,
                    0.0F,
                    centerTextureWidth,
                    textureHeight,
                    textureWidth,
                    textureHeight
            );
        }

        if (rightWidth > 0) {
            graphics.blit(
                    texture,
                    x + width - rightWidth,
                    y,
                    rightWidth,
                    height,
                    textureWidth - rightBorder,
                    0.0F,
                    rightBorder,
                    textureHeight,
                    textureWidth,
                    textureHeight
            );
        }
    }
}
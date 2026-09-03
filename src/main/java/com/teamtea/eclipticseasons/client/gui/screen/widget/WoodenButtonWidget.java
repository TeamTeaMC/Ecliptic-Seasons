package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

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

        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blitSprite(
                sprites.get(
                        active,
                        isHoveredOrFocused() || select
                ),
                getX(),
                getY(),
                getWidth(),
                getHeight()
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        renderString(
                graphics,
                Minecraft.getInstance().font,
                getFGColor()
        );
    }
}
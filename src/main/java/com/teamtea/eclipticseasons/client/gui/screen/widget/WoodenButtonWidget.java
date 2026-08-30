package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class WoodenButtonWidget extends Button.Plain {
    @Setter
    private boolean select;
    @Setter
    private WidgetSprites overrideSprites;

    public WoodenButtonWidget(Builder builder) {
        super(builder);
    }

    protected WoodenButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
    }

    public static WoodenButtonWidget simple(int width, Component message, OnPress onPress) {
        return new WoodenButtonWidget(0, 0, width, 20, message, onPress, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        WidgetSprites clientSprites = overrideSprites != null ? overrideSprites : ConfigEntry.CLIENT_SPRITES;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED,clientSprites.get(this.active, this.isHoveredOrFocused() || this.select), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }
}
//     @Override
//     protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
//         this.extractDefaultSprite(graphics);
//         this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
//     }
// }

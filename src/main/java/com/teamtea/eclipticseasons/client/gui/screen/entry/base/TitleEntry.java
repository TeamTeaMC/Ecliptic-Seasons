package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class TitleEntry extends ConfigEntry {
    public TitleEntry(String text) {
        super(text);
    }

    @Override
    public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
        Component title = Component.literal("◆ ")
                .withColor(0x8FBF83)
                .append(this.label.copy().withColor(0xE8E8E8));
        // return FocusableTextWidget.builder(title,screen.getFont())
        //         .backgroundFill(FocusableTextWidget.BackgroundFill.ALWAYS)
        //         .build();
        TittleStringWidget tittleStringWidget = new TittleStringWidget(title, screen.getFont());
        tittleStringWidget.setHeight(24);
        return tittleStringWidget;
    }

    @Override
    public int getColumn() {
        return 2;
    }


    public static class TittleStringWidget extends StringWidget {
        public int textWidth;

        public TittleStringWidget(Component message, Font font) {
            super(message, font);
            textWidth = this.getFont().width(message);
        }

        @Override
        public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
            int x = getX();
            int lineY = getY() + getHeight() / 2 + getFont().lineHeight / 2;
            graphics.fill(x - 5, lineY, x + this.textWidth + 5, lineY + 1, 0x668FBF83);
        }
    }
}

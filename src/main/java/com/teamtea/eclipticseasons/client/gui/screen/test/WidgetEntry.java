package com.teamtea.eclipticseasons.client.gui.screen.test;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class WidgetEntry extends ContainerObjectSelectionList.Entry<WidgetEntry> {
    private final AbstractWidget[] widgets;
    private final List<? extends AbstractWidget> children;

    public WidgetEntry(AbstractWidget... widgets) {
        this.widgets = widgets;
        this.children = Arrays.asList(widgets);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        for (int i = 0; i < widgets.length && i < 2; i++) {
            AbstractWidget widget = widgets[i];
            if (widget instanceof StringWidget) {
                widget.setX(left + (width - widget.getWidth()) / 2 );
                widget.setY(top+widget.getHeight()/2+2);
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
            } else {
                widget.setX(left + width / 2 - widget.getWidth() * (1 - i) - (i==0?2:-2));
                widget.setY(top);
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        return this.children;
    }
}
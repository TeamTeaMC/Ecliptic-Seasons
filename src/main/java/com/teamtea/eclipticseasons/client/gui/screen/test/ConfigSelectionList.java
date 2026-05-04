package com.teamtea.eclipticseasons.client.gui.screen.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;

public class ConfigSelectionList extends ContainerObjectSelectionList<WidgetEntry> {
    public ConfigSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    private AbstractWidget last;

    public void addWidget(AbstractWidget widget) {
        if (widget instanceof StringWidget)
            this.addEntry(new WidgetEntry(widget));
        else if (last == null)
            last = widget;
        else {
            this.addEntry(new WidgetEntry(last, widget));
            last = null;
        }
    }

    public void end() {
        if (last != null) {
            this.addEntry(new WidgetEntry(last));
            last = null;
        }
    }


    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.width - 6;
    }
}




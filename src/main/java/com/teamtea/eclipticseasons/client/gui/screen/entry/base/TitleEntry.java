package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class TitleEntry extends ConfigEntry {
    public TitleEntry(String text) {
        super(text);
    }

    @Override
    public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
        return new StringWidget(Component.empty().append(this.label).withStyle(ChatFormatting.UNDERLINE), screen.getFont());
    }

    @Override
    public int getColumn() {
        return 2;
    }
}

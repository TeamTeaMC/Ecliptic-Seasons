package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class CallbackEntry extends ConfigEntry {
    CycleButton.OnValueChange<Boolean> consumer;
    boolean base;

    public CallbackEntry(String text, Boolean base, CycleButton.OnValueChange<Boolean> consumer) {
        super(text);
        this.consumer = consumer;
        this.base = base;
    }

    @Override
    public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(base)
                .create(x, y, width, 20, this.label, consumer);
        return booleanCycleButton;
    }
}

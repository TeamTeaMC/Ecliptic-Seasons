package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class CallbackEntry extends ConfigEntry {
    CycleButton.OnValueChange<Boolean> consumer;
    boolean base;
    protected final Component hoveredInfo;

    public CallbackEntry(String text, String hoveredText, Boolean base, CycleButton.OnValueChange<Boolean> consumer) {
        super(text);
        this.consumer = consumer;
        this.base = base;
        this.hoveredInfo = Component.translatable(hoveredText);
    }

    public CallbackEntry(String text, Boolean base, CycleButton.OnValueChange<Boolean> consumer) {
        this(text, "", base, consumer);
    }

    @Override
    public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(base)
                .create(x, y, width, 20, this.label, consumer);
        booleanCycleButton.setTooltip(Tooltip.create(label.copy().withStyle(ChatFormatting.BOLD).append("\n\n").append(hoveredInfo.copy().withStyle(style -> style.withBold(false)))));
        return booleanCycleButton;
    }
}

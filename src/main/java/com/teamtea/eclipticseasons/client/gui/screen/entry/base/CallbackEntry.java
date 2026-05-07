package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.BooleanSupplier;

public class CallbackEntry extends ConfigEntry {
    CycleButton.OnValueChange<Boolean> consumer;
    final BooleanSupplier base;
    protected final Component hoveredInfo;
    @Accessors(chain = true)
    @Setter
    protected ModConfigSpec.RestartType restartType = ModConfigSpec.RestartType.NONE;
    boolean nowValue;

    public CallbackEntry(String text, String hoveredText, BooleanSupplier base, CycleButton.OnValueChange<Boolean> consumer) {
        super(text);
        this.consumer = consumer;
        this.base = base;
        this.hoveredInfo = Component.translatable(hoveredText);
        this.nowValue = base.getAsBoolean();
    }

    @Override
    public boolean isValueChanged() {
        return base.getAsBoolean() != nowValue;
    }

    @Override
    public boolean shouldRestart(boolean inGame) {
        return switch (restartType) {
            case WORLD -> inGame;
            case GAME -> !inGame;
            default -> false;
        };
    }

    @Override
    public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(nowValue)
                .create(x, y, width, 20, this.label, (b, v) -> {
                    this.nowValue = v;
                    consumer.onValueChange(b, v);
                });
        booleanCycleButton.setTooltip(Tooltip.create(label.copy().withStyle(ChatFormatting.BOLD).append("\n\n").append(hoveredInfo.copy().withStyle(style -> style.withBold(false)))));
        return booleanCycleButton;
    }
}

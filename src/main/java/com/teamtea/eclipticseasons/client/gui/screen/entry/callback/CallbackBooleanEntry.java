package com.teamtea.eclipticseasons.client.gui.screen.entry.callback;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.CallbackEntry;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CallbackBooleanEntry extends CallbackEntry<Boolean> {
    public CallbackBooleanEntry(String text, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(text, getter::getAsBoolean, setter);
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        return buildBooleanButton(nowValue, syncType, x, y, width, (button, value) -> {
            nowValue = value;
            setter.accept(value);
        });
    }
}

package com.teamtea.eclipticseasons.client.gui.screen.entry.callback;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.CallbackEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CallbackBooleanEntry extends CallbackEntry<Boolean> {
    public CallbackBooleanEntry(String text, BooleanSupplier getter, Consumer<Boolean> setter,
                                BooleanSupplier defaultGetter) {
        super(text, getter::getAsBoolean, setter, defaultGetter::getAsBoolean);
    }

    @Override
    public LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        boolean value = getter.get();

        return buildResettableCycle(width, value, defaultGetter.get(), setter,
                onValueChange -> buildBooleanButton(value, syncType, x, y, width - 30, onValueChange));
    }
}

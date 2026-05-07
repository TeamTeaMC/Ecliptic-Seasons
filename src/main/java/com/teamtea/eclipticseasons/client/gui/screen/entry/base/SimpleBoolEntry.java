package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;


// for mixins options
public class SimpleBoolEntry extends ConfigEntry {
    private BooleanSupplier value;
    private final Consumer<Boolean> setter;
    protected final boolean oldValue;

    public SimpleBoolEntry(String name, BooleanSupplier value, Consumer<Boolean> setter) {
        super(name);
        this.value = value;
        this.setter = setter;
        this.oldValue = value.getAsBoolean();
    }

    @Override
    public boolean isValueChanged() {
        return value.getAsBoolean() != oldValue;
    }

    @Override
    public boolean shouldRestart(boolean inGame) {
        return true;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public LayoutElement build(ESModConfigScreen screen, int x, int y, int width) {
        return CycleButton.onOffBuilder(value.getAsBoolean())
                .create(x, y, width, 20, this.label, (button, newValue) -> {
                    this.value = () -> newValue;
                    this.setter.accept(newValue);
                });
    }
}

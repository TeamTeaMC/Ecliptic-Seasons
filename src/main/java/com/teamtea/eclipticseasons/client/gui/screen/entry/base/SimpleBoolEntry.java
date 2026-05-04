package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.function.Consumer;

public class SimpleBoolEntry extends ConfigEntry {
    private boolean value;
    private final Consumer<Boolean> setter;

    public SimpleBoolEntry(String name, boolean value, Consumer<Boolean> setter) {
        super(name);
        this.value = value;
        this.setter = setter;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public LayoutElement build(ESModConfigScreen screen, int x, int y, int width) {
        return CycleButton.onOffBuilder(value)
                .create(x, y, width, 20, this.label, (button, newValue) -> {
                    this.value = newValue;
                    this.setter.accept(newValue);
                });
    }
}

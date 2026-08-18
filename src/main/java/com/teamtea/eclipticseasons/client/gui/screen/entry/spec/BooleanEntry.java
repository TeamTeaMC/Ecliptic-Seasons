package com.teamtea.eclipticseasons.client.gui.screen.entry.spec;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BooleanEntry extends SpecEntry<Boolean> {
    public BooleanEntry(ModConfigSpec.BooleanValue spec) {
        super(spec);
    }

    @Override
    public int getColumn() {
        return 2;
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        return buildLabelAndControl(screen, label, buildModConfigSpec(screen, x, y, width), width);
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        return buildBooleanButton(spec.get(), syncType, x, y, width, (button, value) -> spec.set(value));
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

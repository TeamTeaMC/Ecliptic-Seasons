package com.teamtea.eclipticseasons.client.gui.screen.entry.spec;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.awt.*;
import java.util.Objects;

public class BooleanEntry extends SpecEntry<Boolean> {
    public BooleanEntry(ForgeConfigSpec.BooleanValue spec) {
        super(spec);
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        return buildLabelAndControl(screen, getLabel(screen), buildModConfigSpec(screen, x, y, width), width);
    }

    @Override
    public LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        return buildResettableCycle(width, spec.get(), spec.getDefault(), spec::set,
                onValueChange -> {
                    CycleButton.Builder<Boolean> booleanBuilder = CycleButton.onOffBuilder(spec.get()).displayOnlyValue();
                    applyClientSprite(booleanBuilder, getSyncType());
                    return booleanBuilder
                            .create(0, 0, width - 30, 20, label, onValueChange);
                });
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

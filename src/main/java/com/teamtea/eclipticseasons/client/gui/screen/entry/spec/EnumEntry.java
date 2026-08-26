package com.teamtea.eclipticseasons.client.gui.screen.entry.spec;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

public class EnumEntry<T extends Enum<T>> extends SpecEntry<T> {
    private final T[] values;

    public EnumEntry(ModConfigSpec.EnumValue<T> spec) {
        super(spec);
        this.values = spec.get().getDeclaringClass().getEnumConstants();
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        return buildLabelAndControl(screen, getLabel(screen), buildModConfigSpec(screen, x, y, width), width);
    }

    @Override
    public LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton.Builder<T> builder = CycleButton.<T>builder(value -> value instanceof ITranslatable it
                ? it.getTranslation() : Component.literal(value.name())).displayOnlyValue();
        applyClientSprite(builder, syncType);

        return buildResettableCycle(width, spec.get(), spec.getDefault(), spec::set,
                onValueChange -> builder.withValues(values).withTooltip(this::getTooltipSupplier)
                        .create(x, y, width - 30, 20, label, onValueChange));
    }


    @Override
    public int getPosition() {
        return 0;
    }
}

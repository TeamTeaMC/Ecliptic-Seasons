package com.teamtea.eclipticseasons.client.gui.screen.entry.callback;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.CallbackEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CallbackEnumEntry<T extends Enum<T>> extends CallbackEntry<T> {
    private final List<T> values;
    private final Function<T, Component> valueDisplay;

    public CallbackEnumEntry(String text, Supplier<T> getter, Consumer<T> setter, Supplier<T> defaultGetter,
                             List<T> values, Function<T, Component> valueDisplay) {
        super(text, getter, setter, defaultGetter);
        this.values = values;
        this.valueDisplay = valueDisplay;
    }

    public CallbackEnumEntry(String text, Supplier<T> getter, Consumer<T> setter, Supplier<T> defaultGetter,
                             List<T> values) {
        this(text, getter, setter, defaultGetter, values,
                value -> value instanceof ITranslatable it
                        ? it.getTranslation() : Component.literal(value.name()));
    }

    @Override
    protected <E> Tooltip getTooltipSupplier(E value) {
        if (value instanceof ITranslatable it) {
            Component tooltip = it.getDescription();
            if (tooltip != null) {
                MutableComponent message = this.label.copy().append(": ").append(it.getTranslation())
                        .withStyle(ChatFormatting.BOLD)
                        .append(Component.literal("\n\n").append(tooltip).withStyle(style -> style.withBold(false)));
                message.append("\n\n");
                Object[] enumConstants = value.getClass().getEnumConstants();
                for (int i = 0, enumConstantsLength = enumConstants.length; i < enumConstantsLength; i++) {
                    Object enumConstant = enumConstants[i];
                    MutableComponent append = message.append(((ITranslatable) enumConstant).getTranslation());
                    if (i < enumConstantsLength - 1)
                        append.append(" > ");
                }
                return Tooltip.create(message);
            } else {
                return super.getTooltipSupplier(value);
            }
        }
        return super.getTooltipSupplier(value);
    }

    @Override
    public LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        T value = getter.get();
        CycleButton.Builder<T> builder = CycleButton.builder(valueDisplay).withInitialValue(value).displayOnlyValue();
        applyClientSprite(builder, syncType);

        return buildResettableCycle(width, value, defaultGetter.get(), setter,
                onValueChange -> builder.withValues(values).withTooltip(this::getTooltipSupplier)
                        .create(x, y, width - 30, 20, Component.empty(), onValueChange));
    }

}

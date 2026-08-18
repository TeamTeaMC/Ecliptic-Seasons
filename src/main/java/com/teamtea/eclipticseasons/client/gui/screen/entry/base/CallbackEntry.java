package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An entry whose value is read and written through {@link Supplier} / {@link Consumer},
 * rather than a {@link ModConfigSpec.ConfigValue}.
 */
public abstract class CallbackEntry<T> extends ConfigEntry {
    protected final Supplier<T> getter;
    protected final Consumer<T> setter;
    protected T nowValue;

    protected final String text;

    @Accessors(chain = true)
    @Setter
    protected ModConfigSpec.RestartType restartType = ModConfigSpec.RestartType.NONE;

    @Accessors(chain = true)
    @Setter
    @Getter
    protected SyncType syncType = SyncType.COMMON;

    protected CallbackEntry(String text, Supplier<T> getter, Consumer<T> setter) {
        super(text);
        this.text = text;
        this.getter = getter;
        this.setter = setter;
        this.nowValue = getter.get();
    }

    @Override
    public String getSearchText() {
        return label.getString() + " " + text;
    }

    @Override
    public boolean isValueChanged() {
        return !Objects.equals(getter.get(), nowValue);
    }

    @Override
    public boolean shouldRestart(boolean inGame) {
        return switch (restartType) {
            case WORLD -> inGame;
            case GAME -> true;
            default -> false;
        };
    }

    @Override
    public int getColumn() {
        return 2;
    }

    @Override
    public LayoutElement build(ESModConfigScreen screen, int x, int y, int width) {
        LayoutElement layoutElement = buildLayout(screen, x, y, width);

        MutableComponent comment = buildTooltipComment(text + ".tooltip", Component.empty());
        applyTooltip(layoutElement, label, comment);
        return layoutElement;
    }

    @Override
    protected <E> Tooltip getTooltipSupplier(E value) {
        return Tooltip.create(label.copy().withStyle(ChatFormatting.BOLD)
                .append(buildTooltipComment(text + ".tooltip", Component.empty()).copy().withStyle(style -> style.withBold(false))));
    }

    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        return buildLabelAndControl(screen, label, buildModConfigSpec(screen, x, y, width), width);
    }

    public abstract AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width);
}

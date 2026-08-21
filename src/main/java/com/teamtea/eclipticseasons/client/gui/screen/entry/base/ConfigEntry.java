package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public abstract class ConfigEntry {
    protected final Component label;

    public ConfigEntry(String translationKey) {
        this.label = Component.translatable(translationKey);
    }

    public boolean isValueChanged() {
        return false;
    }

    public boolean shouldRestart(boolean inGame) {
        return false;
    }

    public int getPosition() {
        return 10;
    }

    public int getColumn() {
        return 2;
    }

    public SyncType getSyncType() {
        return SyncType.NONE;
    }

    public String getSearchText() {
        return label.getString();
    }

    public abstract LayoutElement build(ESModConfigScreen screen, int x, int y, int width);

    public static MultiLineTextWidget getMultiLineTextWidget(Component label, ESModConfigScreen screen, int width) {
        Component label1 = label;
        int width1 = screen.getFont().width(label1.getString());
        if (width1 > width - 20) {
            float cut = (width - 20) / (float) width1;
            String substring = label1.getString().substring(0, (int) (cut * label1.getString().length()));
            label1 = Component.literal(substring + "...");
        }
        MultiLineTextWidget multiLineTextWidget = new MultiLineTextWidget(label1, screen.getFont());
        multiLineTextWidget.setHeight(20);
        multiLineTextWidget.setWidth(width);
        return multiLineTextWidget;
    }

    protected static LayoutElement buildLabelAndControl(ESModConfigScreen screen, Component label, AbstractWidget control, int width) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
        StringWidget labelWidget = new StringWidget(trimText(screen.getFont(), label, width - 10), screen.getFont());
        labelWidget.alignLeft();
        labelWidget.setWidth(width + 4);
        labelWidget.setHeight(20);
        control.setWidth(width);
        helper.addChild(labelWidget);
        helper.addChild(control);
        return gridLayout;
    }

    protected static Component trimText(Font font, Component text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int availableWidth = Math.max(0, maxWidth - font.width(ellipsis));

        return Component.literal(
                font.plainSubstrByWidth(text.getString(), availableWidth) + ellipsis
        ).withStyle(text.getStyle());
    }

    protected static <T> void applyClientSprite(CycleButton.Builder<T> builder, SyncType syncType) {
        if (syncType == SyncType.CLIENT) {
            // builder.withSprite((cycleButton, value) ->
            //         CLIENT_SPRITES.get(cycleButton.isActive(), cycleButton.isHoveredOrFocused()));
        }
    }

    protected <E> Tooltip getTooltipSupplier(E value) {
        return null;
    }

    protected CycleButton<Boolean> buildBooleanButton(
            boolean value,
            SyncType syncType,
            int x,
            int y,
            int width,
            CycleButton.OnValueChange<Boolean> onValueChange
    ) {
        CycleButton.Builder<Boolean> builder = CycleButton.onOffBuilder(value)
                .displayOnlyValue()
                // .displayState(CycleButton.DisplayState.VALUE)
                .withTooltip(this::getTooltipSupplier);
        applyClientSprite(builder, syncType);
        return builder.create(x, y, width, 20, Component.empty(), onValueChange);
    }

    protected static void applyTooltip(LayoutElement layoutElement, Component title, Component comment) {
        layoutElement.visitWidgets(aw -> {
            if (aw.tooltip == null) {
                aw.setTooltip(Tooltip.create(title.copy().withStyle(ChatFormatting.BOLD)
                        .append(comment.copy().withStyle(style -> style.withBold(false)))));
            }
        });
    }

    protected static MutableComponent buildTooltipComment(String commentKey, Component fallback) {
        return Component.literal("\n\n")
                .withStyle(Style.EMPTY.withBold(false))
                .append(Language.getInstance().has(commentKey)
                        ? Component.translatable(commentKey)
                        : fallback);
    }

}

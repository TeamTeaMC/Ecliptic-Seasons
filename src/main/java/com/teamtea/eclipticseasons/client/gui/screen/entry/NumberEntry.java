package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.config.util.SpecUtil;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class NumberEntry<T extends Number> extends ConfigEntry.SpecEntry<T> {
    public NumberEntry(ForgeConfigSpec.ConfigValue<T> spec) {
        super(spec);
    }

    @Override
    public int getColumn() {
        return 2;
    }

    @Override
    public int getPosition() {
        return 5;
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        // LinearLayout linearLayout = new LinearLayout(x, y, LinearLayout.Orientation.HORIZONTAL);
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
        StringWidget s = new StringWidget(label, screen.getFont());
        s.setWidth(width + 4);
        s.setHeight(20);
        AbstractWidget abstractWidget = buildModConfigSpec(screen, x, y, width);
        abstractWidget.setWidth(width);
        helper.addChild(s);
        helper.addChild(abstractWidget);

        return gridLayout;
    }


    public static class TextNumberEntry<T extends Number> extends NumberEntry<T> {
        public TextNumberEntry(ForgeConfigSpec.ConfigValue<T> spec) {
            super(spec);
        }

        @Override
        public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
            final SpecUtil.Range<?> range = SpecUtil.getRange(SpecUtil.getSpec(spec));
            final EditBox box = new EditBox(screen.getFont(), 0, 0, width, Button.DEFAULT_HEIGHT, Component.empty());
            box.setEditable(true);
            box.setValue(spec.get().toString());

            box.setResponder(value -> {
                try {
                    Number n;
                    if (spec.getDefault() instanceof Integer) n = Integer.parseInt(value);
                    else n = Double.parseDouble(value);

                    if (range != null && !range.test(n)) {
                        box.setTextColor(0xFFFF0000);
                        return;
                    }
                    spec.set((T) n);
                    box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                } catch (Exception e) {
                    box.setTextColor(0xFFFF0000);
                }
            });
            return box;
        }
    }

    public static class IntSliderEntry extends NumberEntry<Integer> {
        public IntSliderEntry(ForgeConfigSpec.IntValue spec) {
            super(spec);
        }

        @Override
        public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
            SpecUtil.Range<Integer> range = SpecUtil.getRange(SpecUtil.getSpec(spec));
            int min = range != null ? range.getMin() : 0;
            int max = range != null ? range.getMax() : Integer.MAX_VALUE;

            return new OptionInstance<>(
                    spec.getPath().get(spec.getPath().size() - 1),
                    OptionInstance.noTooltip(),
                    (caption, value) -> Component.literal(value.toString()),
                    new OptionInstance.IntRange(min, max),
                    spec.get(),
                    spec::set
            ).createButton(Minecraft.getInstance().options, 0, 0, width);
        }
    }

    public static class DoubleSliderEntry extends NumberEntry<Double> {

        public DoubleSliderEntry(ForgeConfigSpec.DoubleValue spec) {
            super(spec);
        }

        @Override
        public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
            return new OptionInstance<>(
                    spec.getPath().get(spec.getPath().size() - 1),
                    OptionInstance.noTooltip(),
                    (caption, value) -> Component.literal(String.format("%.2f", value)),
                    OptionInstance.UnitDouble.INSTANCE,
                    spec.get(),
                    spec::set
            ).createButton(Minecraft.getInstance().options, 0, 0, width);
        }
    }
}

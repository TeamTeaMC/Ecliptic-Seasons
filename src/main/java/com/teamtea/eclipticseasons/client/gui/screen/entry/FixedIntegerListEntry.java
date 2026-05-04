package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class FixedIntegerListEntry extends ConfigEntry.SpecEntry<List<? extends Integer>> {
    private final ModConfigSpec.Range<Integer> range;

    public FixedIntegerListEntry(ModConfigSpec.ConfigValue<List<? extends Integer>> spec, ModConfigSpec.Range<Integer> integerRange) {
        super(spec);
        this.range = integerRange;
    }

    public FixedIntegerListEntry(ModConfigSpec.ConfigValue<List<? extends Integer>> spec) {
        this(spec, ModConfigSpec.Range.of(1, 23999));
    }

    @Override
    public int getPosition() {
        return 20;
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(3).alignHorizontallyCenter();
        int columnSize = spec.getDefault().size() == 6 ? 3 : 1;
        GridLayout.RowHelper helper = gridLayout.createRowHelper(columnSize);
        helper.addChild(new StringWidget(Component.empty().append(label).withStyle(ChatFormatting.ITALIC), screen.getFont()), columnSize);
        List<Integer> integers = new ArrayList<>(spec.get());
        for (int i = 0; i < integers.size(); i++) {
            int possibleValue = integers.get(i);
            final EditBox box = new EditBox(screen.getFont(), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty());
            box.setWidth(width * 2 / 3);
            box.setEditable(true);
            box.setValue(possibleValue + "");
            int finalI = i;
            box.setResponder(value -> {
                try {
                    Integer n = Integer.parseInt(value);
                    if (range != null && !range.test(n)) {
                        box.setTextColor(0xFFFF0000);
                        return;
                    }
                    integers.set(finalI, n);
                    spec.set(integers);
                    box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                } catch (Exception e) {
                    box.setTextColor(0xFFFF0000);
                }
            });
            box.setTooltip(Tooltip.create(Component.literal("1 ~ 23999")));
            helper.addChild(box);
        }
        return gridLayout;
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        return null;
    }

    @Override
    public int getColumn() {
        return 2;
    }
}

package com.teamtea.eclipticseasons.client.gui.screen.entry.spec;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraftforge.common.ForgeConfigSpec;

public class BooleanEntry extends SpecEntry<Boolean> {
    public BooleanEntry(ForgeConfigSpec.BooleanValue spec) {
        super(spec);
    }

    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        return buildLabelAndControl(screen, getLabel(screen), buildModConfigSpec(screen, x, y, width), width);
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {

        CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(spec.get())
                .displayOnlyValue()
                .create(0, 0, width, 20, this.label, (button, value) -> spec.set(value));
        // booleanCycleButton.setTooltip(Tooltip.create(Component.translatable("eclipticseasons.configuration." + spec.getPath().get(spec.getPath().size()-1)).withStyle(ChatFormatting.BOLD).append(Component.translatable("\n\n" + SpecUtil.getSpec(spec).getComment() + "")).withStyle(ChatFormatting.RESET)));
        return booleanCycleButton;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

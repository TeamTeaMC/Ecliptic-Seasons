package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraftforge.common.ForgeConfigSpec;

public class BoolEntry extends ConfigEntry.SpecEntry<Boolean> {
    public BoolEntry(ForgeConfigSpec.BooleanValue spec) {
        super(spec);
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {

        CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(spec.get())
                .create(0, 0, width, 20, this.label, (button, value) -> spec.set(value));
        // booleanCycleButton.setTooltip(Tooltip.create(Component.translatable("eclipticseasons.configuration." + spec.getPath().get(spec.getPath().size()-1)).withStyle(ChatFormatting.BOLD).append(Component.translatable("\n\n" + SpecUtil.getSpec(spec).getComment() + "")).withStyle(ChatFormatting.RESET)));
        return booleanCycleButton;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

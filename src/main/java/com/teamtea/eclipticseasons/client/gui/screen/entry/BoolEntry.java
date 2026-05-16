package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BoolEntry extends ConfigEntry.SpecEntry<Boolean> {
    public BoolEntry(ModConfigSpec.BooleanValue spec) {
        super(spec);
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton.Builder<Boolean> booleanBuilder = CycleButton.onOffBuilder(spec.get());
        if (syncType == SyncType.CLIENT) {
            booleanBuilder.withSprite(
                    (cycleButton, aBoolean) ->
                    CLIENT_SPRITES.get(cycleButton.isActive(), cycleButton.isHoveredOrFocused()));
        }
        CycleButton<Boolean> booleanCycleButton = booleanBuilder
                .create(x, y, width, 20, this.label, (button, value) -> spec.set(value));
        return booleanCycleButton;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

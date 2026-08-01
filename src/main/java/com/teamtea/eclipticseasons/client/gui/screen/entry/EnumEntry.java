package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EnumEntry<T extends Enum<T>> extends ConfigEntry.SpecEntry<T> {
    private final T[] values;

    public EnumEntry(ModConfigSpec.EnumValue<T> spec) {
        super(spec);
        this.values = spec.get().getDeclaringClass().getEnumConstants();
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        CycleButton.Builder<T> builder = CycleButton.builder(
                value ->
                        value instanceof ITranslatable it ?
                                it.getTranslation() :
                                Component.literal(value.name()),
                spec.get()
        );

        if (syncType == SyncType.CLIENT) {
            builder.withSprite(
                    (cycleButton, value) ->
                            CLIENT_SPRITES.get(
                                    cycleButton.isActive(),
                                    cycleButton.isHoveredOrFocused()
                            )
            );
        }

        return builder
                .withValues(values)
                .create(x, y, width, 20, this.label,
                        (button, value) -> spec.set(value));
    }

    @Override
    public int getPosition() {
        return 0;
    }
}

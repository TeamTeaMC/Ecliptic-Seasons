package com.teamtea.eclipticseasons.client.gui.screen.config;

import net.minecraft.network.chat.Component;

public record ConfigScreenText(
        Component title,
        Component searchHint,
        Component noResult,
        Component classicScreen
) {
    public ConfigScreenText(Component title) {
        this(title,
                Component.translatable("eclipticseasons.options.search"),
                Component.translatable("eclipticseasons.options.search.no_result"),
                Component.translatable("eclipticseasons.options.configure_in_classic_screen")
        );
    }
}

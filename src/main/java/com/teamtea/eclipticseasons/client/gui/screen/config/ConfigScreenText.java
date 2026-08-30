package com.teamtea.eclipticseasons.client.gui.screen.config;

import net.minecraft.network.chat.Component;

public record ConfigScreenText(
        Component title,
        Component searchHint,
        Component noResult,
        Component classicScreen
) {
}

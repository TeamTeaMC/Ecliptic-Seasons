package com.teamtea.eclipticseasons.client.gui.screen.tab;

import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public record Tab(Component name, Map<Component, List<ConfigEntry>> configShown) {
}

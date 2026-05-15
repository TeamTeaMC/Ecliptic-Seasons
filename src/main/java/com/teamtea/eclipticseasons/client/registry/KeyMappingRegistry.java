package com.teamtea.eclipticseasons.client.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.KeyMapping;

public class KeyMappingRegistry {
    public static final KeyMapping DEBUG_KEY = new KeyMapping(
            EclipticSeasons.rl("main/debug").toLanguageKey("keys"),
            InputConstants.KEY_NUMPAD5, EclipticSeasons.rl("main").toLanguageKey("key.category")
    );
    public static final KeyMapping DEBUG_KEY_1 = new KeyMapping(
            EclipticSeasons.rl("main/debug_1").toLanguageKey("keys"),
            InputConstants.KEY_LCONTROL, EclipticSeasons.rl("main").toLanguageKey("key.category")
    );
}

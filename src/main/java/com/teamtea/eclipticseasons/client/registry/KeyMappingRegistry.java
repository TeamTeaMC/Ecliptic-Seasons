package com.teamtea.eclipticseasons.client.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class KeyMappingRegistry {
    public static final KeyMapping.Category MAIN = new KeyMapping.Category(EclipticSeasons.rl("main"));
    public static final KeyMapping DEBUG_KEY = new KeyMapping(
            EclipticSeasons.rl("main/debug").toLanguageKey("keys"),
            ESKeyConflictContexts.DEBUG_CONTEXT, KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM, InputConstants.KEY_I, KeyMappingRegistry.MAIN
    );
}
